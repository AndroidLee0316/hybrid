package com.pasc.lib.hybrid.callback;

import android.graphics.Bitmap;
import android.view.KeyEvent;

import com.tencent.smtt.export.external.interfaces.ClientCertRequest;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;

/**
 * 用于业务监听Webview的状态行为
 */
public interface WebViewClientListener {
    void onPageStarted(String url, Bitmap favicon);

    void onPageFinished(WebView webView, String url);

    void onLoadResource(String url);

    void onReceivedError(int errorCode, String description, String failingUrl);

    void onReceivedError(WebResourceRequest request, WebResourceError error);

    void onReceivedHttpError(WebResourceRequest request, WebResourceResponse errorResponse);

    void onReceivedSslError(WebView view,SslErrorHandler handler, SslError error);

    void onReceivedClientCertRequest(ClientCertRequest request);

    void onUnhandledKeyEvent(KeyEvent event);

    void onScaleChanged(float oldScale, float newScale);

    void onReceivedLoginRequest(String realm, String account, String args);

    boolean shouldOverrideUrlLoading(WebView view, String url);
}
