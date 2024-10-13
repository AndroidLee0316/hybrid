package com.pasc.lib.hybrid;

import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.ErrorPageback;
import com.pasc.lib.hybrid.callback.HybridInitCallback;
import com.pasc.lib.hybrid.callback.InjectJsCallback;
import com.pasc.lib.hybrid.callback.NoNetPageback;
import com.pasc.lib.hybrid.callback.OldLogicCallback;
import com.pasc.lib.hybrid.callback.WebErrorListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.lang.String;

/**
 * hybrid初始化配置，只在初始化时调用一次
 */
public class HybridInitConfig implements Serializable {

  /**
   * 扩展提供给web的接口
   */
  public Map<String, BehaviorHandler> customerBehaviors = new HashMap<>(16);
  private Map<String, String> behaviorRemarks = new HashMap<>();

  /**
   * 添加自定义行为.
   *
   * @param handlerName 行为名称.
   * @param behaviorHandler 行为处理器.
   * @return 配置对象.
   * @see #addCustomerBehavior(String, BehaviorHandler, String)
   * @deprecated
   */
  public HybridInitConfig addCustomerBehavior(String handlerName, BehaviorHandler behaviorHandler) {
    customerBehaviors.put(handlerName, behaviorHandler);
    return this;
  }

  /**
   * 添加自定义行为.
   *
   * @param handlerName 行为名称.
   * @param behaviorHandler 行为处理器.
   * @param remark 备注信息，主要用于导出API文档，能够让开发者理解API的含义.
   * @return 配置对象.
   */
  public HybridInitConfig addCustomerBehavior(String handlerName, BehaviorHandler behaviorHandler,
      String remark) {
    customerBehaviors.put(handlerName, behaviorHandler);
    behaviorRemarks.put(handlerName,remark);
    return this;
  }

  public Map<String, String> getBehaviorRemarks() {
    return behaviorRemarks;
  }

  /**
   * 初始化所需的回调
   */
  private HybridInitCallback hybridInitCallback;

  /**
   * webview 加载错误回调
   */
  private WebErrorListener webErrorListener;

  /**
   * 老的逻辑回调
   */
  private OldLogicCallback oldLogicCallback;
  /**
   * 错误页面回调
   */
  private ErrorPageback errorPageback;
  /**
   * 错误页面回调
   */
  private NoNetPageback nonetPageback;

  /**
   * 注入js回调
   */
  private InjectJsCallback injectJsCallback;

  private boolean logEnable; // 日志启用

  public HybridInitConfig setHybridInitCallback(HybridInitCallback hybridInitCallback) {
    this.hybridInitCallback = hybridInitCallback;
    return this;
  }

  public HybridInitCallback getHybridInitCallback() {
    return hybridInitCallback;
  }

  public HybridInitConfig setWebErrorListener(WebErrorListener webErrorListener) {
    this.webErrorListener = webErrorListener;
    return this;
  }

  public WebErrorListener getWebErrorListener() {
    return webErrorListener;
  }

  public HybridInitConfig setOldLogicCallback(OldLogicCallback oldLogicCallback) {
    this.oldLogicCallback = oldLogicCallback;
    return this;
  }

  public OldLogicCallback getOldLogicCallback() {
    return oldLogicCallback;
  }

  public HybridInitConfig setErrorPagek(ErrorPageback errorPageback) {
    this.errorPageback = errorPageback;
    return this;
  }

  public ErrorPageback getErrorPagek() {
    return errorPageback;
  }

  public HybridInitConfig setNoNetPagek(NoNetPageback nonetPageback) {
    this.nonetPageback = nonetPageback;
    return this;
  }

  public NoNetPageback getNoNetPagek() {
    return nonetPageback;
  }

  public HybridInitConfig setInjectJsCallback(InjectJsCallback injectJsCallback) {
    this.injectJsCallback = injectJsCallback;
    return this;
  }

  public InjectJsCallback getInjectJsCallback() {
    return injectJsCallback;
  }

  public boolean isLogEnable() {
    return logEnable;
  }

  public void setLogEnable(boolean logEnable) {
    this.logEnable = logEnable;
  }
}
