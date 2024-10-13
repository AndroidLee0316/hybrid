package com.pasc.lib.hybrid;

import com.pasc.lib.hybrid.webview.PascWebView;

/**
 * 使用fragment接入hybrid必须实现的接口，让hybrid框架能通过context获取hybrid fragment和webview
 */
public interface PascHybridInterface {
    PascWebFragment getPascWebFragment();
    PascWebView getPascWebView();
}
