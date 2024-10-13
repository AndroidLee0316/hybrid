package com.pasc.lib.hybrid;

import com.pasc.lib.hybrid.callback.CallBackFunction;

public interface WebViewJavascriptBridge {

    public void send(String data);

    public void send(String data, CallBackFunction responseCallback);
}
