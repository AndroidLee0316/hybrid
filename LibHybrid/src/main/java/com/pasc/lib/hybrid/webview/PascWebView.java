package com.pasc.lib.hybrid.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.pasc.lib.hybrid.HybridInitConfig;
import com.pasc.lib.hybrid.Message;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.WebViewJavascriptBridge;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.behavior.DefaultBehaviorManager;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.hybrid.callback.HybridInitCallback;
import com.pasc.lib.hybrid.callback.WebSettingCallback;
import com.pasc.lib.hybrid.callback.WebViewClientListener;
import com.pasc.lib.hybrid.util.BridgeUtil;
import com.pasc.lib.smtbrowser.util.FileUiUtils;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("SetJavaScriptEnabled")
public class PascWebView extends WebView implements WebViewJavascriptBridge {

  private final String TAG = "PASC_HYBRID";

  public static final String JS_JAVASCRIPT_BRIDGE = "PascWebViewJavascriptBridge.js";
  public static final String JS_COMPATIBILITY = "Compatibility.js";

  /**
   * 业务自定义的behavior 定义为webview的私有属性，这样可以兼容多个webview的场景
   */
  private Map<String, BehaviorHandler> mCustomBehaviorCache = new HashMap<>(16);
  Map<String, CallBackFunction> responseCallbacks = new HashMap<String, CallBackFunction>();
  private Context mContext;

  private List<Message> startupMessage = new ArrayList<Message>();

  public List<Message> getStartupMessage() {
    return startupMessage;
  }

  private PascWebChromeClient mWebChromeClient = null;
  private PascWebViewClient mWebViewClient = null;
  private OnWebScorollListener mOnWebScorollListener;
  WebSettingCallback webSettingCallback;
  WebSettings settings;

  @Override
  public PascWebChromeClient getWebChromeClient() {
    sureWebChromeClient();
    return mWebChromeClient;
  }

  public void setWebViewClientListener(WebViewClientListener listener) {
    if (null != mWebViewClient) {
      mWebViewClient.setWebViewClientListener(listener);
    }
  }

  public Map<String, BehaviorHandler> getCustomBehaviorCache() {
    return mCustomBehaviorCache;
  }

  public boolean isLoadFinish() {
    if (null != mWebViewClient) {
      return mWebViewClient.isLoadFinish();
    }

    return true;
  }

  public void setStartupMessage(List<Message> startupMessage) {
    this.startupMessage = startupMessage;
  }

  private long uniqueId = 0;

