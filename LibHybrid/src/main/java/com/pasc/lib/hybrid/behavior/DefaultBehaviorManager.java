package com.pasc.lib.hybrid.behavior;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pasc.lib.hybrid.Message;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.R;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.hybrid.util.BridgeUtil;
import com.pasc.lib.hybrid.util.Utils;
import com.pasc.lib.smtbrowser.entity.DeviceBean;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import com.pasc.lib.smtbrowser.entity.NetworkStatusBean;
import com.pasc.lib.smtbrowser.entity.ToastBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import java.lang.String;

public class DefaultBehaviorManager {
  private static final String TAG = DefaultBehaviorManager.class.getSimpleName();
  public static final int ACTION_BEHAVIOR_CONTROLL_TOOLBAR = 1001;
  public static final int ACTION_BEHAVIOR_SHARE = 1002;
  public static final int ACTION_BEHAVIOR_OPEN_NEW_WEBVIEW = 1003;
  public static final int ACTION_BEHAVIOR_CLOSE_WEBVIEW = 1004;
  public static final int ACTION_BEHAVIOR_CLOSE_BACK_HOME = 1005;
  public static final int ACTION_BEHAVIOR_SELECT_CONTACT = 1006;
  public static final int ACTION_BEHAVIOR_GO_BACK = 1007;
  public static final int ACTION_BEHAVIOR_SEND_SMS = 1008;
  public static final int ACTION_BEHAVIOR_NETWORK_STATUS = 1009;
  public static final int ACTION_BEHAVIOR_WEBVIEW_UI = 1010;
  public static final int ACTION_BEHAVIOR_CLOSE_ALL_WEBVIEW = 1011;
  // 功能协议名，对于的功能
  private Map<String, BehaviorHandler> mDefaultBehaviorHandlers = new HashMap<>();
  private Map<String, String> behaviorRemarks = new HashMap<>();
  private SparseArray<Handler> mUiHandlers = new SparseArray<>(16);
  private int uiHandlerKey;
  private BehaviorHandler mDefaultHandler = null;
  private WebPageConfig mWebPageConfig;
  //private HybridInitConfig mHybridInitConfig;
  private Map<String, BehaviorHandler> customerBehaviors;

  public Map<String, String> getBehaviorRemarks() {
    return behaviorRemarks;
  }

  public Map<String, BehaviorHandler> getDefaultBehaviorHandlers() {
    return mDefaultBehaviorHandlers;
  }

  private DefaultBehaviorManager() {
    initDefaultBehaviorHandlers();
  }

  public static DefaultBehaviorManager getInstance() {
    return SingletonHolder.instance;
  }

  private static class SingletonHolder {
    private static final DefaultBehaviorManager instance = new DefaultBehaviorManager();
  }

  public void setUIHandler(Handler handler) {
    mUiHandlers.put(handler.hashCode(), handler);
    uiHandlerKey = handler.hashCode();
  }

  public Handler getUIHandler() {
    Handler mUIHandler = mUiHandlers.get(uiHandlerKey);
    if (mUIHandler == null) {
      throw new RuntimeException("UIHandler is null !!!");
    }
    if (mUIHandler.getLooper().getThread() != Looper.getMainLooper().getThread()) {
      throw new RuntimeException("You must set a Handler for the main thread in setUIHandler()");
    }
    return mUIHandler;
  }

  public void setWebPageConfig(WebPageConfig webPageConfig) {
    mWebPageConfig = webPageConfig;
  }

  public WebPageConfig getWebPageConfig() {
    return mWebPageConfig;
  }

  public Map<String, BehaviorHandler> getCustomerBehaviors() {
    return customerBehaviors;
  }

  public void setCustomerBehaviors(Map<String, BehaviorHandler> customerBehaviors) {
    this.customerBehaviors = customerBehaviors;
  }

  /**
   * 触发行为
   *
   * @author chenshangyong872
   */
  public void actionDefaultBehavior(Context context, Message message,
          CallBackFunction responseFunction) {
    BehaviorHandler handler = null;
    if (!TextUtils.isEmpty(message.getHandlerName())) {
      handler = mDefaultBehaviorHandlers.get(message.getHandlerName());
    }

    NativeResponse nativeResponse = new NativeResponse();

    if (null == handler) {
      handler = sureRegisterDefaultHandler();
      nativeResponse.code = BridgeUtil.RESPONSE_CODE_API_UNUSABLE;
      nativeResponse.message = BridgeUtil.RSPONSE_MSG_API_UNUSABLE;
    } else {
      nativeResponse.code = BridgeUtil.RESPONSE_CODE_SUCCESS;
      nativeResponse.message = "";
    }

    handler.handler(context, message.getData(), responseFunction, nativeResponse);
  }

