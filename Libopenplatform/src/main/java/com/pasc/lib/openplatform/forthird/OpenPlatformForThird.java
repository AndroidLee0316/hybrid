package com.pasc.lib.openplatform.forthird;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.pasc.lib.net.resp.BaseRespThrowableObserver;
import com.pasc.lib.openplatform.CertificationCallback;
import com.pasc.lib.openplatform.IBizCallback;
import com.pasc.lib.openplatform.OpenAuthorizationActivity;
import com.pasc.lib.openplatform.PascOpenPlatform;
import com.pasc.lib.openplatform.network.OpenBiz;
import com.pasc.lib.openplatform.resp.CheckInitCodeResp;
import com.pasc.lib.openplatform.resp.OpenIdResp;
import com.pasc.lib.openplatform.resp.RequestCodeResp;
import com.pasc.lib.openplatform.resp.ServiceInfoResp;
import com.pasc.lib.openplatform.resp.ServiceStatusResp;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class OpenPlatformForThird {
    private final static String LogTag = "openPlatformTag";
    public static final int CODE_FAIL_DEFULT = -1;
    public static final int CODE_SUCCESS = 0;
    public static final int CODE_JSSDK_NOT_INIT = -10001;
    public static final int CODE_USER_REFUSE = -10002;
    public static final int CODE_USER_NOT_LOGIN = -10003;
    public static final int CODE_USER_NOT_CERTIFICATION = -10004;
    private static OpenPlatformForThird openPlatformForThird;
    public RequestIdCallback requestIdCallback;
    private CompositeDisposable disposables = new CompositeDisposable ();

    public void dispose() {
        disposables.dispose ();
    }

    private OpenPlatformForThird() {

    }

    public static OpenPlatformForThird getInstance() {
        if (openPlatformForThird == null) {
            openPlatformForThird = new OpenPlatformForThird ();
        }
        return openPlatformForThird;
    }

    public OpenPlatformForThird setRequestIdCallback(RequestIdCallback requestIdCallback) {
        this.requestIdCallback = requestIdCallback;
        return this;
    }

    /**
     * jsSdk初始化
     *
     * @param context
     * @param appId
     * @param initCode
     */
    public void initJsSDK(final Context context, final String appId, String initCode) {
        Disposable disposable = OpenBiz.checkCode (appId, initCode).subscribe (new Consumer<CheckInitCodeResp> () {
            @Override
            public void accept(CheckInitCodeResp checkInitCodeResp) throws Exception {
                if (checkInitCodeResp.verifyResult) {
                    if (OpenPlatformForThird.getInstance ().requestIdCallback != null) {
                        OpenPlatformForThird.getInstance ().requestIdCallback
                                .initSuccess (CODE_SUCCESS, "初始化jsSDK成功");
                    }

                } else {
                    new Handler (context.getMainLooper ()).post (new Runnable () {
                        @Override
                        public void run() {
                            if (OpenPlatformForThird.getInstance ().requestIdCallback != null) {
                                OpenPlatformForThird.getInstance ().requestIdCallback
                                        .authfail (CODE_JSSDK_NOT_INIT, "没有初始化jsSDK");
                            }
                        }
                    });
                }
            }
        }, new BaseRespThrowableObserver () {
            @Override
            public void onError(int code, String msg) {
                if (OpenPlatformForThird.getInstance ().requestIdCallback != null) {
                    OpenPlatformForThird.getInstance ().requestIdCallback
                            .authfail (CODE_JSSDK_NOT_INIT, "没有初始化jsSDK:" + msg);
                }
            }
        });
        disposables.add (disposable);
    }

    /**
     * 兼容旧版本
     */
    public void initJsSDK(final Context context, final String appId, String initCode, final String serviceName) {
        initJsSDK (context, appId, initCode);
    }

    public void userAuth(final Context context, final String appId, final String serviceName) {
        userAuth (context, appId);
    }

    /**
     * 用户授权
     *
     * @param context
     * @param appId
     */
    public void userAuth(final Context context, final String appId) {
        if (appId == null) {
            if (OpenPlatformForThird.getInstance ().requestIdCallback != null) {
                OpenPlatformForThird.getInstance ().requestIdCallback
                        .authfail (CODE_FAIL_DEFULT, "没有获取到appid");
            }
            return;
        }

        if (null != PascOpenPlatform.getInstance ().getOpenPlatformProvider ()) {
            PascOpenPlatform.getInstance ().getOpenPlatformProvider ().getUserToken (new IBizCallback () {
                @Override
                public void onLoginSuccess(String userToken) {
                    if (null == userToken) {
                        OpenPlatformForThird.getInstance ().requestIdCallback
                                .authfail (CODE_USER_NOT_LOGIN, "没有获取到token");
                        return;
                    }

                    getServiceInfo (context, appId, userToken);
                }
            });
//            PascOpenPlatform.getInstance().getOpenPlatformProvider().getUserToken(new IBizCallback() {
//                @Override
//                public void onLoginSuccess(String userToken) {
//                    token = userToken;
//                }
//            });
//            if (TextUtils.isEmpty(token)) {
//                if(OpenPlatformForThird.getInstance().requestIdCallback!=null){
//                    OpenPlatformForThird.getInstance().requestIdCallback
//                            .authfail(CODE_USER_NOT_LOGIN,"没有获取到token");
//                }
//                return;
//            }
        } else {
            if (OpenPlatformForThird.getInstance ().requestIdCallback != null) {
                OpenPlatformForThird.getInstance ().requestIdCallback
                        .authfail (CODE_USER_NOT_LOGIN, "没有获取到token");
            }
            return;
        }


    }

    public void getServiceInfo(final Context context, final String appId, final String token) {
        Disposable disposable = OpenBiz.getServiceInfo (appId).subscribe (new Consumer<ServiceInfoResp> () {
            @Override
            public void accept(ServiceInfoResp infoResp) throws Exception {
                getServiceStatus (context, appId, token, infoResp);

            }
        }, new BaseRespThrowableObserver () {
            @Override
            public void onError(int code, String msg) {
                Log.e (LogTag, msg);

            }
        });
        disposables.add (disposable);
    }

    public void getServiceStatus(final Context context, final String appId, final String token
            , final ServiceInfoResp infoResp) {
        Disposable disposable = OpenBiz.getServiceStatus (appId, token).subscribe (new Consumer<ServiceStatusResp> () {
            @Override
            public void accept(final ServiceStatusResp statusResp) throws Exception {
//                if (infoResp != null && infoResp.authorizationStatus == 1) {
//                    getResquestCode(context, appId);
//                } else {
//                    getServiceInfo(context,appId,serviceName);
//                }
                //需要实名认证
                if ("1".equals (infoResp.realNameAuthStatus)) {
                    PascOpenPlatform.getInstance ().getOpenPlatformProvider ()
                            .getCertification (context, new CertificationCallback () {
                                @Override
                                public void certification(boolean isCertification) {
                                    if (isCertification) {
                                        if (statusResp != null && statusResp.authorizationStatus == 1) {
                                            getResquestCode (context, appId, token);
                                        } else {
                                            if ("0".equals (infoResp.authStatus)) {
                                                getOpenId (context, appId, token, infoResp.realNameAuthStatus);
                                            } else {
                                                OpenAuthorizationActivity.start (context, appId, token, infoResp.realNameAuthStatus);
                                            }
                                        }
                                    } else {
                                        OpenPlatformForThird.getInstance ().requestIdCallback
                                                .authfail (CODE_USER_NOT_CERTIFICATION, "用户没有实名认证");
                                    }
                                }
                            });
                } else {
                    if (statusResp != null && statusResp.authorizationStatus == 1) {
                        getResquestCode (context, appId, token);
                    } else {
                        if ("0".equals (infoResp.authStatus)) {
                            getOpenId (context, appId, token, infoResp.realNameAuthStatus);
                        } else {
                            OpenAuthorizationActivity.start (context, appId, token, infoResp.realNameAuthStatus);
                        }
                    }
                }
            }
        }, new BaseRespThrowableObserver () {
            @Override
            public void onError(int code, String msg) {
                if (OpenPlatformForThird.getInstance ().requestIdCallback != null) {
                    OpenPlatformForThird.getInstance ().requestIdCallback
                            .authfail (CODE_FAIL_DEFULT, "获取授权状态失败:" + msg);
                }
                PascOpenPlatform.getInstance ()
                        .getOpenPlatformProvider ()
                        .onOpenPlatformError (code, msg);
            }
        });
        disposables.add (disposable);
    }

//    public void openDialog(final Context context,final String appId,final String serviceName){
//        new Handler(context.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                new OpenAuthorizationDialog(context, new OpenAuthorizationDialog.OnButtonClickListener() {
//                    @Override
//                    public void onSure(OpenAuthorizationDialog dialog) {
//                        getOpenId(context,appId);
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        if(OpenPlatformForThird.getInstance().requestIdCallback!=null){
//                            OpenPlatformForThird.getInstance().requestIdCallback
//                                    .authfail(CODE_USER_REFUSE,"用户拒绝授权");
//                        }
//                    }
//                }).setServiceName(serviceName).show();
//
//            }
//        });
//    }

    public void getResquestCode(final Context context, final String appId, final String token) {
        Disposable disposable = OpenBiz.getResquestCode (appId, token).subscribe (new Consumer<RequestCodeResp> () {
            @Override
            public void accept(final RequestCodeResp requestCodeResp) throws Exception {
                //final ServiceAuthResult result = new ServiceAuthResult();
                new Handler (context.getMainLooper ()).post (new Runnable () {
                    @Override
                    public void run() {
                        if (OpenPlatformForThird.getInstance ().requestIdCallback != null) {
                            OpenPlatformForThird.getInstance ().requestIdCallback
                                    .getRequestId (requestCodeResp.requestCode, requestCodeResp.expiresIn);

                        }
                    }
                });
            }
        }, new BaseRespThrowableObserver () {
            @Override
            public void onError(int code, String msg) {
                if (OpenPlatformForThird.getInstance ().requestIdCallback != null) {
                    OpenPlatformForThird.getInstance ().requestIdCallback
                            .authfail (CODE_FAIL_DEFULT, "授权失败：" + msg);
                }
            }
        });
        disposables.add (disposable);
    }

    public void getOpenId(final Context context, final String appId, final String token
            , final String realNameAuthStatus) {
      Disposable disposable=  OpenBiz.getOpenId (appId, token).subscribe (new Consumer<OpenIdResp> () {
            @Override
            public void accept(OpenIdResp openIdResp) throws Exception {
                getResquestCode (context,appId,token);
            }
        }, new BaseRespThrowableObserver () {
            @Override
            public void onError(int code, String msg) {
                if (OpenPlatformForThird.getInstance ().requestIdCallback != null) {
                    OpenPlatformForThird.getInstance ().requestIdCallback
                            .authfail (CODE_FAIL_DEFULT, "获取openid失败：" + msg);
                }
            }
        });
        disposables.add (disposable);
    }
}