  public PascWebView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    init();
  }

  public PascWebView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    mContext = context;
    init();
  }

  public PascWebView(Context context) {
    super(context);
    mContext = context;
    init();
  }

  private void init() {
    requestFocus(View.FOCUS_DOWN);// 手动加入输入焦点, 有些手机不支持键盘弹出
    this.setVerticalScrollBarEnabled(false);
    this.setHorizontalScrollBarEnabled(false);
    settings = getSettings();
    settings.setJavaScriptEnabled(true);
    settings.setSavePassword(false);//h5界面不准保存密码
    settings.setUseWideViewPort(true);
    settings.setLoadWithOverviewMode(true);
    settings.setBuiltInZoomControls(true);
    settings.setDisplayZoomControls(false);
    settings.setAppCacheEnabled(true);
    String appCachePath = FileUiUtils.getExternalCacheDir(mContext).getAbsolutePath();
    Log.d(TAG, "appCachePath::" + appCachePath);
    settings.setAppCachePath(appCachePath);
    settings.setAppCacheMaxSize(512 * 1024 * 1024);
    settings.setDatabaseEnabled(true);
    settings.setDomStorageEnabled(true);
    settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    settings.setAllowFileAccess(false);
    //解决图片加载不出来的问题，设置网页在加载的时候暂时不加载图片
    settings.setBlockNetworkImage(false);
    //兼容荣耀10，p20手机https加载http图片问题
    if ("BKL-AL00".equals(android.os.Build.MODEL) || "COL-AL10".equals(android.os.Build.MODEL)
        || "EML-AL00".equals(android.os.Build.MODEL)) {
      settings.setMixedContentMode(WebSettings.LOAD_NO_CACHE);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      settings.setMixedContentMode(WebSettings.LOAD_NORMAL);
    }
    HybridInitConfig hybridInitConfig = PascHybrid.getInstance().getHybridInitConfig();
    if (hybridInitConfig != null) {
      HybridInitCallback hybridInitCallback = hybridInitConfig.getHybridInitCallback();
      if (hybridInitCallback != null) {
        hybridInitCallback.setWebSettings(settings);
      }
    }
    sureWebChromeClient();
    sureWebViewClient();
  }

  public void setWebSettingCallback(WebSettingCallback webSettingCallback) {
    this.webSettingCallback = webSettingCallback;
    if (webSettingCallback != null && settings != null) {
      webSettingCallback.setWebSettings(settings);
    }
  }

  private void sureWebViewClient() {
    if (null == mWebViewClient) {
      mWebViewClient = new PascWebViewClient(this);
      this.setWebViewClient(mWebViewClient);
    }
  }

  private void sureWebChromeClient() {
    if (null == mWebChromeClient) {
      mWebChromeClient = new PascWebChromeClient();
      mWebChromeClient.setContext(mContext);
      this.setWebChromeClient(mWebChromeClient);
    }
  }

  /**
   * 获取到CallBackFunction data执行调用并且从数据集移除
   */
  void handlerReturnData(String url) {
    String functionName = BridgeUtil.getFunctionFromReturnUrl(url);
    CallBackFunction f = responseCallbacks.get(functionName);
    String data = BridgeUtil.getDataFromReturnUrl(url);
    if (f != null) {
      f.onCallBack(data);
      responseCallbacks.remove(functionName);
      return;
    }
  }

  @Override
  public void send(String data) {
    send(data, null);
  }

  @Override
  public void send(String data, CallBackFunction responseCallback) {
    doSend(null, data, responseCallback);
  }

  /**
   * 保存message到消息队列
   *
   * @param handlerName handlerName
   * @param data data
   * @param responseCallback CallBackFunction
   */
  private void doSend(String handlerName, String data, CallBackFunction responseCallback) {
    Message m = new Message();
    if (!TextUtils.isEmpty(data)) {
      m.setData(data);
    }
    if (responseCallback != null) {
      String callbackStr = String.format(BridgeUtil.CALLBACK_ID_FORMAT,
          ++uniqueId + (BridgeUtil.UNDERLINE_STR + SystemClock.currentThreadTimeMillis()));
      responseCallbacks.put(callbackStr, responseCallback);
      m.setCallbackId(callbackStr);
    }
    if (!TextUtils.isEmpty(handlerName)) {
      m.setHandlerName(handlerName);
    }

    queueMessage(m);
  }

  /**
   * list<message> != null 添加到消息集合否则分发消息
   *
   * @param m Message
   */
  private void queueMessage(Message m) {
    if (startupMessage != null) {
      startupMessage.add(m);
    } else {
      dispatchMessage(m);
    }
  }

  /**
   * 分发message 必须在主线程才分发成功
   *
   * @param m Message
   */
  void dispatchMessage(Message m) {
    String messageJson = m.toJson();
    boolean logEnable = PascHybrid.getInstance().getHybridInitConfig().isLogEnable();
    if (logEnable) {
      Log.d(TAG, "WebView return data. data=" + messageJson);
    }
    //escape special characters for json string  为json字符串转义特殊字符
    messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
    messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
    messageJson = messageJson.replaceAll("(?<=[^\\\\])(\')", "\\\\\'");
    messageJson = messageJson.replaceAll("%7B", URLEncoder.encode("%7B"));
    messageJson = messageJson.replaceAll("%7D", URLEncoder.encode("%7D"));
    messageJson = messageJson.replaceAll("%22", URLEncoder.encode("%22"));
    String javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson);
    if (logEnable) {
      Log.d(TAG, "Invoke js command. command=" + javascriptCommand);
    }
    // 必须要找主线程才会将数据传递出去 --- 划重点
    if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
      this.loadUrl(javascriptCommand);
    }
  }

  /**
   * 刷新消息队列
   */
  void flushMessageQueue() {
    if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
      loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, new CallBackFunction() {

        @Override
        public void onCallBack(String data) {
          // deserializeMessage 反序列化消息
          List<Message> list = null;
          try {
            list = Message.toArrayList(data);
          } catch (Exception e) {
            e.printStackTrace();
            return;
          }
          if (list == null || list.size() == 0) {
            return;
          }
          for (int i = 0; i < list.size(); i++) {
            Message msg = list.get(i);
            String responseId = msg.getResponseId();
            // 是否是response  CallBackFunction
            if (!TextUtils.isEmpty(responseId)) {
              CallBackFunction function = responseCallbacks.get(responseId);
              String responseData = msg.getResponseData();
              function.onCallBack(responseData);
              responseCallbacks.remove(responseId);
            } else {
              CallBackFunction responseFunction = null;
              // if had callbackId 如果有回调Id
              final String callbackId = msg.getCallbackId();
              if (!TextUtils.isEmpty(callbackId)) {
                responseFunction = new CallBackFunction() {
                  @Override
                  public void onCallBack(String data) {
                    Message responseMsg = new Message();
                    responseMsg.setResponseId(callbackId);
                    responseMsg.setResponseData(data);
                    queueMessage(responseMsg);
                  }
                };
              } else {
                responseFunction = new CallBackFunction() {
                  @Override
                  public void onCallBack(String data) {
                    // do nothing
                  }
                };
              }

              // BehaviorHandler执行
              boolean logEnable = PascHybrid.getInstance().getHybridInitConfig().isLogEnable();
              boolean actionBehaviorResult = actionBehavior(msg, responseFunction);
              if (actionBehaviorResult) {
                if (logEnable) {
                  Log.d(TAG, "Invoke behavior. msg=" + msg.toJson());
                }
              } else {
                if (logEnable) {
                  Log.d(TAG, "Invoke default behavior. msg=" + msg.toJson());
                }
                DefaultBehaviorManager.getInstance()
                    .actionDefaultBehavior(mContext, msg, responseFunction);
              }
            }
          }
        }
      });
    }
  }

  private boolean actionBehavior(Message message, CallBackFunction responseFunction) {
    if (TextUtils.isEmpty(message.getHandlerName())) {
      return false;
    }

    BehaviorHandler handler = mCustomBehaviorCache.get(message.getHandlerName());
    NativeResponse nativeResponse = new NativeResponse();

    if (null == handler) {
      return false;
    }
    nativeResponse.code = BridgeUtil.RESPONSE_CODE_SUCCESS;
    nativeResponse.message = "";
    handler.handler(mContext, message.getData(), responseFunction, nativeResponse);
    return true;
  }

  public void loadUrl(String jsUrl, CallBackFunction returnCallback) {
    this.loadUrl(jsUrl);
    // 添加至 Map<String, CallBackFunction>
    responseCallbacks.put(BridgeUtil.parseFunctionName(jsUrl), returnCallback);
  }

  /**
   * call javascript registered handler
   * 调用javascript处理程序注册
   *
   * @param handlerName handlerName
   * @param data data
   * @param callBack CallBackFunction
   */
  public void callHandler(String handlerName, String data, CallBackFunction callBack) {
    boolean logEnable = PascHybrid.getInstance().getHybridInitConfig().isLogEnable();
    if (logEnable) {
      Log.d(TAG, "WebView call handler. handlerName=" + handlerName + ",data=" + data);
    }
    doSend(handlerName, data, callBack);
  }

  /**
   * 注册 Behavior,javascript可以通过协议名来和本地native进行通讯
   *
   * @param protocolName 行为的协议名 - 由H5和java确定
   * @param handler handler
   */
  public void registerBehavior(String protocolName, BehaviorHandler handler) {
    if (!TextUtils.isEmpty(protocolName) && !mCustomBehaviorCache.containsKey(protocolName)) {
      Log.i(TAG, "registerBehavior: " + protocolName);
      mCustomBehaviorCache.put(protocolName, handler);
    }
  }

  public void clearCustomBehavior() {
    if (null == mCustomBehaviorCache) {
      mCustomBehaviorCache = new HashMap<>(16);
    }
    mCustomBehaviorCache.clear();
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    mOnWebScorollListener.onScrollChanged(t, oldt);
  }

  public void setOnWebScorollListener(OnWebScorollListener listener) {
    this.mOnWebScorollListener = listener;
  }

  public interface OnWebScorollListener {
    void onScrollChanged(int t, int oldt);
  }

  /**
   *      * 使WebView不可滚动
   *      
   */
  @Override
  public void scrollTo(int x, int y) {
    if (WebViewContants.banScroll) {
      super.scrollTo(0, 0);
    }
  }
}
