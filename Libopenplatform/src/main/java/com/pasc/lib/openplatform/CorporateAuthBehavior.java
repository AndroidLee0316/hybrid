package com.pasc.lib.openplatform;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.PascWebviewActivity;
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

import java.io.Serializable;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class CorporateAuthBehavior implements BehaviorHandler, Serializable {
    /**
     * 是否已经发起请求状态
     */
    private boolean isServiceStatus;
    public static final int CODE_FAIL_DEFULT = -1;
    public static final int CODE_JSSDK_NOT_INIT = -10001;
    public static final int CODE_USER_REFUSE = -10002;
    public static final int CODE_USER_NOT_LOGIN = -10003;
    public static final int CODE_USER_NOT_CERTIFICATION = -10004;
    private CompositeDisposable disposables = new CompositeDisposable();

    NativeResponse response;
    CallBackFunction function;
    UserAuthBean userAuthBean;
    Context context;
    public void dispose() {
        disposables.dispose();
    }

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
            final UserAuthBean userAuthBean = gson.fromJson(data, UserAuthBean.class);
            if (!TextUtils.isEmpty(InitJSSDKBehavior.appId)){
                userAuthBean.appId = InitJSSDKBehavior.appId;
            }
            PascHybrid.getInstance().saveCallBackFunction(context.hashCode(), ConstantBehaviorName.ENTERPRISE_USER_AUTH, function);
            getToken(context,userAuthBean,function,response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getToken(){
        PascOpenPlatform.getInstance().getOpenPlatformProvider().getCorporateToken(new IBizCallback() {
            @Override
            public void onLoginSuccess(String userToken) {
                if (null == userToken) {
                    response.code = CODE_USER_NOT_LOGIN;
                    response.message = "用户未登陆";
                    function.onCallBack(new Gson().toJson(response));
                    return;
                }
                isServiceStatus = false;
                if (context instanceof PascWebviewActivity) {
                    if (((PascWebviewActivity) context).isFinishing()) {
                        isServiceStatus = true;
                    }
                }
                getServiceInfo(context, userAuthBean.appId, userToken, response, function);
            }
        });
    }

    public void getToken(final Context context, final UserAuthBean userAuthBean, final CallBackFunction function,
                         final NativeResponse response){
        PascOpenPlatform.getInstance().getOpenPlatformProvider().getCorporateToken(new IBizCallback() {
            @Override
            public void onLoginSuccess(String userToken) {
                if (null == userToken) {
                    response.code = CODE_USER_NOT_LOGIN;
                    response.message = "用户未登陆";
                    function.onCallBack(new Gson().toJson(response));
                    return;
                }
                isServiceStatus = false;
                if (context instanceof PascWebviewActivity) {
                    if (((PascWebviewActivity) context).isFinishing()) {
                        isServiceStatus = true;
                    }
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
        Disposable disposable = OpenBiz.getServiceInfo(appId).subscribe(new Consumer<ServiceInfoResp>() {
            @Override
            public void accept(ServiceInfoResp infoResp) throws Exception {
                getCorporateAuthInfo(context, appId, response, function, token, infoResp);

            }
        }, new BaseRespThrowableObserver() {
            @Override
            public void onError(int code, String msg) {
                Log.e("openPlatformTag", msg);
            }
        });
        disposables.add(disposable);
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
    private void getCorporateAuthInfo(final Context context, final String appId,
                                  final NativeResponse response, final CallBackFunction function
            , final String token, final ServiceInfoResp infoResp) {
        if (!isServiceStatus) {
            isServiceStatus = true;
            Disposable disposable = OpenBiz.getCorporateAuthInfo(appId, token).subscribe(new Consumer<ServiceStatusResp>() {
                @Override
                public void accept(final ServiceStatusResp statusResp) throws Exception {
                    //需要实名认证
                    if ("1".equals(infoResp.realNameAuthStatus)) {
                        PascOpenPlatform.getInstance().getOpenPlatformProvider()
                                .getCertification(context, new CertificationCallback() {
                                    @Override
                                    public void certification(boolean isCertification) {
                                        if (isCertification) {
                                            if (statusResp != null && statusResp.authorizationStatus == 1) {
                                                getCorporateRequestCode(context, appId, token, response, function);
                                            } else {
                                                if ("0".equals(infoResp.authStatus)) {
                                                    getCorporateOpenId(context, appId, token, response, function);
                                                } else {
                                                    OpenCorporateAuthorizationActivity.start(context, appId, token, infoResp.realNameAuthStatus);
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
                        if (statusResp != null && statusResp.authorizationStatus == 1) {
                            getCorporateRequestCode(context, appId, token, response, function);
                        } else {
                            if ("0".equals(infoResp.authStatus)) {
                                getCorporateOpenId(context, appId, token, response, function);
                            } else {
                                OpenCorporateAuthorizationActivity.start(context, appId, token, infoResp.realNameAuthStatus);
                            }
                        }
                    }
                }
            }, new BaseRespThrowableObserver() {
                @Override
                public void onError(int code, String msg) {
                    //token状态101不合法，103已失效，104空
                    if(code == 101 || code == 103 || code == 104){
                        PascOpenPlatform.getInstance()
                                .getOpenPlatformProvider()
                                .onCorporateOpenPlatformError(code, msg);
                    } else {
                        response.code = CODE_FAIL_DEFULT;
                        response.data = "获取服务状态失败：" + msg;
                        function.onCallBack(new Gson().toJson(response));
                    }
                }
            });
            disposables.add(disposable);
        }
    }

    public void getCorporateRequestCode(final Context context, final String appId, String token, final NativeResponse response, final CallBackFunction function) {
        Disposable disposable = OpenBiz.getCorporateRequestCode(appId, token).subscribe(new Consumer<RequestCodeResp>() {
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
                //token状态101不合法，103已失效，104空
                if(code == 101 || code == 103 || code == 104){
                    PascOpenPlatform.getInstance()
                            .getOpenPlatformProvider()
                            .onCorporateOpenPlatformError(code, msg);
                } else {
                    response.code = CODE_FAIL_DEFULT;
                    response.data = "授权失败：" + msg;
                    function.onCallBack(new Gson().toJson(response));
                }
            }
        });
        disposables.add(disposable);
    }

    public void getCorporateOpenId(final Context context, final String appId, final String token
            , final NativeResponse response, final CallBackFunction function) {
        Disposable disposable = OpenBiz.getCorporateOpenId(appId, token).subscribe(new Consumer<OpenIdResp>() {
            @Override
            public void accept(OpenIdResp openIdResp) throws Exception {
                getCorporateRequestCode(context, appId, token, response, function);
            }
        }, new BaseRespThrowableObserver() {
            @Override
            public void onError(int code, String msg) {
                //token状态101不合法，103已失效，104空
                if(code == 101 || code == 103 || code == 104){
                    PascOpenPlatform.getInstance()
                            .getOpenPlatformProvider()
                            .onCorporateOpenPlatformError(code, msg);
                } else {
                    response.code = CODE_FAIL_DEFULT;
                    response.data = "获取openid失败：" + msg;
                    function.onCallBack(new Gson().toJson(response));
                }
            }
        });
        disposables.add(disposable);
    }
}