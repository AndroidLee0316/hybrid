package com.pasc.lib.hybrid.callback;

import android.content.Intent;
import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.widget.WebCommonTitleView;
import com.tencent.smtt.sdk.WebView;

/**
 * create by wujianning385 on 2018/10/25.
 */
public interface OldLogicCallback {

  /**
   * 设置旧的收藏
   */
  void oldCollection(WebCommonTitleView mCommonTitleView);

  /**
   * 设置旧的url拦截
   */
  boolean oldInterceptCallback(WebView webView,String url);

  /**
   * 设置旧的interface交互
   */
  void oldInterfaceCallback(WebCommonTitleView webCommonTitleView, WebView webView);

  void oldActivityResultCallback(WebView webView, int requestCode, int resultCode, Intent data);

  /**
   * 原生关闭页面
   */
  void oldNativeClose(PascWebviewActivity pascWebviewActivity);

  void onInterceptPageStarted(WebView webView,String url);
}
