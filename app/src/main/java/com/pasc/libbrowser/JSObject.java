package com.pasc.libbrowser;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class JSObject {

    Activity activity;
    public JSObject(Activity activity){
        this.activity = activity;
    }

    @JavascriptInterface
    public void goBack() {
        //退出当前页

    }

    @JavascriptInterface
    public void close() {
        Toast.makeText(activity,"close覆盖",Toast.LENGTH_SHORT).show();
    }
}
