package com.pasc.lib.openplatform;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.net.resp.BaseRespThrowableObserver;
import com.pasc.lib.openplatform.network.OpenBiz;
import com.pasc.lib.openplatform.resp.OpenIdResp;
import com.pasc.lib.openplatform.resp.RequestCodeResp;
import com.pasc.lib.openplatform.resp.ServiceAuthResult;
import com.pasc.lib.openplatform.resp.ServiceInfoResp;
import com.pasc.lib.openplatform.resp.ServiceStatusResp;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import com.pasc.lib.smtbrowser.entity.UserAuthBean;
import com.pasc.libopenplatform.R;

import io.reactivex.functions.Consumer;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 开放平台用户授权行为
 * create by wujianning385 on 2018/10/22.
 */
public class UserAuthBehavior implements BehaviorHandler, Serializable {
    /**
     * 是否已经发起请求状态
     */
    private boolean isServiceStatus;
    public static final int CODE_FAIL_DEFULT = -1;
    public static final int CODE_JSSDK_NOT_INIT = -10001;
    public static final int CODE_USER_REFUSE = -10002;
    public static final int CODE_USER_NOT_LOGIN = -10003;
    public static final int CODE_USER_NOT_CERTIFICATION = -10004;
//    private CompositeDisposable disposables = new CompositeDisposable();
//    public void dispose() {
//        disposables.dispose();
//    }
    private NativeResponse response;
    private CallBackFunction function;
    private UserAuthBean userAuthBean;
    private boolean isGo2Auth = false;
    private Context context;
    private ArrayList<String> needAuthData;
    private ArrayList<String> inputData;

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
            isGo2Auth = false;
            if (userAuthBean.userDataTypes==null){
                userAuthBean.userDataTypes = new ArrayList<>();
            }
            inputData = (ArrayList<String>) userAuthBean.userDataTypes;
            PascHybrid.getInstance().saveCallBackFunction(context.hashCode(), ConstantBehaviorName.USER_AUTH, function);
            getToken(context,userAuthBean,function,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getToken(){
        PascOpenPlatform.getInstance().getOpenPlatformProvider().getUserToken(new IBizCallback() {
            @Override
            public void onLoginSuccess(String userToken) {
                if (null == userToken) {
                    response.code = CODE_USER_NOT_LOGIN;
                    response.message = "用户未登陆";
                    function.onCallBack(new Gson().toJson(response));
                    return;
                }
                isServiceStatus = false;
                if (context instanceof Activity&&((Activity) context).isFinishing()) {
                        isServiceStatus = true;
                }
                getServiceInfo(context, userAuthBean.appId, userToken, response, function);
            }
        });
    }

    public void getToken(final Context context, final UserAuthBean userAuthBean, final CallBackFunction function,
                         final NativeResponse response){
        PascOpenPlatform.getInstance().getOpenPlatformProvider().getUserToken(new IBizCallback() {
            @Override
            public void onLoginSuccess(String userToken) {
                if (null == userToken) {
                    response.code = CODE_USER_NOT_LOGIN;
                    response.message = "用户未登陆";
                    function.onCallBack(new Gson().toJson(response));
                    return;
                }
                isServiceStatus = false;
                if (context instanceof Activity&&((Activity) context).isFinishing()) {
                    isServiceStatus = true;
                }
                getServiceInfo(context, userAuthBean.appId, userToken, response, function);
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
                getServiceStatus(context, appId, response, function, token, infoResp);

            }
        }, new BaseRespThrowableObserver() {
            @Override
            public void onError(int code, String msg) {
                Log.e("openPlatformTag", msg);
                if(code == 101 || code == 103 || code == 108 || code == 109){
                    PascOpenPlatform.getInstance()
                            .getOpenPlatformProvider()
                            .onOpenPlatformError(code, msg);
                } else {
                    response.code = CODE_FAIL_DEFULT;
                    response.message = msg;
                    function.onCallBack(new Gson().toJson(response));
                }

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

            OpenBiz.getServiceStatus(appId, token).subscribe(new Consumer<ServiceStatusResp>() {
                @Override
                public void accept(final ServiceStatusResp statusResp) throws Exception {
                    if (infoResp.applyUserDataTypeInfo!=null&&statusResp.userDataTypeAuthInfos!=null) {
                        Map<String, String> statusMap = new HashMap<>();
                        for (ServiceStatusResp.AuthInfos authInfo : statusResp.userDataTypeAuthInfos) {
                            statusMap.put(authInfo.userDataTypeCode, authInfo.userDataTypeAuthStatus);
                        }
                        //如果有未授权的服务
                        for (String code : userAuthBean.userDataTypes) {
                            if ("0".equals(statusMap.get(code))) {
                                //有权限未授权，加入需要授权集合，跳授权页
                                if (!needAuthData.contains(code)){
                                    needAuthData.add(code);
                                }
                            }else if ("1".equals(statusMap.get(code))){
                                needAuthData.remove(code);
                            }
                        }
                        if ("0".equals(infoResp.authStatus)){
                            isGo2Auth = false;
                        }else if (needAuthData.size()>0){
                            isGo2Auth = true;
                        }
                    }

                    //需要实名认证
                    if ("1".equals(infoResp.realNameAuthStatus)) {
                        PascOpenPlatform.getInstance().getOpenPlatformProvider()
                                .getCertification(context, new CertificationCallback() {
                                    @Override
                                    public void certification(boolean isCertification) {
                                        if (isCertification) {
                                            if (isGo2Auth){
                                                OpenAuthorizationActivity.start(context,
                                                    appId, token, infoResp.realNameAuthStatus,needAuthData,inputData);
                                            }else {
                                                if (statusResp != null
                                                    && statusResp.authorizationStatus == 1) {
                                                    getResquestCode(context, appId, token, response,
                                                        function);
                                                } else {
                                                    if ("0".equals(infoResp.authStatus)) {
                                                        getOpenId(context, appId, token, response,
                                                            function);
                                                    } else {
                                                        OpenAuthorizationActivity.start(context,
                                                            appId, token, infoResp.realNameAuthStatus,needAuthData,inputData);
                                                    }
                                                }
                                            }
                                        } else {
                                            response.code = CODE_USER_NOT_CERTIFICATION;
                                            response.data = "用户未实名认证";
                                            function.onCallBack(new Gson().toJson(response));
                                        }
                                    }
                                });
                    } else {
                        if (isGo2Auth){
                            OpenAuthorizationActivity.start(context, appId, token, infoResp.realNameAuthStatus,needAuthData,inputData);
                        }else {
                            if (statusResp != null && statusResp.authorizationStatus == 1) {
                                getResquestCode(context, appId, token, response, function);
                            } else {
                                if ("0".equals(infoResp.authStatus)) {
                                    getOpenId(context, appId, token, response, function);
                                } else {
                                    OpenAuthorizationActivity.start(context, appId, token, infoResp.realNameAuthStatus,needAuthData,inputData);
                                }
                            }
                        }

                    }
                }
            }, new BaseRespThrowableObserver() {
                @Override
                public void onError(int code, String msg) {
                    //token状态101不合法，103已失效，108错误，109session失效
                    if(code == 101 || code == 103 || code == 108 || code == 109){
                        PascOpenPlatform.getInstance()
                                .getOpenPlatformProvider()
                                .onOpenPlatformError(code, msg);
                    } else {
                        response.code = CODE_FAIL_DEFULT;
                        response.data = "获取服务状态失败：" + msg;
                        function.onCallBack(new Gson().toJson(response));
                    }
                }
            });
            //disposables.add(disposable);
        }
    }

    public void getResquestCode(final Context context, final String appId, String token, final NativeResponse response, final CallBackFunction function) {
        List<String> dataTypes = new ArrayList<>();
        for (String code : userAuthBean.userDataTypes) {
            if (!TextUtils.isEmpty(code)){
                dataTypes.add(code);
            }
        }
        OpenBiz.getResquestCode(appId, token,dataTypes).subscribe(new Consumer<RequestCodeResp>() {
            @Override
            public void accept(RequestCodeResp requestCodeResp) throws Exception {
                ServiceAuthResult result = new ServiceAuthResult();
                result.requestCode = requestCodeResp.requestCode;
                result.openId = requestCodeResp.openId;
                response.code = 0;
                response.data = result;
                function.onCallBack(new Gson().toJson(response));
            }
        }, new BaseRespThrowableObserver() {
            @Override
            public void onError(int code, String msg) {
                //token状态101不合法，103已失效，108错误，109session失效
                if(code == 101 || code == 103 || code == 108 || code == 109){
                    PascOpenPlatform.getInstance()
                            .getOpenPlatformProvider()
                            .onOpenPlatformError(code, msg);
                } else {
                    response.code = CODE_FAIL_DEFULT;
                    response.data = "授权失败：" + msg;
                    function.onCallBack(new Gson().toJson(response));
                }
            }
        });
        //disposables.add(disposable);
    }

    public void getOpenId(final Context context, final String appId, final String token
            , final NativeResponse response, final CallBackFunction function) {
        OpenBiz.getOpenId(appId, token).subscribe(new Consumer<OpenIdResp>() {
            @Override
            public void accept(OpenIdResp openIdResp) throws Exception {
                getResquestCode(context, appId, token, response, function);
            }
        }, new BaseRespThrowableObserver() {
            @Override
            public void onError(int code, String msg) {
                //token状态101不合法，103已失效，108错误，109session失效
                if(code == 101 || code == 103 || code == 108 || code == 109){
                    PascOpenPlatform.getInstance()
                            .getOpenPlatformProvider()
                            .onOpenPlatformError(code, msg);
                } else {
                    response.code = CODE_FAIL_DEFULT;
                    response.data = "获取openid失败：" + msg;
                    function.onCallBack(new Gson().toJson(response));
                }
            }
        });
        //disposables.add(disposable);
    }
}
