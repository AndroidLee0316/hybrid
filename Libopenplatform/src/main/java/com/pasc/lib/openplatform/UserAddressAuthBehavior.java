package com.pasc.lib.openplatform;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.pasc.lib.base.widget.LoadingDialog;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.net.resp.BaseRespThrowableObserver;
import com.pasc.lib.openplatform.address.AddressPresenter;
import com.pasc.lib.openplatform.address.AddressResp;
import com.pasc.lib.openplatform.bean.UserAuthContentBean;
import com.pasc.lib.openplatform.forthird.OpenPlatformForThird;
import com.pasc.lib.openplatform.network.OpenBiz;
import com.pasc.lib.openplatform.resp.ServiceInfoResp;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import com.pasc.lib.smtbrowser.entity.UserAuthBean;
import com.pasc.libopenplatform.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * 开放平台用户授权行为
 * create by wujianning385 on 2018/10/22.
 */
public abstract class UserAddressAuthBehavior implements BehaviorHandler, Serializable {
    /**
     * 是否已经发起请求状态
     */
    private boolean isServiceStatus;
    public static final int CODE_FAIL_DEFULT = -1;
    public static final int CODE_JSSDK_NOT_INIT = -10001;
    public static final int CODE_USER_REFUSE = -10002;
    public static final int CODE_USER_NOT_LOGIN = -10003;
    public static final int CODE_USER_NOT_CERTIFICATION = -10004;

    private LoadingDialog loadingDialog;
    private NativeResponse response;
    private CallBackFunction function;
    private UserAuthBean userAuthBean;
    protected Context context;
    private ArrayList<String> needAuthData;

    private ServiceInfoResp serviceInfoResp;

    /**
     * 获取到到地址列表
     */
    private List<AddressResp> addressRespList;

    /**
     * 用户到token
     */
    protected String mUserToken;

    private static final String AUTH_DATA_TYPE = "address";

