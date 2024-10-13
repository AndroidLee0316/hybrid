package com.pasc.lib.hybrid;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.hybrid.behavior.DefaultBehaviorManager;
import com.pasc.lib.hybrid.behavior.WebPageConfig;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.hybrid.callback.NetworkStatusCallback;
import com.pasc.lib.hybrid.listener.KeyboardListener;
import com.pasc.lib.hybrid.nativeability.WebStrategy;
import com.pasc.lib.hybrid.nativeability.WebStrategyType;
import com.pasc.lib.hybrid.util.NetWorkStateReceiver;
import com.pasc.lib.hybrid.util.NetWorkUtils;
import com.pasc.lib.hybrid.util.StatusBarUtils;
import com.pasc.lib.hybrid.util.Utils;
import com.pasc.lib.hybrid.util.WebViewVirtualBoardAndInputTools;
import com.pasc.lib.hybrid.webview.WebViewContants;
import com.pasc.lib.hybrid.widget.CommonDialog;
import com.pasc.lib.hybrid.widget.ServiceLoadDialog;
import com.pasc.lib.hybrid.widget.WebCommonTitleView;
import com.pasc.lib.smtbrowser.entity.NetworkStatusBean;
import com.pasc.lib.smtbrowser.entity.OpenNewWebBean;
import com.pasc.lib.smtbrowser.entity.SelectContactBean;
import com.pasc.lib.smtbrowser.entity.SendSMSBean;
import com.pasc.lib.smtbrowser.entity.ToolbarBeanNew;
import com.pasc.lib.smtbrowser.entity.WebShareBean;
import com.pasc.lib.smtbrowser.entity.WebViewUIBean;
import com.pasc.lib.smtbrowser.util.BrowserUtils;
import com.pasc.lib.smtbrowser.view.CustomPopup;
import com.tbruyelle.rxpermissions2.RxPermissions;
import io.reactivex.functions.Consumer;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

final public class PascWebviewActivity extends FragmentActivity implements NetworkStatusCallback {

  /**
   * hybrid打开的webviewActivity列表，用于一次性关闭所有打开的webViewActivity接口
   */
  public static List<SoftReference<Activity>> webViewHistoryList = new ArrayList<>(2);
  /**
   * 本webViewActivity，软引用
   */
  private SoftReference<Activity> srActivity;

  public static final int REQUEST_CODE_FILE_SELECT = 0x1000;
  public static final int REQUEST_CODE_LOGIN = 0x1001;
  public static final int REQUEST_CODE_ADDADDRESS = 0x1002;
  public static final int REQUEST_CODE_CONTACT = 0x2000;
  public static final int TITLE_PLACE_HODLER = 1;
  public static final int TITLE_FRIST_PAGE = 2;
  public static final int TITLE_HOLE_WEBVIEW = 3;
  private final static String EXTRA_URL_FLG = "extra_url";
  private final static String EXTRA_STRATEGY_FLG = "extra_strategy";
  private static final String TAG = "PASC_HYBRID";

  private Context mContext;
  public PascWebviewFragment mWebviewFragment;
  public WebCommonTitleView mCommonTitleView;
  public View titleBarView;
  private CustomPopup customPopup;

  public ToolbarBeanNew toolbarTitleBean;

  private JSONObject toolbarBeanJson = new JSONObject();

  private WebViewHandler mHandler;
  private String url;
  //策略
  public @Nullable WebStrategy webStrategy;
  public int webStrategyKey;

  private NetWorkStateReceiver netWorkStateReceiver;
  private String networkAction;
  public int titleLevel;
  FrameLayout webViewContainer;

  public boolean stopJs = true; //web页面开启新activity跳转web页面时，防止前一个页面停止了js影响后一个页面
  private KeyboardListener keyboardListener; // 键盘监听
  private HashMap<String, KeyboardListener.OnKeyboardListener> keyboardListenerMap =
      new HashMap<>();

  public void addKeyboardListener(String key,
      KeyboardListener.OnKeyboardListener keyboardListener) {
    keyboardListenerMap.put(key, keyboardListener);
  }

  // 启动WebviewActivity
  public static void startWebviewActivity(final Context context, final String url) {
    Intent intent = new Intent(context, PascWebviewActivity.class);
    intent.putExtra(EXTRA_URL_FLG, url);
    context.startActivity(intent);
  }

