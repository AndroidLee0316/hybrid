package com.pasc.lib.hybrid.callback;

import com.tencent.smtt.sdk.WebView;

public interface OverrideCallback {

    boolean overrideUrl(WebView view, String url);

}