  public BehaviorHandler sureRegisterDefaultHandler() {
    if (null == mDefaultHandler) {
      mDefaultHandler = new DefaultHandler();
    }
    return mDefaultHandler;
  }

  private void initDefaultBehaviorHandlers() {
    // 这里构建所有的默认BehaviorHandler[包括ui：ActionBar的和功能]
    registerDefaultBehavior(ConstantBehaviorName.TOAST, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {
        Gson gson = new Gson();

        ToastBean toastBean = gson.fromJson(data, ToastBean.class);
        if (!"hide".equals(toastBean.getType())) {

          Toast.makeText(context, toastBean.getText(), Toast.LENGTH_LONG).show();
        }
        function.onCallBack(gson.toJson(response));
      }
    }, "显示吐司");

    registerDefaultBehavior(ConstantBehaviorName.OPEN_NEW_WEBVIEW, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {
        android.os.Message message = getUIHandler().obtainMessage();
        message.what = ACTION_BEHAVIOR_OPEN_NEW_WEBVIEW;
        message.obj = data;
        getUIHandler().sendMessage(message);
        PascHybrid.getInstance().saveCallBackFunction(context.hashCode(),ConstantBehaviorName.OPEN_NEW_WEBVIEW,function);
//        function.onCallBack(new Gson().toJson(response));
      }
    },"打开一个新的WebView并加载网页");

    registerDefaultBehavior(ConstantBehaviorName.CLOSE_WEBVIEW, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {
        android.os.Message message = getUIHandler().obtainMessage();
        message.what = ACTION_BEHAVIOR_CLOSE_WEBVIEW;
        getUIHandler().sendMessage(message);
        function.onCallBack(new Gson().toJson(response));
      }
    },"直接退出当前WebView");

    registerDefaultBehavior(ConstantBehaviorName.CLOSE_ALL_WEBVIEW, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
                                    NativeResponse response) {
        android.os.Message message = getUIHandler().obtainMessage();
        message.what = ACTION_BEHAVIOR_CLOSE_ALL_WEBVIEW;
        getUIHandler().sendMessage(message);
        response.message = "close success";
        function.onCallBack(new Gson().toJson(response));
      }
    },"直接退出所有的WebView");

    registerDefaultBehavior(ConstantBehaviorName.WEBVIEW_GO_BACK, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {
        android.os.Message message = getUIHandler().obtainMessage();
        message.what = ACTION_BEHAVIOR_GO_BACK;
        getUIHandler().sendMessage(message);
        function.onCallBack(new Gson().toJson(response));
      }
    },"返回上一个网页");

    registerDefaultBehavior(ConstantBehaviorName.CONTROLL_TOOLBAR, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {

        android.os.Message message = getUIHandler().obtainMessage();
        message.what = ACTION_BEHAVIOR_CONTROLL_TOOLBAR;
        message.obj = data;
        getUIHandler().sendMessage(message);
        function.onCallBack(new Gson().toJson(response));
      }
    },"设置工具栏");

    registerDefaultBehavior(ConstantBehaviorName.BACK_TO_ROOT_VIEW, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {

        function.onCallBack(new Gson().toJson(response));
      }
    },"跳转回到根页面");

    registerDefaultBehavior(ConstantBehaviorName.CLOSE_WITH_BACK_HOME, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {
        android.os.Message message = getUIHandler().obtainMessage();
        message.what = ACTION_BEHAVIOR_CLOSE_BACK_HOME;
        message.obj = data;
        getUIHandler().sendMessage(message);
        Gson gson = new Gson();
        function.onCallBack(gson.toJson(response));
      }
    },"关闭WebView，回到主页，切换到指定的Tab页");

    registerDefaultBehavior(ConstantBehaviorName.PREVIEW_IMAGE, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {

        function.onCallBack(new Gson().toJson(response));
      }
    },"预览图片");

    registerDefaultBehavior(ConstantBehaviorName.OPEN_MAP_NAVIGATION, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {
        Gson gson = new Gson();
        function.onCallBack(gson.toJson(response));
      }
    },"打开地图导航");

    registerDefaultBehavior(ConstantBehaviorName.STATISTICS_PAGE, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {

        function.onCallBack(new Gson().toJson(response));
      }
    },"页面统计");

    registerDefaultBehavior(ConstantBehaviorName.OPEN_SHARE, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {
        android.os.Message message = getUIHandler().obtainMessage();
        message.what = ACTION_BEHAVIOR_SHARE;
        message.obj = data;
        getUIHandler().sendMessage(message);
        PascHybrid.getInstance()
                .saveCallBackFunction(context.hashCode(), ConstantBehaviorName.OPEN_SHARE,
                        function);
      }
    },"打开分享");

      registerDefaultBehavior(ConstantBehaviorName.SYSTEM_SHARE, new BehaviorHandler() {

          @Override public void handler(Context context, String data, CallBackFunction function,
                                        NativeResponse response) {
              android.os.Message message = getUIHandler().obtainMessage();
              message.what = ACTION_BEHAVIOR_SHARE;
              message.obj = data;
              getUIHandler().sendMessage(message);
              PascHybrid.getInstance()
                      .saveCallBackFunction(context.hashCode(), ConstantBehaviorName.SYSTEM_SHARE,
                              function);
          }
      },"系统分享");

    registerDefaultBehavior(ConstantBehaviorName.LOG_EVENT, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {
        Gson gson = new Gson();
        function.onCallBack(gson.toJson(response));
      }
    },"记录日志");

    registerDefaultBehavior(ConstantBehaviorName.GET_DEVICE_INFO, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {
        if (context == null) {
          return;
        }
        try {
          DeviceBean device = new DeviceBean();
          device.setDeviceName(Build.MODEL);
          device.setSysType(1);
          device.setSysVersion(Build.VERSION.RELEASE);
          device.setAppName(getApplicationName(context));
          device.setAppVersion(context.getPackageManager()
                  .getPackageInfo(context.getPackageName(), 0).versionName);
          device.setAppID(context.getPackageName());
          device.setIDFV("");
          device.setIMEI(Utils.getImei(context));
          device.setHybridVersion(context.getString(R.string.hybrid_version_name));
          device.setChannelID("official");
          response.data = device;
          function.onCallBack(new Gson().toJson(response));
        } catch (PackageManager.NameNotFoundException e) {
          e.printStackTrace();
        }
      }
    },"获取设备信息");

    registerDefaultBehavior(ConstantBehaviorName.BOX_TIPS, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {

        function.onCallBack(new Gson().toJson(response));
      }
    },"显示提示信息");

    registerDefaultBehavior(ConstantBehaviorName.OPEN_CONTACT, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {
        android.os.Message message = getUIHandler().obtainMessage();
        message.what = ACTION_BEHAVIOR_SELECT_CONTACT;
        getUIHandler().sendMessage(message);
        PascHybrid.getInstance()
                .saveCallBackFunction(context.hashCode(), ConstantBehaviorName.OPEN_CONTACT,
                        function);
      }
    },"打开联系人界面");

    registerDefaultBehavior(ConstantBehaviorName.GET_GPS_INFO, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {
        function.onCallBack(new Gson().toJson(response));
      }
    },"获取GPS信息");

    registerDefaultBehavior(ConstantBehaviorName.QR_CODE_SCAN, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {

        PascHybrid.getInstance()
                .saveCallBackFunction(context.hashCode(), ConstantBehaviorName.QR_CODE_SCAN,
                        function);
      }
    },"扫描二维码");

    registerDefaultBehavior(ConstantBehaviorName.SUPPROT_SHARE_PLATFORM, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {

        function.onCallBack(new Gson().toJson(response));
      }
    },"获取已安装的分享平台");

    registerDefaultBehavior(ConstantBehaviorName.NETWORK_STATUS, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {

        android.os.Message message = getUIHandler().obtainMessage();
        message.what = ACTION_BEHAVIOR_NETWORK_STATUS;
        message.obj = data;
        getUIHandler().sendMessage(message);
        NetworkStatusBean networkStatus = new NetworkStatusBean(Utils.getAPNType(context));
        response.data = networkStatus;
        function.onCallBack(new Gson().toJson(response));
      }
    },"获取网络状态");

    registerDefaultBehavior(ConstantBehaviorName.WEB_CALLBACK, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {

        function.onCallBack(new Gson().toJson(response));
      }
    },"Web回调。场景：列表页为原生，详情页为WebView，Web做了某些动作后需要给列表页一个回调，让列表进行刷新");

    registerDefaultBehavior(ConstantBehaviorName.BROADCAST, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {

        function.onCallBack(new Gson().toJson(response));
      }
    },"发送广播");

    registerDefaultBehavior(ConstantBehaviorName.SEND_SMS, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {
        android.os.Message message = getUIHandler().obtainMessage();
        message.what = ACTION_BEHAVIOR_SEND_SMS;
        message.obj = data;
        getUIHandler().sendMessage(message);
        function.onCallBack(new Gson().toJson(response));
      }
    },"发送短信");

    registerDefaultBehavior(ConstantBehaviorName.SUPPORT_API, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {
        boolean isExist = false;
        try {
          JsonObject jsonObject = (JsonObject) new JsonParser().parse(data);
          String str = jsonObject.get("path").getAsString();
          Map<String, BehaviorHandler> tempAllHandlers = new HashMap<>(16);
          tempAllHandlers.putAll(mDefaultBehaviorHandlers);
          tempAllHandlers.putAll(getCustomerBehaviors() != null ? getCustomerBehaviors()
                  : new HashMap<String, BehaviorHandler>(16));
          for (String key : tempAllHandlers.keySet()) {
            if (str.equals(key)) {
              response.message = "";
              response.code = 0;
              isExist = true;
              break;
            }
          }
          if (!isExist) {
            response.message = "";
            response.code = -1;
          }
        } catch (Exception e) {
          response.message = "json parse error";
          response.code = -1;
        }
        function.onCallBack(new Gson().toJson(response));
      }
    },"是否支持交互API(单个查询)");

    registerDefaultBehavior(ConstantBehaviorName.SUPPORT_APIS, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {
        try {
          JsonObject jsonObject = (JsonObject) new JsonParser().parse(data);
          JsonArray jsonArray = (JsonArray) jsonObject.get("paths");
          List<String> tempArray = new ArrayList<>();
          Map<String, BehaviorHandler> tempAllHandlers = new HashMap<>(16);
          tempAllHandlers.putAll(mDefaultBehaviorHandlers);
          tempAllHandlers.putAll(getCustomerBehaviors() != null ? getCustomerBehaviors()
                  : new HashMap<String, BehaviorHandler>(16));
          for (int i = 0; i < jsonArray.size(); i++) {
            String str = jsonArray.get(i).getAsString();
            if (!tempAllHandlers.containsKey(str)) {
              tempArray.add(str);
            }
          }
          if (tempArray.size() == 0) {
            response.message = "";
            response.code = 0;
          } else {
            for (int i = 0; i < tempArray.size(); i++) {
              if (i == tempArray.size() - 1) {
                response.message += tempArray.get(i);
              } else {
                response.message += tempArray.get(i) + ",";
              }
            }
            response.code = -1;
          }
        } catch (Exception e) {
          response.message = "json parse error";
          response.code = -1;
        }
        function.onCallBack(new Gson().toJson(response));
      }
    },"是否支持交互API(多个查询)");

    registerDefaultBehavior(ConstantBehaviorName.WEBVIEW_UI, new BehaviorHandler() {

      @Override public void handler(Context context, String data, CallBackFunction function,
              NativeResponse response) {
        android.os.Message message = getUIHandler().obtainMessage();
        message.what = ACTION_BEHAVIOR_WEBVIEW_UI;
        message.obj = data;
        getUIHandler().sendMessage(message);
        function.onCallBack(new Gson().toJson(response));
      }
    },"设置WebViewUi");
  }

  /**
   * 注册 Behavior,javascript可以通过协议名来和本地native进行通讯
   *
   * @param protocolName 行为的协议名 - 由H5和java确定
   * @param handler handler
   * @deprecated
   * @see #registerDefaultBehavior(String, BehaviorHandler, String)
   */
  private void registerDefaultBehavior(String protocolName, BehaviorHandler handler) {
    if (!TextUtils.isEmpty(protocolName) && !mDefaultBehaviorHandlers.containsKey(protocolName)) {
      Log.i(TAG, "registerDefaultBehavior: " + protocolName);
      mDefaultBehaviorHandlers.put(protocolName, handler);
    }
  }

  private void registerDefaultBehavior(String protocolName, BehaviorHandler handler, String remark){
    if (!TextUtils.isEmpty(protocolName) && !mDefaultBehaviorHandlers.containsKey(protocolName)) {
      Log.i(TAG, "registerDefaultBehavior: " + protocolName);
      mDefaultBehaviorHandlers.put(protocolName, handler);
      behaviorRemarks.put(protocolName,remark);
    }
  }

  public String getApplicationName(Context context) {
    PackageManager packageManager = null;
    ApplicationInfo applicationInfo = null;
    try {
      packageManager = context.getPackageManager();
      applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      applicationInfo = null;
    }
    String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);

    return applicationName;
  }

  public void destroyHandler(Handler handler) {
    if (mUiHandlers.get(handler.hashCode()) != null) {
      mUiHandlers.get(handler.hashCode()).removeCallbacksAndMessages(null);
      mUiHandlers.remove(handler.hashCode());
    }
  }
}
