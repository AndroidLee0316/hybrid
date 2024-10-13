package com.pasc.lib.hybrid.callback;

import android.widget.ImageView;

import com.pasc.lib.hybrid.nativeability.WebStrategyType;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

public abstract class HybridInitCallback {
    /**
     * 外部实现图片加载
     */
    public abstract void loadImage(ImageView imageView, String url);

    /**
     * 外部控制settings，一定要设置usergent
     */
    public abstract void setWebSettings(WebSettings settings);

    /**
     * 控制主题，改变toolbar文字和图标颜色
     */
    public String themeColorString() {
        return null;
    }

    public String rightIconColorString() {
        return null;
    }

    public int titleCloseButton() {
        return WebStrategyType.CLOSEBUTTON_ALWAKES_VISIBLE;
    }

    public int titleCloseStyle() {
        return WebStrategyType.CLOSEBUTTON_TEXT;
    }

    public abstract void onWebViewCreate(WebView webView);

    public abstract void onWebViewProgressChanged(WebView webView, int progress);

    public abstract void onWebViewPageFinished(WebView webView, String url);
}
