package com.pasc.lib.hybrid.webview;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.pasc.lib.hybrid.Message;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.callback.WebChromeClientCallback;
import com.pasc.lib.hybrid.widget.CommonDialog;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

/**
 * Copyright (C) 2018 pasc Licensed under the Apache License, Version 1.0 (the "License");
 * <p>
 * hybird内核自定义WebChromeClient，主要完成JsAlert原生窗口化与WebView的加载进度和文件get
 *
 * @author chenshangyong872
 * @version 1.0
 * @date 2018-07-15
 */
public class PascWebChromeClient extends WebChromeClient {
    private final String TAG = PascWebChromeClient.class.getSimpleName();

    private Context mContext = null;
    private WebChromeClientCallback mCallback = null;

    public void setContext(Context context) {
        mContext = context;
    }

    public void setWebChromeClientCallback(WebChromeClientCallback callback) {
        mCallback = callback;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        if(WebViewContants.banAlart){
            return true;
        }
//        if (!((Activity)view.getContext()).isFinishing()) {
//            new CommonDialog(mContext).setContent(message).setButton1("取消").setButton2("确定",CommonDialog.Blue_4d73f4)
//                    .setOnButtonClickListener(new CommonDialog.OnButtonClickListener() {
//                        @Override
//                        public void button1Click() {
//                            super.button1Click();
//                            result.cancel();
//                        }
//
//                        @Override
//                        public void button2Click() {
//                            super.button2Click();
//                            result.confirm();
//                        }
//                    }).show();
//        }
        result.confirm();// 不加这行代码，会造成Alert劫持：Alert只会弹出一次，并且WebView会卡死
        return true;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        Log.d(TAG, "进度发生改变::" + newProgress);
        if (null != mCallback) {
            mCallback.onProgressChanged(newProgress);
        }

        super.onProgressChanged(view, newProgress);

        if(view instanceof PascWebView && newProgress > 90){
            if(((PascWebView) view).getStartupMessage() != null){
                for(Message m : ((PascWebView) view).getStartupMessage()){
                    ((PascWebView) view).dispatchMessage(m);
                }
                ((PascWebView) view).setStartupMessage(null);
            }
        }

    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if (null != mCallback) {
            mCallback.onReceivedTitle(view,title);
        }
    }

    @Override
    public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
        if (null != mCallback) {
            mCallback.openFileChooser(valueCallback,acceptType,capture);
        }
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
        if (null != mCallback) {
            mCallback.showFileChooser(valueCallback,fileChooserParams);
        }
        return true;
    }

    @Override
    public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
        super.onShowCustomView(view, customViewCallback);
        if (null != mCallback) {
            mCallback.onShowCustomView(view,customViewCallback);
        }
    }

    @Override
    public void onShowCustomView(View view, int i, IX5WebChromeClient.CustomViewCallback customViewCallback) {
        super.onShowCustomView(view, i, customViewCallback);
        if (null != mCallback) {
            mCallback.onShowCustomView(view,i,customViewCallback);
        }
    }

    @Override
    public void onHideCustomView() {
        super.onHideCustomView();
        if (null != mCallback) {
            mCallback.onHideCustomView();
        }
    }
}
