package com.pasc.lib.hybrid.webview;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.MimeTypeMap;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.callback.WebViewClientListener;
import com.pasc.lib.hybrid.util.BridgeUtil;
import com.pasc.lib.hybrid.util.Constants;
import com.pasc.lib.hybrid.util.LogUtils;
import com.pasc.lib.hybrid.util.MimeUtils;
import com.pasc.lib.hybrid.util.Utils;
import com.tencent.smtt.export.external.interfaces.ClientCertRequest;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Copyright (C) 2018 pasc Licensed under the Apache License, Version 1.0 (the "License");
 * <p>
 * hybird内核自定义WebViewClient，主要完成url拦截分发与WebView的行为状态监听
 *
 * @author chenshangyong872
 * @version 1.0
 * @date 2018-07-15
 */
public class PascWebViewClient extends WebViewClient {
    static final String TAG = "PASC_HYBRID";

    private static String ERROR_PAGE = "file:///android_asset/failload/failLoadPage.html";

    private PascWebView mWebView;

    private WebViewClientListener mListener = null;

    private boolean isLoadFinish = false;

    public PascWebViewClient(PascWebView webView) {
        this.mWebView = webView;
    }

    public void setWebViewClientListener(WebViewClientListener listener) {
        this.mListener = listener;
    }

    public boolean isLoadFinish() {
        return isLoadFinish;
    }

    // URL拦截判断逻辑
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        boolean logEnable = PascHybrid.getInstance().getHybridInitConfig().isLogEnable();
        if (logEnable) {
            Log.d(TAG, "WebViewClient should override url loading. url=" + url);
        }

