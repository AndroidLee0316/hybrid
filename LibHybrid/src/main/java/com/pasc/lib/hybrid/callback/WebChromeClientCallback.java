package com.pasc.lib.hybrid.callback;

import android.net.Uri;
import android.view.View;

import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

public interface WebChromeClientCallback {
    void onProgressChanged(int newProgress);

    void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture);

    void showFileChooser(ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams);

    void onReceivedTitle(WebView view, String title);

    void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback);

    void onShowCustomView(View view, int i, IX5WebChromeClient.CustomViewCallback customViewCallback);

    void onHideCustomView();

}