    @Override
    public void handler(final Context context, String data, final CallBackFunction function,
                        final NativeResponse response) {

        try {
            if (InitJSSDKBehavior.initStatus != 0) {
                response.code = InitJSSDKBehavior.initStatus;
                if (InitJSSDKBehavior.initStatus == InitJSSDKBehavior.STATUS_UN_INIT){
                    response.message = context.getString(R.string.openplatform_user_un_init_jssdk);
                }
                function.onCallBack(new Gson().toJson(response));
                return;
            }
            Gson gson = new Gson();
            final UserAuthBean userAuth = gson.fromJson(data, UserAuthBean.class);
            this.context = context;
            this.function = function;
            this.response = response;
            this.userAuthBean = userAuth;
            if (!TextUtils.isEmpty(InitJSSDKBehavior.appId)){
                this.userAuthBean.appId = InitJSSDKBehavior.appId;
            }
            if (userAuthBean.userDataTypes==null){
                userAuthBean.userDataTypes = new ArrayList<>();
            }
            PascHybrid.getInstance().saveCallBackFunction(context.hashCode(), ConstantBehaviorName.USER_AUTH, function);
            getToken(context,userAuthBean,function,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getToken(final Context context, final UserAuthBean userAuthBean, final CallBackFunction function,
                         final NativeResponse response){
        PascOpenPlatform.getInstance().getOpenPlatformProvider().getUserToken(new IBizCallback() {
            @Override
            public void onLoginSuccess(String userToken) {
                mUserToken = userToken;
                if (null == userToken) {
                    response.code = CODE_USER_NOT_LOGIN;
                    response.message = "用户未登陆";
                    function.onCallBack(new Gson().toJson(response));
                    return;
                }

                PascOpenPlatform.getInstance().getOpenPlatformProvider().getCertification(context, new CertificationCallback() {
                    @Override
                    public void certification(boolean isCertification) {
                        if (!isCertification) {
                            response.code = CODE_USER_NOT_CERTIFICATION;
                            response.message = "用户未认证";
                            function.onCallBack(new Gson().toJson(response));
                            return;
                        }
                        isServiceStatus = false;
                        if (context instanceof PascWebviewActivity) {
                            if (((PascWebviewActivity) context).isFinishing()) {
                                isServiceStatus = true;
                            }
                        }
                        getAddressList(context, userAuthBean.appId, mUserToken, response,function);

                    }
                });

            }
        });
    }

    /**
     * 判断是否需要实名，是否需要弹授权框
     *
     * @param context
     * @param appId
     * @param token
     * @param response
     * @param function
     */
    public void getServiceInfo(final Context context, final String appId, final String token
            , final NativeResponse response, final CallBackFunction function) {

        OpenBiz.getServiceInfo(appId).subscribe(new Consumer<ServiceInfoResp>() {
            @Override
            public void accept(ServiceInfoResp infoResp) throws Exception {
                serviceInfoResp = infoResp;
                getServiceStatus(context, appId, response, function, token, infoResp);

            }
        }, new BaseRespThrowableObserver() {
            @Override
            public void onError(int code, String msg) {
                Log.e("openPlatformTag", msg);
                dismissLoading();
                response.code = CODE_FAIL_DEFULT;
                response.message = msg;
                function.onCallBack(new Gson().toJson(response));
            }
        });
        //disposables.add(disposable);
    }

    /**
     * 判断服务是否已实名
     *
     * @param context
     * @param appId
     * @param response
     * @param function
     * @param token
     * @param infoResp
     */
    private void getServiceStatus(final Context context, final String appId,
                                  final NativeResponse response, final CallBackFunction function
            , final String token, final ServiceInfoResp infoResp) {

        dismissLoading();

        if (!isServiceStatus) {
            isServiceStatus = true;
            needAuthData = new ArrayList<>();
            if ("1".equals(infoResp.userInfoGetType)){
                //获取用户信息统一返回，走之前的逻辑
            }else {
                //获取用户信息按需返回，需要根据返回的授权数据再决定是否跳转授权页
                if (infoResp.applyUserDataTypeInfo!=null) {
                    List<String> serviceTypeCodes = new ArrayList<>();
                    for (ServiceInfoResp.UserDataTypeInfo typeInfo : infoResp.applyUserDataTypeInfo) {
                        serviceTypeCodes.add(typeInfo.userDataTypeCode);
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String code : userAuthBean.userDataTypes) {
                        if (!TextUtils.isEmpty(code)) {
                            if (!serviceTypeCodes.contains(code)) {
                                    stringBuilder.append(code).append(",");
                            } else {
                                if (!needAuthData.contains(code)){
                                    needAuthData.add(code);
                                }
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(stringBuilder)){
                        String tips = stringBuilder.deleteCharAt(stringBuilder.length()-1).toString();
                        //有未申请的服务项，报错
                        response.code = CODE_FAIL_DEFULT;
                        response.message = tips + "无申请权限";
                        function.onCallBack(new Gson().toJson(response));
                        return;
                    }
                }

            }

            //组合跳转所需的数据
            UserAuthContentBean showBean = new UserAuthContentBean();
            showBean.title = context.getString(R.string.openplatform_user_address_select);
            int index = 0;
            //这里可以确保 addressRespList 不为空，因为如果为空就不会走到这里了
            //除非内存被清除了
            for (AddressResp addressResp : addressRespList){
                UserAuthContentBean.ItemBean itemBean = new UserAuthContentBean.ItemBean();
                itemBean.title = addressResp.addressName + "  " + addressResp.addressMobile;
                itemBean.subTitle = addressResp.detailAddress;
                if (index == 0){
                    itemBean.select = true;
                }
                index++;

                itemBean.sourceData = addressResp;
                showBean.addItem(itemBean);
            }
            serviceInfoResp.remark = context.getString(R.string.openplatform_user_address_remark);
            //跳转到授权页面
            OpenAuthSelectActivity.start(context, appId, mUserToken,needAuthData,AUTH_DATA_TYPE,serviceInfoResp, showBean);
        }
    }


    /**
     * 获取地址列表
     * @param context
     * @param appid
     * @param token
     * @param response
     * @param function
     */
    protected void getAddressList(final Context context, final String appid, String token, final NativeResponse response, final CallBackFunction function){
        if (context instanceof Activity){
            showLoading((Activity) context);
        }
        AddressPresenter addressPresenter = new AddressPresenter();
        addressPresenter.getAddressList(appid, token, new AddressPresenter.AddressCallback() {
            @Override
            public void onSuccess(List<AddressResp> list) {
                addressRespList = list;
                getServiceInfo(context, userAuthBean.appId, mUserToken, response, function);
            }

            @Override
            public void onEmpty() {
                dismissLoading();
                onAddressEmpty();
            }

            @Override
            public void onFailed(String code, String msg) {
                dismissLoading();
                if ("101".equals(code) || "103".equals(code) || "108".equals(code) || "109".equals(code)) {
                    PascOpenPlatform.getInstance().getOpenPlatformProvider().onOpenPlatformError(Integer.valueOf(code), msg);
                } else {
                    PascHybrid.getInstance()
                            .triggerCallbackFunction(ConstantBehaviorName.USER_AUTH, -1, msg, null);
                    if (OpenPlatformForThird.getInstance().requestIdCallback != null) {
                        OpenPlatformForThird.getInstance().requestIdCallback.authfail(-1, msg);
                    }
                    response.code = CODE_FAIL_DEFULT;
                    response.data = msg;
                    function.onCallBack(new Gson().toJson(response));
                }

            }
        });
    };


    protected void doFinish(NativeResponse response, CallBackFunction function, String msg){
        response.code = CODE_FAIL_DEFULT;
        response.data = msg;
        function.onCallBack(new Gson().toJson(response));
    }

    /**
     * 当地址为空时候调用，需要项目外部实现跳转到添加地址
     */
    protected abstract void onAddressEmpty();


    private void showLoading(Activity activity){
        loadingDialog=new LoadingDialog(activity);
        loadingDialog.setHasContent(false);
        loadingDialog.show();
    }

    private void dismissLoading(){
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
        loadingDialog = null;

    }

}