        if (url.startsWith(BridgeUtil.PASC_BRIDGE_INJECT)) {
            BridgeUtil.webViewLoadLocalJs(view, PascWebView.JS_JAVASCRIPT_BRIDGE);
            return true;
        } else if (url.startsWith(BridgeUtil.PASC_RETURN_DATA)) { // 如果是返回数据
            try {
                url = URLDecoder.decode(url, "UTF-8");
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mWebView.handlerReturnData(url);
            return true;
        } else if (url.startsWith(BridgeUtil.PASC_OVERRIDE_SCHEMA)) {
            mWebView.flushMessageQueue();
            return true;
        } else {
            if (mListener != null) {
                return mListener.shouldOverrideUrlLoading(view, url);
            }
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (null != mListener) {
            mListener.onPageStarted(url, favicon);
        }

        isLoadFinish = false;
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (null != mListener) {
            mListener.onPageFinished(view, url);
        }

        super.onPageFinished(view, url);
        isLoadFinish = true;

        String jsStr = "";
        try {
            InputStream in = view.getContext().getAssets().open("Compatibility.js");
            byte buff[] = new byte[1024];
            ByteArrayOutputStream fromFile = new ByteArrayOutputStream();
            do {
                int numRead = in.read(buff);
                if (numRead <= 0) {
                    break;
                }
                fromFile.write(buff, 0, numRead);
            } while (true);
            jsStr = fromFile.toString();
            in.close();
            fromFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        view.evaluateJavascript(jsStr, new ValueCallback<String>() {
            @Override public void onReceiveValue(String value) {//js与native交互的回调函数
                Log.d(TAG, "Receive Value from Web. value=" + value);
            }
        });

        if(PascHybrid.getInstance().getHybridInitConfig() != null && PascHybrid.getInstance().getHybridInitConfig().getInjectJsCallback()!=null){
            PascHybrid.getInstance().getHybridInitConfig().getInjectJsCallback().injectJs(view);
        }

//        if (mWebView.getStartupMessage() != null) {
//            for (Message m : mWebView.getStartupMessage()) {
//                mWebView.dispatchMessage(m);
//            }
//
//            mWebView.setStartupMessage(null);
//        }
    }

    /////////////////////////////一下复写仅做监听/////////////////////////////
    @Override
    public void onLoadResource(WebView view, String url) {
        if (null != mListener) {
            mListener.onLoadResource(url);
        }
        super.onLoadResource(view, url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description,
                                String failingUrl) {
        if (null != mListener) {
            mListener.onReceivedError(errorCode, description, failingUrl);
        }
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if (null != mListener) {
            mListener.onReceivedError(request, error);
        }
        super.onReceivedError(view, request, error);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request,
                                    WebResourceResponse errorResponse) {
        if (null != mListener) {
            mListener.onReceivedHttpError(request, errorResponse);
        }

        super.onReceivedHttpError(view, request, errorResponse);
    }

    /**
     * 当后台不是ca认证的时候，手动校验https证书，目前没有检验，所以所有返回值都调用
     * sslErrorHandler.proceed();如若后期做成完全https，只需在checkSSLFalure调用
     * sslErrorHandler.cancel()，以及在WebViewSSlCheck中更换读取的证书路径即可
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                   SslError error) {
        if (null != mListener) {
            mListener.onReceivedSslError(view, handler, error);
        }
        Utils.showSslErrorDialog(view.getContext(), handler);
        //handler.proceed();

    }

    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        if (null != mListener) {
            mListener.onReceivedClientCertRequest(request);
        }

        super.onReceivedClientCertRequest(view, request);
    }

    @Override
    public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
        if (null != mListener) {
            mListener.onUnhandledKeyEvent(event);
        }

        super.onUnhandledKeyEvent(view, event);
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        if (null != mListener) {
            mListener.onScaleChanged(oldScale, newScale);
        }

        super.onScaleChanged(view, oldScale, newScale);
    }

    @Override
    public void onReceivedLoginRequest(WebView view, String realm, @Nullable String account,
                                       String args) {
        if (null != mListener) {
            mListener.onReceivedLoginRequest(realm, account, args);
        }

        super.onReceivedLoginRequest(view, realm, account, args);
    }

    @TargetApi(21)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        WebResourceResponse resourceResponse;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resourceResponse = handleResourceRequest(view, request.getUrl().toString());
        } else {
            resourceResponse = super.shouldInterceptRequest(view, request);
        }
        return resourceResponse;
    }


    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        return handleResourceRequest(view, url);
    }


    protected WebResourceResponse handleResourceRequest(final WebView webView, String requestUrl) {
        LogUtils.i(TAG, "[handleResourceRequest] url =  " + requestUrl);
        if (!shouldIntercept(requestUrl)) {
            return super.shouldInterceptRequest(webView, requestUrl);
        }

        // 图片等其他资源使用先返回空流，异步写数据
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(requestUrl);
        String mimeType = MimeUtils.guessMimeTypeFromExtension(fileExtension);
        try {
            if (requestUrl.trim().contains(Constants.IMAGE_TYPE)
                || requestUrl.trim().contains(Constants.VIDEO_TYPE)
                || requestUrl.trim().contains(Constants.AUDIO_TYPE)) {
                mimeType = Constants.MIME_TYPE_TEXT;
            }
            LogUtils.i(TAG, "start load async :" + requestUrl);
            WebResourceResponse xResponse = new WebResourceResponse(mimeType, "UTF-8", new NetworkInputStream(requestUrl));
            if (Utils.hasLollipop()) {
                Map<String, String> headers = new HashMap<>();
                headers.put("Access-Control-Allow-Origin", "*");
                if (requestUrl.trim().contains(Constants.VIDEO_KEY)) {
                    headers.put("Content-Type", "video/mp4");
                }
                if (requestUrl.trim().contains(Constants.AUDIO_KEY)) {
                    headers.put("Content-Type", "audio/aac");
                }
                xResponse.setResponseHeaders(headers);
            }
            return xResponse;
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtils.e(TAG, "url : " + requestUrl + " " + e.getMessage());
            return super.shouldInterceptRequest(webView, requestUrl);
        }
    }

    private boolean shouldIntercept(String url) {
        // 非合法uri，不拦截
        Uri uri = null;
        try {
            uri = Uri.parse(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == uri) {
            return false;
        }
        //只拦截native允许的paths
        if (!TextUtils.isEmpty(url) && url.contains(PascHybrid.PROTOFUL)) {
            Iterator<String> iterator = PascHybrid.getInstance().getAuthorizationPath().iterator();
            while (iterator.hasNext()) {
                String path = iterator.next();
                if (TextUtils.isEmpty(path)) {
                    iterator.remove();
                    continue;
                }
                if (url.contains(path)) {
                    return true;
                }
            }
        }
        return false;
    }
}