  // 启动WebviewActivity带策略
  public static void startWebviewActivity(final Context context, WebStrategy strategy,
      int strategyKey) {
    Intent intent = new Intent(context, PascWebviewActivity.class);
    intent.putExtra(EXTRA_STRATEGY_FLG, strategyKey);
    if (strategy.activityStartModle == WebStrategyType.FLAG_ACTIVITY_NEW_TASK) {
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    context.startActivity(intent);
  }

  /**
   * 启动WebviewActivity带策略，
   *  + 带回调，解决 openNewWebview 接口没有activity finish 回调的问题
   * @param context
   * @param strategy
   * @param strategyKey
   * @param requestCode
   */

  public static void startWebviewActivityForResult(final Activity context, WebStrategy strategy,
                                          int strategyKey, int requestCode) {
    Intent intent = new Intent(context, PascWebviewActivity.class);
    intent.putExtra(EXTRA_STRATEGY_FLG, strategyKey);
    if (strategy.activityStartModle == WebStrategyType.FLAG_ACTIVITY_NEW_TASK) {
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    context.startActivityForResult(intent, requestCode);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = this;
    srActivity = new SoftReference<>(this);
    webViewHistoryList.add(srActivity);
    ActionBar actionBar = getActionBar();
    if (actionBar != null) {
      actionBar.hide();
    }

    //BrowserUtils.setStatusBarTxColor(this, true);
    int sdkInt = Build.VERSION.SDK_INT;
    Log.d("PASC_HYBRID", "当前的sdk版本号是：" + sdkInt);
    StatusBarUtils.setTransparentForWindow(this); // 统一都是沉浸式，非沉浸式添加间隔即可。因为两种状态需要互相切换

    setContentView(R.layout.activity_pasc_webview);
    titleBarView = findViewById(R.id.titleBarView);
    int statusBarHeight = StatusBarUtils.getStatusBarHeight(this);
    Log.d(TAG,"状态栏高度："+statusBarHeight);
    titleBarView.setPadding(0,statusBarHeight, 0,0);

    if (savedInstanceState != null) {
      try {
        WebPageConfig webPageConfig =
            (WebPageConfig) savedInstanceState.getSerializable("webConfig");
        if (webPageConfig != null) {
          DefaultBehaviorManager.getInstance().setWebPageConfig(webPageConfig);
        }
        url = savedInstanceState.getString(EXTRA_URL_FLG);
        if (!TextUtils.isEmpty(url)) {
          getIntent().putExtra(EXTRA_URL_FLG, url + "");
        }
        webStrategyKey = savedInstanceState.getInt(EXTRA_STRATEGY_FLG);
        getIntent().putExtra(EXTRA_STRATEGY_FLG, webStrategyKey);
      } catch (RuntimeException e) {
        e.printStackTrace();
      }
    }

    keyboardListener = new KeyboardListener(this);
    keyboardListener.setOnKeyboardListener(new KeyboardListener.OnKeyboardListener() {
      @Override
      public void onKeyboardOpened(int keyboardHeight) {
        Collection<KeyboardListener.OnKeyboardListener> keyboardListeners =
            keyboardListenerMap.values();
        for (KeyboardListener.OnKeyboardListener listener : keyboardListeners) {
          listener.onKeyboardOpened(keyboardHeight);
        }
      }

      @Override
      public void onKeyboardChanged(int keyboardHeight) {
        Collection<KeyboardListener.OnKeyboardListener> keyboardListeners =
            keyboardListenerMap.values();
        for (KeyboardListener.OnKeyboardListener listener : keyboardListeners) {
          listener.onKeyboardChanged(keyboardHeight);
        }
      }

      @Override
      public void onKeyboardClose() {
        Collection<KeyboardListener.OnKeyboardListener> keyboardListeners =
            keyboardListenerMap.values();
        for (KeyboardListener.OnKeyboardListener listener : keyboardListeners) {
          listener.onKeyboardClose();
        }
      }
    });

    initHandler();
    initWebview();

    WebViewVirtualBoardAndInputTools.assistActivity(this); // 解决遮挡输入框问题

  }

  public void setStatusBarBgColor(int statusColor) {
    titleBarView.setBackgroundColor(statusColor);
  }

  public void setStatusBarVisibility(int visibility) {
    titleBarView.setVisibility(visibility);
  }

  @SuppressLint("IncorrectToast")
  public void initWebview() {

    Intent intent = getIntent();
    if (null != intent) {
      webStrategyKey = intent.getIntExtra(EXTRA_STRATEGY_FLG, 0);
      webStrategy = PascHybrid.getInstance().webStrategyMap.get(webStrategyKey);
      if (webStrategy == null) {
        url = intent.getStringExtra(EXTRA_URL_FLG);
      } else {
        url = webStrategy.url;
      }
      if (null != url) {
        if (url.contains("http://ntgsc-smt-web.pingan.com.cn/") || url.contains(
            "http://smt-web-stg.pingan.com.cn/") || url.contains(
            "https://ntgsc-smt-web.nantong.cn/") || url.contains(
            "http://ntgsc-smt-web-stg.nantong.cn/")) {
          // 添加新的参数
          try {
            Utils.appendUri(url, "openweb=paschybrid");
          } catch (URISyntaxException e) {
            e.printStackTrace();
          }
        }
      } else {
        Toast.makeText(this, "url为null", Toast.LENGTH_SHORT).show();
        url = "about:blank";
      }
    }

    webViewContainer = findViewById(getContentId());
    Fragment fragment = getSupportFragmentManager().findFragmentById(getContentId());
    if (fragment == null) {
      fragment = new PascWebviewFragment();
      Bundle bundle = getIntent().getExtras();
      fragment.setArguments(bundle);
      showFragment(getContentId(), fragment);
    }

    mWebviewFragment = (PascWebviewFragment) fragment;
    String urlTmp = url;
    try {
      urlTmp = Utils.getDeleteParamUri(url);
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    mWebviewFragment.loadUrl(urlTmp);

    initToolbar();

    try {
      if (!TextUtils.isEmpty(Utils.getUiparam(url))) {
        String uiJson = URLDecoder.decode(Utils.getUiparam(url), "UTF-8");
        Gson gson = new Gson();
        toolbarTitleBean = gson.fromJson(getNewToolbarJson(uiJson), ToolbarBeanNew.class);
      }
    } catch (URISyntaxException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (RuntimeException e) {
      e.printStackTrace();
    }

    if (toolbarTitleBean == null) {
      toolbarTitleBean = new ToolbarBeanNew();
    }
    updateToolbar();
    showServiceLoadDialog();
  }

  private void showServiceLoadDialog() {
    if (webStrategy == null) {
      return;
    }
    if ("1".equalsIgnoreCase(webStrategy.isNotice)) {
      //显示第三方服务跳转页面
      ServiceLoadDialog.Builder builder = new ServiceLoadDialog.Builder();
      if (!TextUtils.isEmpty(webStrategy.description)) {
        builder.setDescription(webStrategy.description);
      }
      if (!TextUtils.isEmpty(webStrategy.serviceProvider)) {
        builder.setContent(webStrategy.serviceProvider);
      }
      builder.setDelay(webStrategy.pageShowDuration);
      builder.setCallback(new ServiceLoadDialog.ICallback() {
        @Override public void delayFinish() {

        }

        @Override public void onBack() {
           finish();
        }
      });
      new ServiceLoadDialog(mContext, builder).show();
    }
  }

  private String getNewToolbarJson(String uiJson) {
    try {
      if (!TextUtils.isEmpty(uiJson)) {
        JSONObject newJson = new JSONObject(uiJson);
        Iterator<String> keys = newJson.keys();
        while (keys.hasNext()) {
          String key = keys.next();
          Object value = newJson.opt(key);
          toolbarBeanJson.put(key, value);
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return toolbarBeanJson.toString();
  }

  public void initWebStrategy() {
    if (webStrategy == null) {
      return;
    }
    if (webStrategy.title != null) {
      mCommonTitleView.setTitleText(webStrategy.title);
    }

    if (webStrategy.toolBarColor != null) {
      mCommonTitleView.setToolBarColor(webStrategy.toolBarColor);
    }

    if (webStrategy.titleTextColor != null) {
      mCommonTitleView.setTitleTextColor(Color.parseColor(webStrategy.titleTextColor));
      mCommonTitleView.setLeftTextColor(Color.parseColor(webStrategy.titleTextColor));
    }

    if (webStrategy.titleBold == WebStrategyType.TITLT_BOLD) {
      TextPaint tp = mCommonTitleView.getTitleTV().getPaint();
      tp.setFakeBoldText(true);
    }

    if (webStrategy.toolbarDivider == WebStrategyType.TOOLBAR_DIVIDER_VISIBLE) {
      mCommonTitleView.setUnderLineVisible(true);
    }

    if (webStrategy.backIconColor == WebStrategyType.BACKICONCOLOR_WHITE) {
      mCommonTitleView.setBackDrawableLeft(R.drawable.paschybrid_ic_back_white);
    }

    if (webStrategy.oldPhoneIcon == WebStrategyType.OLD_PHONEICON_ON) {
      mCommonTitleView.setRightImageVisible(View.VISIBLE);
      mCommonTitleView.setRightDrawableRight(R.drawable.paschybrid_ic_phone);
      mCommonTitleView.setOnRightImageClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          new CommonDialog(mContext).setContent("(0513) 12345")
              .setButton1("取消")
              .setButton2("呼叫", CommonDialog.Blue_4d73f4)
              .setOnButtonClickListener(new CommonDialog.OnButtonClickListener() {
                @Override
                public void button2Click() {
                  Intent intent =
                      new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "051312345"));
                  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  startActivity(intent);
                }
              })
              .show();
        }
      });
    }

    if (webStrategy.toolBarVisibility == WebStrategyType.TOOLBAR_GONE) {
      mCommonTitleView.setVisibility(View.GONE);
      /*mCommonTitleView.setVisibility(View.GONE);
      ViewGroup.MarginLayoutParams params =
          new ViewGroup.MarginLayoutParams(webViewContainer.getLayoutParams());
      params.topMargin = 0;
      webViewContainer.setLayoutParams(params);*/
    } else if (webStrategy.toolBarVisibility == WebStrategyType.TOOLBAR_VISIBLE) {
      mCommonTitleView.setVisibility(View.VISIBLE);
      /*mCommonTitleView.setVisibility(View.VISIBLE);
      FrameLayout.LayoutParams params =
          new FrameLayout.LayoutParams(webViewContainer.getLayoutParams());
      params.topMargin = Utils.dp2px(44);
      webViewContainer.setLayoutParams(params);*/
    }
  }

  protected void showFragment(int resId, Fragment fg) {
    showFragment(resId, fg, false);
  }

  protected void showFragment(int resId, Fragment fg, boolean addToBackStack) {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(resId, fg);
    if (addToBackStack) {
      ft.addToBackStack(null);
    }
    ft.commitAllowingStateLoss();
  }

  private int getContentId() {
    return R.id.fl_container_webview;
  }

  public void initToolbar() {
    //处理toolbar
    mCommonTitleView = findViewById(R.id.common_title);
    mCommonTitleView.setOnLeftClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onBackPressed();
      }
    }).setOnRightImageClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

      }
    }).setOnLeftTextClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    /**
     * 设置toolbar文字和图标的整体颜色风格
     */
    if (null != PascHybrid.getInstance().mHybridInitConfig
        && null != PascHybrid.getInstance().mHybridInitConfig.getHybridInitCallback()) {
      Drawable drawableLeft = getResources().getDrawable(R.drawable.paschybrid_ic_close);
      String color =
          PascHybrid.getInstance().mHybridInitConfig.getHybridInitCallback().themeColorString();
      try {
        if (null != color) {
          mCommonTitleView.setLeftTextColor(Color.parseColor(color));
          mCommonTitleView.setTitleTextColor(Color.parseColor(color));
          mCommonTitleView.setRightTextColor(Color.parseColor(color));
          mCommonTitleView.setSubTitleColor(Color.parseColor(color));
          mCommonTitleView.getLeftIv().setColorFilter(Color.parseColor(color));
          mCommonTitleView.getRightIv().setColorFilter(Color.parseColor(color));
          drawableLeft.setColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_ATOP);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      //控制关闭按钮显示文字还是图标
      if (PascHybrid.getInstance().getHybridInitConfig().getHybridInitCallback()
          .titleCloseStyle() == WebStrategyType.CLOSEBUTTON_IMAGE) {
        mCommonTitleView.getLeftTV().setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
            null, null, null);
        mCommonTitleView.getLeftTV().setText("");
      } else {
        mCommonTitleView.getLeftTV().setCompoundDrawablesWithIntrinsicBounds(null,
            null, null, null);
        mCommonTitleView.getLeftTV().setText("关闭");
      }

      if (PascHybrid.getInstance().mHybridInitConfig.getHybridInitCallback().titleCloseButton()
          == WebStrategyType.CLOSEBUTTON_ALWAKES_GONE
          || PascHybrid.getInstance().mHybridInitConfig.getHybridInitCallback()
          .titleCloseButton() == WebStrategyType.CLOSEBUTTON_FRISTPAGE_GONE) {
        //第一次进来不显示或者一直不显示关闭文字
        mCommonTitleView.setLeftTextVisibility(View.GONE);
      } else if (PascHybrid.getInstance().mHybridInitConfig.getHybridInitCallback()
          .titleCloseButton() == WebStrategyType.CLOSEBUTTON_ALWAKES_VISIBLE) {
        //永远显示关闭文字
        mCommonTitleView.setLeftTextVisibility(View.VISIBLE);
      }
    }

    initWebStrategy();
  }

  @SuppressLint("HandlerLeak")
  private void initHandler() {
    if (null == mHandler) {
      mHandler = new WebViewHandler(this) {
        @Override
        public void handleMessage(Message msg) {
          Gson gson = new Gson();
          String data = null;
          switch (msg.what) {
            case DefaultBehaviorManager.ACTION_BEHAVIOR_CONTROLL_TOOLBAR:
              data = (String) msg.obj;

              try {
                toolbarTitleBean = gson.fromJson(getNewToolbarJson(data), ToolbarBeanNew.class);
                updateToolbar();
              } catch (RuntimeException e) {
                e.printStackTrace();
              }

              break;
            case DefaultBehaviorManager.ACTION_BEHAVIOR_SHARE:
              data = (String) msg.obj;
              WebShareBean shareBean = gson.fromJson(data, WebShareBean.class);
              StringBuilder builder = new StringBuilder();
              builder.append(shareBean.getTitle())
                  .append(shareBean.getContent())
                  .append(shareBean.getShareUrl());
              Intent intent = new Intent(Intent.ACTION_SEND);
              // 分享发送的数据类型
              intent.setType("text/plain");
              // 分享的内容
              intent.putExtra(Intent.EXTRA_TEXT, builder.toString());
              // 目标应用选择对话框的标题
              mContext.startActivity(Intent.createChooser(intent, "分享到"));
              break;
            case DefaultBehaviorManager.ACTION_BEHAVIOR_OPEN_NEW_WEBVIEW:
              data = (String) msg.obj;
              OpenNewWebBean openNewWeb = gson.fromJson(data, OpenNewWebBean.class);
              //stopJs = false;
              PascHybrid.getInstance()
                  .with(DefaultBehaviorManager.getInstance().getWebPageConfig())
                  .startForResult((Activity) mContext, new WebStrategy().setUrl(openNewWeb.url)
                      .setToolBarVisibility(openNewWeb.hideNavBar ? WebStrategyType.TOOLBAR_GONE
                          : WebStrategyType.TOOLBAR_VISIBLE),PascWebviewFragment.REQUEST_CODE_OPEN_NEW_WEBVIEW);

              if (openNewWeb.closeCurWeb) {
                PascWebviewActivity.this.finish();
              }
              break;
            case DefaultBehaviorManager.ACTION_BEHAVIOR_CLOSE_WEBVIEW:
              PascWebviewActivity.this.finish();
              break;
            case DefaultBehaviorManager.ACTION_BEHAVIOR_CLOSE_ALL_WEBVIEW:
              if (webViewHistoryList != null && webViewHistoryList.size() > 0){
                Log.e(TAG,"webViewHistoryList size : " + webViewHistoryList.size());
                for (SoftReference<Activity> sr : webViewHistoryList){
                  if (sr != null && sr.get() != null){
                    sr.get().finish();
                  }
                }
                webViewHistoryList.clear();
              }
              break;
            case DefaultBehaviorManager.ACTION_BEHAVIOR_CLOSE_BACK_HOME:
              PascWebviewActivity.this.finish();
              break;
            case DefaultBehaviorManager.ACTION_BEHAVIOR_SELECT_CONTACT:
              checkContactPermission();
              break;
            case DefaultBehaviorManager.ACTION_BEHAVIOR_GO_BACK:
              if (mWebviewFragment != null) {
                mWebviewFragment.onBackPressed();
              }
              break;
            case DefaultBehaviorManager.ACTION_BEHAVIOR_SEND_SMS:
              data = (String) msg.obj;
              SendSMSBean smsBean = gson.fromJson(data, SendSMSBean.class);
              StringBuilder phoneNums = new StringBuilder();
              if (smsBean.recipients != null) {
                for (int i = 0; i < smsBean.recipients.size(); i++) {
                  phoneNums.append(smsBean.recipients.get(i)).append(",");
                }
              }
              BrowserUtils.sendSMS(mContext, phoneNums.toString(), smsBean.message);
              break;
            case DefaultBehaviorManager.ACTION_BEHAVIOR_NETWORK_STATUS:
              networkAction = (String) msg.obj;
              break;
            case DefaultBehaviorManager.ACTION_BEHAVIOR_WEBVIEW_UI:
              data = (String) msg.obj;
              WebViewUIBean webViewUIBean = gson.fromJson(data, WebViewUIBean.class);
              if (webViewUIBean.webViewBackgroundColor != null) {
                mWebviewFragment.mWebView.setBackgroundColor(
                    Color.parseColor(webViewUIBean.webViewBackgroundColor));
              }
              WebViewContants.banAlart = webViewUIBean.banAlert;
              WebViewContants.banScroll = webViewUIBean.banScroll;
              if (webViewUIBean.progressColor != null) {
                Utils.setColors(mWebviewFragment.mProgressbar, 0xffffffff,
                    Color.parseColor(webViewUIBean.progressColor));
              }
              int scrolly = (int) (mWebviewFragment.mWebView.getContentHeight()
                  * mWebviewFragment.mWebView.getScale()
                  * webViewUIBean.verticalOffset);
              mWebviewFragment.mWebView.scrollTo(0, scrolly);
              break;
          }
        }
      };
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    if (requestCode == REQUEST_CODE_FILE_SELECT
        || requestCode == REQUEST_CODE_LOGIN
        || requestCode == REQUEST_CODE_ADDADDRESS) {
      if (intent == null || PascHybrid.getInstance().activityResultCallback == null) {
        return;
      }
      PascHybrid.getInstance().activityResultCallback.activityResult(requestCode, resultCode,
          intent);
    }
    if (requestCode == REQUEST_CODE_CONTACT && resultCode == RESULT_OK) {
      if (intent == null) {
        return;
      }
      //处理返回的data,获取选择的联系人信息
      try {
        Uri uri = intent.getData();
        String[] contacts = Utils.getPhoneContacts(mContext, uri);
        SelectContactBean contactBean = new SelectContactBean(contacts[0], contacts[1]);
        PascHybrid.getInstance()
            .triggerCallbackFunction(ConstantBehaviorName.OPEN_CONTACT, contactBean);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return;
    }

    //这段代码加在这里，是因为通过openNewWebview打开页面需要回调，为了尽量减少对以前逻辑对影响
    //就写了这个if在这里
    if (requestCode == PascWebviewFragment.REQUEST_CODE_OPEN_NEW_WEBVIEW){
      if (PascHybrid.getInstance().activityResultCallback != null){
        PascHybrid.getInstance().activityResultCallback.activityResult(requestCode, resultCode,
                intent);
      }
      if (mWebviewFragment != null) {
        mWebviewFragment.onActivityResult(requestCode, resultCode, intent);
      }
      return;
    }


    if (PascHybrid.getInstance().activityResultCallback == null) {
      return;
    } else {
      PascHybrid.getInstance().activityResultCallback.activityResult(requestCode, resultCode,
          intent);
    }

    if (mWebviewFragment != null) {
      mWebviewFragment.onActivityResult(requestCode, resultCode, intent);
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(EXTRA_URL_FLG, url);
    WebPageConfig webPageConfig = DefaultBehaviorManager.getInstance().getWebPageConfig();
    try {
      outState.putSerializable("webConfig", webPageConfig);
      outState.putSerializable(EXTRA_STRATEGY_FLG, webStrategyKey);
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
  }

  @Override
  protected void onResume() {
    super.onResume();
    //开启js
    if (mWebviewFragment != null && mWebviewFragment.mWebView != null) {
      mWebviewFragment.mWebView.resumeTimers();
      mWebviewFragment.mWebView.onResume();
    }
    DefaultBehaviorManager.getInstance().setUIHandler(mHandler);
    if (netWorkStateReceiver == null) {
      netWorkStateReceiver = new NetWorkStateReceiver(this);
    }
    IntentFilter filter = new IntentFilter();
    filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    registerReceiver(netWorkStateReceiver, filter);
  }

  @Override
  protected void onPause() {
    super.onPause();
    //关闭js
    if (mWebviewFragment != null && mWebviewFragment.mWebView != null && stopJs) {
      mWebviewFragment.mWebView.onPause();
      mWebviewFragment.mWebView.pauseTimers();
    }
    if (netWorkStateReceiver != null) {
      unregisterReceiver(netWorkStateReceiver);
    }
  }

  @Override
  public void onBackPressed() {
    if (!NetWorkUtils.isNetworkConnected(mContext)) {
      toolbarTitleBean = null;
    }
    if (null != toolbarTitleBean && null != toolbarTitleBean.getLeftBtns()) {
      if (toolbarTitleBean.getLeftBtns().size() > 0) {
        ToolbarBeanNew.BtnOpts btnOpts = toolbarTitleBean.getLeftBtns().get(0);

        if (null != btnOpts && !TextUtils.isEmpty(btnOpts.getAction())) {
          mWebviewFragment.callHandler(btnOpts.getAction(), "", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {

            }
          });
          return;
        }
      }
    }
    if (mWebviewFragment != null) {
      mWebviewFragment.onBackPressed();
    }
  }

  @Override
  public void finish() {
    if (this.getCurrentFocus() != null && this.getCurrentFocus().getWindowToken() != null) {
      InputMethodManager manager =
          (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
      if (null != manager) {
        manager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
      }
    }
    super.finish();
  }

  @Override
  protected void onDestroy() {
    if (keyboardListener != null) {
      keyboardListener.removeGlobalLayoutListener();
    }

    super.onDestroy();
    if (null != mHandler) {
      mHandler.removeCallbacksAndMessages(null);
    }
    DefaultBehaviorManager.getInstance().destroyHandler(mHandler);
    PascHybrid.getInstance().removeCurrentParams(PascWebviewActivity.this.hashCode());
    //解决org.chromium.android_webview.AwContents.isDestroyed问题
    try {
      ViewParent parent = mWebviewFragment.mWebView.getParent();
      if (parent != null) {
        ((ViewGroup) parent).removeView(mWebviewFragment.mWebView);
      }

      mWebviewFragment.mWebView.stopLoading();
      // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
      mWebviewFragment.mWebView.getSettings().setJavaScriptEnabled(false);
      mWebviewFragment.mWebView.clearView();
      mWebviewFragment.mWebView.removeAllViews();
      mWebviewFragment.mWebView.clearHistory();
      mWebviewFragment.mWebView.destroy();
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (PascHybrid.getInstance().webActivityDestroyCallback != null) {
      PascHybrid.getInstance().webActivityDestroyCallback.webActivityDestroy();
    }

    webViewHistoryList.remove(srActivity);
  }

  private boolean isToolBarHide() {
    if (toolbarTitleBean == null) {
      return false;
    }
    return toolbarTitleBean.isHide();
  }

  /**
   * web 控制 toolbar
   */
  public void updateToolbar() {
    if (isToolBarHide()) {
      mCommonTitleView.setVisibility(View.GONE);
    } else {
      mCommonTitleView.setVisibility(View.VISIBLE);

      if (toolbarTitleBean.isHideBottomLine()) {
        mCommonTitleView.setUnderLineVisible(false);
      } else {
        mCommonTitleView.setUnderLineVisible(true);
      }
      if (!TextUtils.isEmpty(toolbarTitleBean.getBackgroundColor())) {
        mCommonTitleView.setBackgroundColor(Color.parseColor(toolbarTitleBean.getBackgroundColor()));
      }
      if (toolbarTitleBean.getPlaceholderTitle() != null) {
        titleLevel = TITLE_PLACE_HODLER;
        mCommonTitleView.setTitleText(toolbarTitleBean.getPlaceholderTitle());
      }
      if (toolbarTitleBean.getTitle() != null) {
        titleLevel = TITLE_FRIST_PAGE;
        //防止两种title设置方式互相干扰
        if (webStrategy != null) {
          webStrategy.title = toolbarTitleBean.getTitle();
        }
        mCommonTitleView.setTitleText(toolbarTitleBean.getTitle());
      }
      if (toolbarTitleBean.getWebViewTitle() != null) {
        titleLevel = TITLE_HOLE_WEBVIEW;
        //防止两种title设置方式互相干扰
        if (webStrategy != null) {
          webStrategy.title = toolbarTitleBean.getWebViewTitle();
        }
        mCommonTitleView.setTitleText(toolbarTitleBean.getWebViewTitle());
      }
      if (!TextUtils.isEmpty(toolbarTitleBean.getTitleTextColor())) {
        mCommonTitleView.setTitleTextColor(Color.parseColor(toolbarTitleBean.getTitleTextColor()));
      }
      if (toolbarTitleBean.getTitleTextSize() > 0) {
        mCommonTitleView.setTitleTextSize(toolbarTitleBean.getTitleTextSize());
      }
      if (!TextUtils.isEmpty(toolbarTitleBean.getStatusBarBackgroundColor())) {
        setStatusBarBgColor(Color.parseColor(toolbarTitleBean.getStatusBarBackgroundColor()));
      }
      if (!TextUtils.isEmpty(toolbarTitleBean.getSubtitle())) {
        mCommonTitleView.setSubTitleText(toolbarTitleBean.getSubtitle());
        if (toolbarTitleBean.getSubtitleTextSize() > 0) {
          mCommonTitleView.setSubTitleSize(toolbarTitleBean.getSubtitleTextSize());
        }
        if (!TextUtils.isEmpty(toolbarTitleBean.getSubtitleTextColor())) {
          mCommonTitleView.setSubTitleColor(Color.parseColor(toolbarTitleBean.getSubtitleTextColor()));
        }
      }
    }

    BrowserUtils.setStatusBarTxColor(this, ToolbarBeanNew.STATUS_BAR_STYLE_DARK == toolbarTitleBean.getStatusBarStyle());

    //获取沉浸状态栏
    if (toolbarTitleBean.isWebImmersive()) {
      titleBarView.setPadding(0, 0, 0, 0);
    } else {
      titleBarView.setPadding(0, StatusBarUtils.getStatusBarHeight(this), 0, 0);
    }
    //获取渐变色
    if (toolbarTitleBean.getGradientBackgroundColors() != null
        && toolbarTitleBean.getGradientBackgroundColors().size() == 2) {
      mCommonTitleView.setTopViewHeight(Utils.getStatusBarHeight(this));
      mCommonTitleView.setToolBarColor(toolbarTitleBean.getGradientBackgroundColors(),
          toolbarTitleBean.getGradientDirection());
    } else {
      mCommonTitleView.setTopViewHeight(0);
    }
    List<ToolbarBeanNew.BtnOpts> leftBtns = toolbarTitleBean.getLeftBtns();
    if (null != leftBtns) {
      if (leftBtns.size() > 0) {
        for (ToolbarBeanNew.BtnOpts opts : leftBtns) {
          if (opts.getIconType() == 0) {
            mCommonTitleView.setBackDrawableVisible(View.VISIBLE);
          }

          if (!TextUtils.isEmpty(opts.getIconUrl())) {
            mCommonTitleView.setLeftIvResource(opts.getIconUrl());
          } else {
            if ("#ffffff".equals(opts.getColor())) {
              mCommonTitleView.setBackDrawableLeft(R.drawable.paschybrid_ic_back_white);
            } else {
              mCommonTitleView.setBackDrawableLeft(R.drawable.paschybrid_ic_back_blue);
            }
          }
          if (!TextUtils.isEmpty(opts.getTitle())) {
            if (!TextUtils.isEmpty(opts.getColor())) {
              mCommonTitleView.setLeftTextColor(Color.parseColor(opts.getColor()));
            }
            mCommonTitleView.setLeftText(opts.getTitle());
            mCommonTitleView.setOnLeftTextClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                PascWebviewActivity.this.finish();
              }
            });
          } else {
            mCommonTitleView.setLeftText("");
          }
        }
      } else {
        mCommonTitleView.setLeftText("");
      }
    }
    final List<ToolbarBeanNew.BtnOpts> rightBtns = toolbarTitleBean.getRightBtns();
    if (null != rightBtns) {
      if (rightBtns.size() == 1) {
        final ToolbarBeanNew.BtnOpts opts = rightBtns.get(0);
        mCommonTitleView.setRightImageVisible(View.GONE);
        mCommonTitleView.setRightTextVisibility(View.GONE);
        if (!TextUtils.isEmpty(opts.getIconUrl())) {
          mCommonTitleView.setRightIvResource(opts.getIconUrl());
          mCommonTitleView.setOnRightImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              mWebviewFragment.callHandler(opts.getAction(), "", new CallBackFunction() {
                @Override
                public void onCallBack(String data) {

                }
              });
            }
          });
        } else if (!TextUtils.isEmpty(opts.getTitle())) {
          mCommonTitleView.setRightText(opts.getTitle())
              .setOnRightClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  mWebviewFragment.callHandler(opts.getAction(), "", new CallBackFunction() {
                    @Override
                    public void onCallBack(String data) {

                    }
                  });
                }
              });
        } else {
          if (null != PascHybrid.getInstance().mHybridInitConfig
              && null != PascHybrid.getInstance().mHybridInitConfig.getHybridInitCallback()) {
            String color = PascHybrid.getInstance().mHybridInitConfig.getHybridInitCallback()
                .themeColorString();
            try {
              if (null != color) {
                mCommonTitleView.getRightIv().setColorFilter(Color.parseColor(color));
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
          switch (opts.getIconType()) {
            case 0:
              if (toolbarTitleBean.getTintColor() != null && "#FFFFFF".equals(
                  toolbarTitleBean.getTintColor()) && "#ffffff".equals(
                  toolbarTitleBean.getTintColor())) {
                mCommonTitleView.setRightDrawableRight(R.drawable.paschybrid_ic_share_white);
              } else {
                mCommonTitleView.setRightDrawableRight(R.drawable.paschybrid_ic_share_blue);
              }
              break;
            case 1:
              mCommonTitleView.setRightDrawableRight(R.drawable.paschybrid_ic_search_black);
              break;
            case 2:
              mCommonTitleView.setRightDrawableRight(R.drawable.paschybrid_ic_no_collect);
              break;
            case 3:
              String rightColor = PascHybrid.getInstance().mHybridInitConfig.getHybridInitCallback()
                  .rightIconColorString();
              if (!TextUtils.isEmpty(rightColor)) {
                mCommonTitleView.getRightIv()
                    .setColorFilter(Color.parseColor(
                        PascHybrid.getInstance().mHybridInitConfig.getHybridInitCallback()
                            .rightIconColorString()));
              }
              mCommonTitleView.setRightDrawableRight(R.drawable.paschybrid_ic_collected);
              break;
            case 4:
              mCommonTitleView.setRightDrawableRight(R.drawable.paschybrid_ic_call_phone);
              break;
            case 5:
              mCommonTitleView.setRightDrawableRight(R.drawable.paschybrid_ic_copy_link);
              break;
            case 6:
              mCommonTitleView.setRightDrawableRight(R.drawable.paschybrid_ic_browser);
              break;
            case 7:
              mCommonTitleView.setRightDrawableRight(R.drawable.paschybrid_ic_reload);
              break;
            default:
              mCommonTitleView.setRightDrawableRight(R.drawable.paschybrid_ic_more_black);
              break;
          }
          mCommonTitleView.setOnRightImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              mWebviewFragment.callHandler(opts.getAction(), "", new CallBackFunction() {
                @Override
                public void onCallBack(String data) {

                }
              });
            }
          });
        }
      } else if (rightBtns.size() > 1) {
        mCommonTitleView.setRightImageVisible(View.VISIBLE);
        mCommonTitleView.setRightTextVisibility(View.GONE);
        mCommonTitleView.setRightDrawableRight(R.drawable.paschybrid_ic_more_black);
        mCommonTitleView.setOnRightImageClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

            showCreatePopup(view, rightBtns);
          }
        });
      } else if (rightBtns.size() == 0) {
        mCommonTitleView.setRightImageVisible(View.GONE);
        mCommonTitleView.setRightTextVisibility(View.GONE);
      }
    }
  }

  private void checkContactPermission() {
    new RxPermissions(this)
        .request("android.permission.READ_CONTACTS")
        .subscribe(new Consumer<Boolean>() {
          @Override
          public void accept(Boolean aBoolean) throws Exception {
            if (aBoolean) {
              BrowserUtils.openContact(PascWebviewActivity.this);
            }
          }
        });
  }

  /**
   * 显示创建的popupwindow，右上角弹框
   */
  private void showCreatePopup(View view, List<ToolbarBeanNew.BtnOpts> rightBtns) {
    if (customPopup == null) {
      customPopup = new CustomPopup(PascWebviewActivity.this);
    }
    preparePopupItems(customPopup, rightBtns);
    if (!customPopup.isShowing()) {
      customPopup.showAsDropDown(view, 0, 10);
    } else {
      customPopup.dismiss();
      customPopup = null;
    }
  }

  /**
   * 设置标题栏更多按钮的预加载item项
   */
  private void preparePopupItems(CustomPopup customPopup, List<ToolbarBeanNew.BtnOpts> rightBtns) {
    customPopup.clearData();
    for (int i = 0; i < rightBtns.size(); i++) {
      final ToolbarBeanNew.BtnOpts opts = rightBtns.get(i);
      int iconResId = 0;
      switch (opts.getIconType()) {
        case 0:
          if (toolbarTitleBean.getTintColor() != null
              && "#FFFFFF".equals(toolbarTitleBean.getTintColor())
              && "#ffffff".equals(toolbarTitleBean.getTintColor())) {
            iconResId = R.drawable.paschybrid_ic_share_white;
          } else {
            iconResId = R.drawable.paschybrid_ic_share_blue;
          }
          break;
        case 1:

          iconResId = R.drawable.paschybrid_ic_search_black;
          break;
        case 2:
          iconResId = R.drawable.paschybrid_ic_no_collect;

          break;
        case 3:
          iconResId = R.drawable.paschybrid_ic_collected;
          break;
        case 4:
          iconResId = R.drawable.paschybrid_ic_call_phone;
          break;
        case 5:
          iconResId = R.drawable.paschybrid_ic_copy_link;
          break;
        case 6:
          iconResId = R.drawable.paschybrid_ic_browser;
          break;
        case 7:
          iconResId = R.drawable.paschybrid_ic_reload;
          break;
      }
      if (!TextUtils.isEmpty(opts.getIconUrl())) {
        customPopup.addItem(opts.getTitle(), opts.getIconUrl(),
            new CustomPopup.onSeparateItemClickListener() {
              @Override
              public void onClick() {
                if (opts.getIconType() == 5) {
                  ClipboardManager cm =
                      (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                  // 创建普通字符型ClipData
                  ClipData mClipData = ClipData.newPlainText("copy", url);
                  // 将ClipData内容放到系统剪贴板里。
                  if (cm != null) {
                    cm.setPrimaryClip(mClipData);
                    Toast.makeText(mContext, mContext.getText(R.string.hybrid_copy_success),
                        Toast.LENGTH_LONG).show();
                  }
                } else if (opts.getIconType() == 6) {
                  //浏览器打开
                  Uri uri = Uri.parse(url);
                  Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                  startActivity(intent);
                } else if (opts.getIconType() == 7) {
                  mWebviewFragment.loadUrl(url);
                }
                mWebviewFragment.callHandler(opts.getAction(), "", new CallBackFunction() {
                  @Override
                  public void onCallBack(String data) {

                  }
                });
              }
            });
      } else {
        customPopup.addItem(opts.getTitle(), iconResId,
            new CustomPopup.onSeparateItemClickListener() {
              @Override
              public void onClick() {
                if (opts.getIconType() == 5) {
                  ClipboardManager cm =
                      (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                  // 创建普通字符型ClipData
                  ClipData mClipData = ClipData.newPlainText("copy", url);
                  // 将ClipData内容放到系统剪贴板里。
                  if (cm != null) {
                    cm.setPrimaryClip(mClipData);
                  }
                } else if (opts.getIconType() == 6) {
                  //浏览器打开
                  Uri uri = Uri.parse(url);
                  Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                  startActivity(intent);
                } else if (opts.getIconType() == 7) {
                  mWebviewFragment.loadUrl(url);
                }
                mWebviewFragment.callHandler(opts.getAction(), "", new CallBackFunction() {
                  @Override
                  public void onCallBack(String data) {

                  }
                });
              }
            });
      }
    }
  }

  @Override
  public void onNetworkStatus(int networkType, boolean isConnected) {
    if (TextUtils.isEmpty(networkAction)) {
      return;
    }
    try {
      JsonObject jsonObject = (JsonObject) new JsonParser().parse(networkAction);
      Gson gson = new Gson();
      NetworkStatusBean networkStatus = new NetworkStatusBean(networkType);
      String jsonData = gson.toJson(networkStatus);
      mWebviewFragment.callHandler(jsonObject.get("action").getAsString(), jsonData,
          new CallBackFunction() {
            @Override
            public void onCallBack(String data) {

            }
          });
    } catch (Exception e) {

    }
  }

  public static class WebViewHandler extends Handler {
    private WeakReference<Activity> weakReference;

    public WebViewHandler(Activity activity) {
      super(Looper.getMainLooper());
      this.weakReference = new WeakReference<>(activity);
    }

    public Activity get() {
      return weakReference.get();
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    // TODO Auto-generated method stub
    try {
      super.onConfigurationChanged(newConfig);
      if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

      } else if (getResources().getConfiguration().orientation
          == Configuration.ORIENTATION_PORTRAIT) {

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
