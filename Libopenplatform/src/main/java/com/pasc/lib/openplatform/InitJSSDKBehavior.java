package com.pasc.lib.openplatform;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.pasc.lib.hybrid.PascHybridInterface;
import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.net.resp.BaseRespThrowableObserver;
import com.pasc.lib.openplatform.network.OpenBiz;
import com.pasc.lib.openplatform.resp.CheckInitCodeResp;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import com.pasc.lib.smtbrowser.entity.OpenplatformBean;

import java.io.Serializable;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * create by wujianning385 on 2018/10/22.
 */
public class InitJSSDKBehavior implements BehaviorHandler, Serializable {

    /**
     * 未初始化
     */
    public static final int STATUS_UN_INIT = -10001;
    /**
     * 默认应该为未初始化，即一定要初始化才能用
     */
    public static int initStatus = STATUS_UN_INIT;
    /**
     * 初始化使用的appid，在用户信息授权/用户地址授权的时候可以用这个
     */
    public static String appId;

    @Override
    public void handler(final Context context, String data, final CallBackFunction function,
                        final NativeResponse response) {
        try {
            Log.e("data", data);
            Gson gson = new Gson();
            final OpenplatformBean openplatformBean = gson.fromJson(data, OpenplatformBean.class);
            final List<String> nativeApis = openplatformBean.nativeApis;
            OpenBiz.checkCode(openplatformBean.appId, openplatformBean.initCode)
                    .subscribe(new Consumer<CheckInitCodeResp>() {
                @Override
                public void accept(CheckInitCodeResp checkInitCodeResp) throws Exception {
                    if (context instanceof PascWebviewActivity) {
                        if (((PascWebviewActivity) context).mWebviewFragment.mWebView != null) {
                            PascOpenPlatform.getInstance().getOpenPlatformProvider()
                                    .openPlatformBehavior(((PascWebviewActivity) context)
                                            .mWebviewFragment.mWebView, nativeApis);
                        }
                    } else if(context instanceof PascHybridInterface){
                        if(((PascHybridInterface) context).getPascWebView() !=null){
                            PascOpenPlatform.getInstance().getOpenPlatformProvider()
                                    .openPlatformBehavior(((PascHybridInterface) context).getPascWebView(), nativeApis);
                        }
                    }
                    if (checkInitCodeResp.verifyResult) {
                        appId = openplatformBean.appId;
                        response.code = 0;
                        response.message = "verifyResult= " + checkInitCodeResp.verifyResult;
                        initStatus = 0;
                    } else {
                        response.code = -1;
                        response.message = "verifyResult= " + checkInitCodeResp.verifyResult;
                        initStatus = -10001;
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            function.onCallBack(new Gson().toJson(response));
                        }
                    });
                }
            }, new BaseRespThrowableObserver() {
                @Override
                public void onError(int code, final String msg) {
                    Log.e("openPlatformTag", "" + code + "+m" + msg);
                    initStatus = -10001;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            response.code = -1;
                            response.message = msg;
                            function.onCallBack(new Gson().toJson(response));
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
