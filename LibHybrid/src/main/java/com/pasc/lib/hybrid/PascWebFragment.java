package com.pasc.lib.hybrid;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.hybrid.behavior.DefaultBehaviorManager;
import com.pasc.lib.hybrid.behavior.WebPageConfig;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.hybrid.callback.NetworkStatusCallback;
import com.pasc.lib.hybrid.callback.WebChromeClientCallback;
import com.pasc.lib.hybrid.callback.WebViewClientListener;
import com.pasc.lib.hybrid.callback.WebViewJavaScriptFunction;
import com.pasc.lib.hybrid.listener.KeyboardListener;
import com.pasc.lib.hybrid.nativeability.WebStrategy;
import com.pasc.lib.hybrid.nativeability.WebStrategyType;
import com.pasc.lib.hybrid.util.Constants;
import com.pasc.lib.hybrid.util.NetWorkStateReceiver;
import com.pasc.lib.hybrid.util.NetWorkUtils;
import com.pasc.lib.hybrid.util.SizeUtils;
import com.pasc.lib.hybrid.util.Utils;
import com.pasc.lib.hybrid.webview.PascWebView;
import com.pasc.lib.hybrid.webview.WebViewContants;
import com.pasc.lib.hybrid.widget.CommonDialog;
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
import com.pasc.lib.smtbrowser.view.PhotoViewPager;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.smtt.export.external.interfaces.ClientCertRequest;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebBackForwardList;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import io.reactivex.functions.Consumer;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;

public class PascWebFragment  extends BaseFragment
        implements WebChromeClientCallback, WebViewClientListener {
    public static final int REQUEST_CODE_FILE_SELECT = 0x1000;
    public static final int REQUEST_CODE_LOGIN = 0x1001;
    public static final int REQUEST_CODE_ADDADDRESS = 0x1002;
    public static final int REQUEST_CODE_CONTACT = 0x2000;
    public static final int TITLE_PLACE_HODLER = 1;
    public static final int TITLE_FRIST_PAGE = 2;
    public static final int TITLE_HOLE_WEBVIEW = 3;
    private final static String EXTRA_URL_FLG = "extra_url";
    private final static String EXTRA_STRATEGY_FLG = "extra_strategy";

    public WebCommonTitleView mCommonTitleView;
    private CustomPopup customPopup;
    public ToolbarBeanNew toolbarTitle;
    private boolean isToolBarHide;
    private WebViewHandler mHandler;
    private String url;
    //策略
    public WebStrategy webStrategy;
    //public int webStrategyKey;

    private NetWorkStateReceiver netWorkStateReceiver;
    private String networkAction;
    public int titleLevel;
    RelativeLayout webViewContainer;
    boolean isResize; //是否为了沉浸式变化计算高度
    public boolean stopJs = true; //web页面开启新activity跳转web页面时，防止前一个页面停止了js影响后一个页面

    private final String TAG = "PASC_HYBRID";
    public final static int FILECHOOSER_RESULTCODE = 0x1000;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 0x1001;
    public PascWebView mWebView = null;
    private String mUrl = "";
    ValueCallback<Uri> mUploadMessage;
    ValueCallback<Uri[]> mUploadMessageForAndroid5;
    ValueAnimator mValueAnimator = null;
    public ProgressBar mProgressbar;
    private int mCurrentProgress;
    public PhotoViewPager photoViewPager;
    boolean isErrorPage;
    private View mErrorView;
    RelativeLayout webParentView;
    //titleview的状态
    int visibility;
    int mProgress;
    private boolean isFirstOnRes;

    public KeyboardListener keyboardListener; // 键盘监听
    public HashMap<String, KeyboardListener.OnKeyboardListener> keyboardListenerMap =
            new HashMap<>();

    public void addKeyboardListener(String key,
                                    KeyboardListener.OnKeyboardListener keyboardListener) {
        keyboardListenerMap.put(key, keyboardListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup mRootView = (ViewGroup) inflater.inflate(R.layout.hybrid_fragment_pascweb
                , container, false);

        if (null == PascHybrid.getInstance().mHybridInitConfig) {
            throw new IllegalArgumentException("Please call PascHybrid.getInstance().init() .");
        }
        WebPageConfig config = DefaultBehaviorManager.getInstance().getWebPageConfig();
        Map<String, BehaviorHandler> tempBehaviors = new HashMap<>(16);
        tempBehaviors.putAll(PascHybrid.getInstance().mHybridInitConfig.customerBehaviors);
        if (config != null) {
            tempBehaviors.putAll(config.getCustomerBehaviors());
        } else {
            config = new WebPageConfig.Builder().create();
            DefaultBehaviorManager.getInstance().setWebPageConfig(config);
        }
        DefaultBehaviorManager.getInstance().setCustomerBehaviors(tempBehaviors);

        if (savedInstanceState != null) {
            try {
                WebPageConfig webPageConfig =
                        (WebPageConfig) savedInstanceState.getSerializable("webConfig");
                if (webPageConfig != null) {
                    DefaultBehaviorManager.getInstance().setWebPageConfig(webPageConfig);
                }
                url = savedInstanceState.getString(EXTRA_URL_FLG);

                if (!TextUtils.isEmpty(url)) {
                    getArguments().putString(EXTRA_URL_FLG, url + "");
                }
                //webStrategyKey = savedInstanceState.getSerializable(EXTRA_STRATEGY_FLG);
                getArguments().putSerializable(EXTRA_STRATEGY_FLG, savedInstanceState.getSerializable(EXTRA_STRATEGY_FLG));
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        mWebView = mRootView.findViewById(R.id.pasc_webview);
        mProgressbar = mRootView.findViewById(R.id.mprogressBar);
        mProgressbar.setVisibility(View.VISIBLE);
        mCommonTitleView = mRootView.findViewById(R.id.common_title);
        webViewContainer = mRootView.findViewById(R.id.common_webview_layout);
        initToolbar();
        initWebView();
        initWebStrategy();
        initHandler();

        mActivity.getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mWebView.getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.addJavascriptInterface(new WebViewJavaScriptFunction() {

            @Override
            public void onJsFunctionCalled(String tag) {
                // TODO Auto-generated method stub
            }

            @JavascriptInterface
            public void onX5ButtonClicked() {
                enableX5FullscreenFunc();
            }

            @JavascriptInterface
            public void onCustomButtonClicked() {
                disableX5FullscreenFunc();
            }

            @JavascriptInterface
            public void onLiteWndButtonClicked() {
                enableLiteWndFunc();
            }

            @JavascriptInterface
            public void onPageVideoClicked() {
                enablePageVideoFunc();
            }
        }, "Android");

        mWebView.setOnWebScorollListener(new PascWebView.OnWebScorollListener() {

            @Override
            public void onScrollChanged(int t, int oldt) {
                if (null != mActivity
                        && null != toolbarTitle
                        && toolbarTitle.isProgressiveOpacity()) {
                    int height = mActivity.getWindowManager().getDefaultDisplay().getHeight() / 3;
                    float f = (float) t / height;
                    if (f > 1.0f) {
                        f = 1.0f;
                    }
                    mCommonTitleView.setAlpha(f);
                }
            }
        });

        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final WebView.HitTestResult hitTestResult = mWebView.getHitTestResult();
                // 如果是图片类型或者是带有图片链接的类型
                if (hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                        hitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                    // 弹出保存图片的对话框
                    new AlertDialog.Builder(mActivity)
                            .setItems(new String[] { "保存图片" }, (dialog, which) -> {
                                String pic = hitTestResult.getExtra();//获取图片
                                switch (which) {
                                    case 0:
                                        //保存图片到相册
                                        new RxPermissions(mActivity).request(Manifest.permission.WRITE_EXTERNAL_STORAGE
                                                , Manifest.permission.READ_EXTERNAL_STORAGE)
                                                .subscribe(new Consumer<Boolean>() {
                                                    @Override
                                                    public void accept(Boolean aBoolean) throws Exception {
                                                        Utils.saveImage(mActivity, pic);
                                                    }
                                                });
                                        break;
                                }
                            })
                            .show();
                    return true;
                }
                return false;//保持长按可以复制文字
            }
        });

        photoViewPager = new PhotoViewPager(getContext());
        photoViewPager.setVisibility(View.GONE);
        ((ViewGroup) mWebView.getParent()).addView(photoViewPager,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        DefaultBehaviorManager.getInstance().sureRegisterDefaultHandler();
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        keyboardListener = new KeyboardListener(mActivity);
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
    }

    private void initWebView() {
        Bundle bundle = this.getArguments();
        if(bundle != null){
            //webStrategyKey = bundle.getInt(EXTRA_STRATEGY_FLG,0);
            //PascHybrid.getInstance().webStrategyMap.get(webStrategyKey);
            if(bundle.getSerializable(EXTRA_STRATEGY_FLG) instanceof WebStrategy) {
                webStrategy = (WebStrategy) bundle.getSerializable(EXTRA_STRATEGY_FLG);
                url = webStrategy.url;
            }else{
                url = bundle.getString(EXTRA_URL_FLG);
            }
//            if (webStrategy == null) {
//                url = bundle.getString(EXTRA_URL_FLG);
//            } else {
//                url = webStrategy.url;
//            }

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
                Toast.makeText(getActivity(), "url为null", Toast.LENGTH_SHORT).show();
                url = "about:blank";
            }
        }

        String urlTmp = url;
        try {
            urlTmp = Utils.getDeleteParamUri(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        loadUrl(urlTmp);


        try {
            if (!TextUtils.isEmpty(Utils.getUiparam(url))) {
                String uiJson = URLDecoder.decode(Utils.getUiparam(url), "UTF-8");
                Gson gson = new Gson();
                toolbarTitle = gson.fromJson(uiJson, ToolbarBeanNew.class);
                JSONObject jObj = new JSONObject(uiJson);
                if (jObj.has("isHide")) {
                    isToolBarHide = toolbarTitle.isHide();
                }
                if (jObj.has("isWebImmersive")) {
                    isResize = true;
                }
                updateToolbar();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (null == mWebView) {
            throw new RuntimeException("WebView can not be null!");
        }

        if (null != mWebView.getWebChromeClient()) {
            mWebView.getWebChromeClient().setWebChromeClientCallback(this);
        }
        mWebView.setWebViewClientListener(this);
        if (PascHybrid.getInstance().getHybridInitConfig().getHybridInitCallback() != null) {
            PascHybrid.getInstance()
                    .getHybridInitConfig()
                    .getHybridInitCallback()
                    .onWebViewCreate(mWebView);
        }
        if (Constants.TEMP_URL!=null&&Constants.TEMP_URL.equals(mUrl)
                && (System.currentTimeMillis() - Constants.TEMP_CLEAR_CACHE_TIME) < 5000) {
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            Constants.TEMP_URL = "";
            Constants.TEMP_CLEAR_CACHE_TIME = 0;
        } else {
            Constants.TEMP_URL = mUrl;
            Constants.TEMP_CLEAR_CACHE_TIME = System.currentTimeMillis();
        }
        initWebConfig();
    }

    public void initToolbar() {
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
                getActivity().finish();
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
    }

    public void initWebStrategy() {
        if (webStrategy == null) {
            return;
        }
        if(webStrategy.mainPageModule == WebStrategyType.NORMALPAGE){
            //设置状态栏文字颜色为深色
            BrowserUtils.setStatusBarTxColor(mActivity, true);
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
                    new CommonDialog(mActivity).setContent("(0513) 12345")
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
            //webStrategy控制toolbar以后把toolbar状态默认值也该一下
            isToolBarHide = true;
            visibility = View.GONE;
            mCommonTitleView.setVisibility(View.GONE);
            FrameLayout.LayoutParams params =
                    new FrameLayout.LayoutParams(webViewContainer.getLayoutParams());
            params.topMargin = 0;
            webViewContainer.setLayoutParams(params);
        } else if (webStrategy.toolBarVisibility == WebStrategyType.TOOLBAR_VISIBLE) {
            //webStrategy控制toolbar以后把toolbar状态默认值也该一下
            isToolBarHide = false;
            visibility = View.VISIBLE;
            mCommonTitleView.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params =
                    new FrameLayout.LayoutParams(webViewContainer.getLayoutParams());
            params.topMargin = Utils.dp2px(44);
            webViewContainer.setLayoutParams(params);
        }


        if (PascHybrid.getInstance().getHybridInitConfig().getOldLogicCallback() != null
                && webStrategy.oldJsInterface == WebStrategyType.OLD_INTERFACE_ON) {
            //兼容旧框架注册interface
            PascHybrid.getInstance()
                    .getHybridInitConfig()
                    .getOldLogicCallback()
                    .oldInterfaceCallback(mCommonTitleView, mWebView);
        }
        //控制toolbarcolor时隐藏progressbar
        if (webStrategy.toolBarColor != null) {
            mProgressbar.setVisibility(View.GONE);
        }

        if(webStrategy.isHideProgressBar == WebStrategyType.PROGRESS_HIDE){
            mProgressbar.setVisibility(View.GONE);
        }
    }

    private void initHandler() {
        if (null == mHandler) {
            mHandler = new WebViewHandler(mActivity) {
                @Override
                public void handleMessage(Message msg) {
                    Gson gson = new Gson();
                    String data = null;
                    switch (msg.what) {
                        case DefaultBehaviorManager.ACTION_BEHAVIOR_CONTROLL_TOOLBAR:
                            data = (String) msg.obj;

                            try {
                                boolean isWebImmersive = toolbarTitle.isWebImmersive();
                                toolbarTitle = gson.fromJson(data, ToolbarBeanNew.class);
                                JSONObject jObj = new JSONObject(data);
                                if (jObj.has("isHide")) {
                                    isToolBarHide = toolbarTitle.isHide();
                                }
                                if (jObj.has("isWebImmersive")) {
                                    isResize = true;
                                }else{
                                    toolbarTitle.setWebImmersive(isWebImmersive);
                                }
                                updateToolbar();
                            } catch (RuntimeException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
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
                            mActivity.startActivity(Intent.createChooser(intent, "分享到"));
                            break;
                        case DefaultBehaviorManager.ACTION_BEHAVIOR_OPEN_NEW_WEBVIEW:
                            data = (String) msg.obj;
                            OpenNewWebBean openNewWeb = gson.fromJson(data, OpenNewWebBean.class);
                            //stopJs = false;
                            PascHybrid.getInstance()
                                    .with(DefaultBehaviorManager.getInstance().getWebPageConfig())
                                    .start(mActivity, new WebStrategy().setUrl(openNewWeb.url)
                                            .setToolBarVisibility(openNewWeb.hideNavBar ? WebStrategyType.TOOLBAR_GONE
                                                    : WebStrategyType.TOOLBAR_VISIBLE));

                            if (openNewWeb.closeCurWeb) {
                                getActivity().finish();
                            }
                            break;
                        case DefaultBehaviorManager.ACTION_BEHAVIOR_CLOSE_WEBVIEW:
                            getActivity().finish();
                            break;
                        case DefaultBehaviorManager.ACTION_BEHAVIOR_CLOSE_BACK_HOME:
                            getActivity().finish();
                            break;
                        case DefaultBehaviorManager.ACTION_BEHAVIOR_SELECT_CONTACT:
                            Utils.checkContactPermission(mActivity);
                            break;
                        case DefaultBehaviorManager.ACTION_BEHAVIOR_GO_BACK:
                            onBackPressed();
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
                            BrowserUtils.sendSMS(mActivity, phoneNums.toString(), smsBean.message);
                            break;
                        case DefaultBehaviorManager.ACTION_BEHAVIOR_NETWORK_STATUS:
                            networkAction = (String) msg.obj;
                            break;
                        case DefaultBehaviorManager.ACTION_BEHAVIOR_WEBVIEW_UI:
                            data = (String) msg.obj;
                            WebViewUIBean webViewUIBean = gson.fromJson(data, WebViewUIBean.class);
                            if (webViewUIBean.webViewBackgroundColor != null) {
                                mWebView.setBackgroundColor(
                                        Color.parseColor(webViewUIBean.webViewBackgroundColor));
                            }
                            WebViewContants.banAlart = webViewUIBean.banAlert;
                            WebViewContants.banScroll = webViewUIBean.banScroll;
                            if (webViewUIBean.progressColor != null) {
                                Utils.setColors(mProgressbar, 0xffffffff,
                                        Color.parseColor(webViewUIBean.progressColor));
                            }
                            int scrolly = (int) (mWebView.getContentHeight()
                                    * mWebView.getScale()
                                    * webViewUIBean.verticalOffset);
                            mWebView.scrollTo(0, scrolly);
                            break;
                    }
                }
            };
        }
        DefaultBehaviorManager.getInstance().setUIHandler(mHandler);
    }

    public void initWebConfig() {
        WebPageConfig config = DefaultBehaviorManager.getInstance().getWebPageConfig();
        if (null != config) {
            if (config.getJsInterfaces().size() > 0) {
                for (Map.Entry<String, Object> entry : config.getJsInterfaces().entrySet()) {
                    mWebView.addJavascriptInterface(entry.getValue(), entry.getKey());
                }
            }

            for (Map.Entry<String, BehaviorHandler> entry : config.getCustomerBehaviors().entrySet()) {
                mWebView.registerBehavior(entry.getKey(), entry.getValue());
            }

            mWebView.setWebSettingCallback(config.getWebSettingCallback());
        }

        Map<String, BehaviorHandler> customerBehaviors =
                DefaultBehaviorManager.getInstance().getCustomerBehaviors();
        if (null != customerBehaviors) {
            for (Map.Entry<String, BehaviorHandler> entry : customerBehaviors.entrySet()) {
                mWebView.registerBehavior(entry.getKey(), entry.getValue());
            }
        }

        if (mActivity != null) {
            mWebView.setDownloadListener(new PascWebFragment.MyWebViewDownLoadListener());
            if (!TextUtils.isEmpty(mUrl)) {
                loadUrlIntoWebView(mUrl);
            }
        }

        mWebView.callHandler(ConstantBehaviorName.CALL_ENTER_WEBVIEW, "", null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_URL_FLG, url);
        WebPageConfig webPageConfig = DefaultBehaviorManager.getInstance().getWebPageConfig();
        try {
            outState.putSerializable("webConfig", webPageConfig);
            outState.putSerializable(EXTRA_STRATEGY_FLG, getArguments().getSerializable(EXTRA_STRATEGY_FLG));
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void loadUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (null == mWebView) {
            mUrl = url;
        } else {
            // 加载url
            loadUrlIntoWebView(url);
            mUrl = null;
        }
    }

    private void loadUrlIntoWebView(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (mWebView == null) {
            return;
        }
        boolean logEnable = PascHybrid.getInstance().getHybridInitConfig().isLogEnable();
        if (logEnable) {
            Log.d(TAG, "WebView start to load url. url=" + url);
        }
        mWebView.loadUrl(url);
    }

    public void callHandler(String handlerName, String data, CallBackFunction callBack) {
        if (mWebView != null) {
            mWebView.callHandler(handlerName, data, callBack);
        }
    }

    /**
     * webchrome回调
     */
    @Override
    public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
        mUploadMessage = valueCallback;
        if ("video/*".equals(acceptType) && "camcorder".equals(capture)) {
            recordVideo(FILECHOOSER_RESULTCODE);
        } else {
            new RxPermissions(getActivity()).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                                i.addCategory(Intent.CATEGORY_OPENABLE);
                                i.setType("image/*");
                                startActivityForResult(Intent.createChooser(i, "File Chooser"),
                                        FILECHOOSER_RESULTCODE);
                            }
                        }
                    });
        }
    }

    @Override
    public void showFileChooser(ValueCallback<Uri[]> valueCallback,
                                WebChromeClient.FileChooserParams fileChooserParams) {
        mUploadMessageForAndroid5 = valueCallback;
        if ("video/*".equals(fileChooserParams.getAcceptTypes()[0])
                && fileChooserParams.isCaptureEnabled()) {
            recordVideo(FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
        } else {
            new RxPermissions(getActivity()).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                                contentSelectionIntent.setType("image/*");
                                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
                            }
                        }
                    });
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        //判断当前历史列表是否最顶端
        if (null == mActivity) {
            return;
        }
        if (mWebView.copyBackForwardList().getCurrentIndex() > 0) {
            //pascWebviewActivity.mCommonTitleView.setLeftTextVisibility(View.VISIBLE);
            if (webStrategy != null
                    && webStrategy.oldToolBarRecycle
                    == WebStrategyType.OLD_TOOLBAR_RECYCLE_ON) {
                if (PascHybrid.getInstance().toolBarCallback != null
                        && mCommonTitleView != null && mActivity != null) {
                    PascHybrid.getInstance().toolBarCallback.toolBarRecycleCallback(mCommonTitleView
                            , mActivity, false);
                }
            }
        } else {
            //pascWebviewActivity.mCommonTitleView.setLeftTextVisibility(View.GONE);
            if (webStrategy != null
                    && webStrategy.oldToolBarRecycle == WebStrategyType.OLD_TOOLBAR_RECYCLE_ON) {
                if (PascHybrid.getInstance().toolBarCallback != null
                        && mCommonTitleView != null && mActivity != null) {
                    PascHybrid.getInstance().toolBarCallback.toolBarRecycleCallback(mCommonTitleView, mActivity, true);
                }
            }
            if (titleLevel == PascWebviewActivity.TITLE_FRIST_PAGE) {
                mCommonTitleView.setTitleText(title);
            }
        }
        String titleText = title;
        //toolbartitle由web主动控制或者通过url下发优先级最高，webstrategy为本地控制优先级中，网页获取title优先级最低
        if (toolbarTitle != null && TextUtils.isEmpty(toolbarTitle.getWebViewTitle())) {
            if (webStrategy == null || webStrategy.title == null) {
                titleText = title;
                mCommonTitleView.setTitleText(title);
            } else {
                titleText = webStrategy.title;
                mCommonTitleView.setTitleText(webStrategy.title);
            }
        } else if (toolbarTitle == null
                && webStrategy != null
                && webStrategy.title != null) {
            titleText = webStrategy.title;
            mCommonTitleView.setTitleText(webStrategy.title);
        } else {
            titleText = title;
            mCommonTitleView.setTitleText(title);
        }
        //上报页面埋点
        if (PascHybrid.getInstance().statisticsCallback != null) {
            PascHybrid.getInstance().statisticsCallback.uploadWebviewPageData(titleText);
        }
    }

    /**
     * web 控制 toolbar
     */
    public void updateToolbar() {
        if (isToolBarHide) {
            mCommonTitleView.setVisibility(View.GONE);
            visibility = View.GONE;
            FrameLayout.LayoutParams params =
                    new FrameLayout.LayoutParams(webViewContainer.getLayoutParams());
            params.topMargin = 0;
            webViewContainer.setLayoutParams(params);
        } else {
            mCommonTitleView.setVisibility(View.VISIBLE);
            visibility = View.VISIBLE;
            FrameLayout.LayoutParams params =
                    new FrameLayout.LayoutParams(webViewContainer.getLayoutParams());
            params.topMargin = Utils.dp2px(44);
            webViewContainer.setLayoutParams(params);
        }
        if(webStrategy == null || webStrategy.mainPageModule == WebStrategyType.NORMALPAGE){
            if (toolbarTitle.getStatusBarStyle() == 0) { //0黑 1白
                BrowserUtils.setStatusBarTxColor(mActivity, true);
            } else if (toolbarTitle.getStatusBarStyle() == 1) {
                BrowserUtils.setStatusBarTxColor(mActivity, false);
            }
        //获取沉浸状态栏
        if (toolbarTitle.isWebImmersive()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = mActivity.getWindow();
                window.getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window window = mActivity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                mActivity.getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
            if (isResize) {
                isResize = false;
                //只要执行了沉浸式，页面高度会有变化，重新计算页面高度
                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                                , SizeUtils.getScreenSizeOfDevice(mActivity) - SizeUtils
                                .getNavigationBarHeightIfRoom(mActivity));
                mActivity.findViewById(android.R.id.content).setLayoutParams(layoutParams);
            }
        } else {
            BrowserUtils.setStatusBarTxColor(mActivity, true);
            if (isResize) {
                isResize = false;
                //只要执行了沉浸式，页面高度会有变化，重新计算页面高度
                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                                , SizeUtils.getScreenSizeOfDevice(mActivity) - SizeUtils.getStatusBarHeight(mActivity)
                                - SizeUtils.getNavigationBarHeightIfRoom(mActivity));
                mActivity.findViewById(android.R.id.content).setLayoutParams(layoutParams);
            }
        }
        }

        if (toolbarTitle.isHideBottomLine()) {
            mCommonTitleView.setUnderLineVisible(false);
        } else {
            mCommonTitleView.setUnderLineVisible(true);
        }
        if (!TextUtils.isEmpty(toolbarTitle.getBackgroundColor())) {
            mCommonTitleView.setBackgroundColor(Color.parseColor(toolbarTitle.getBackgroundColor()));
        }
        if (toolbarTitle.getPlaceholderTitle() != null) {
            titleLevel = TITLE_PLACE_HODLER;
            mCommonTitleView.setTitleText(toolbarTitle.getPlaceholderTitle());
        }
        if (toolbarTitle.getTitle() != null) {
            titleLevel = TITLE_FRIST_PAGE;
            //防止两种title设置方式互相干扰
            webStrategy.title = toolbarTitle.getTitle();
            mCommonTitleView.setTitleText(toolbarTitle.getTitle());
        }
        if (toolbarTitle.getWebViewTitle() != null) {
            titleLevel = TITLE_HOLE_WEBVIEW;
            //防止两种title设置方式互相干扰
            webStrategy.title = toolbarTitle.getWebViewTitle();
            mCommonTitleView.setTitleText(toolbarTitle.getWebViewTitle());
        }
        if (!TextUtils.isEmpty(toolbarTitle.getTitleTextColor())) {
            mCommonTitleView.setTitleTextColor(Color.parseColor(toolbarTitle.getTitleTextColor()));
        }
        if (toolbarTitle.getTitleTextSize() > 0) {
            mCommonTitleView.setTitleTextSize(toolbarTitle.getTitleTextSize());
        }
        if (!TextUtils.isEmpty(toolbarTitle.getStatusBarBackgroundColor())) {
            Utils.setStatusBarBgColor(mActivity,Color.parseColor(toolbarTitle.getStatusBarBackgroundColor()));
        }
        if (!TextUtils.isEmpty(toolbarTitle.getSubtitle())) {
            mCommonTitleView.setSubTitleText(toolbarTitle.getSubtitle());
            if (toolbarTitle.getSubtitleTextSize() > 0) {
                mCommonTitleView.setSubTitleSize(toolbarTitle.getSubtitleTextSize());
            }
            if (!TextUtils.isEmpty(toolbarTitle.getSubtitleTextColor())) {
                mCommonTitleView.setSubTitleColor(Color.parseColor(toolbarTitle.getSubtitleTextColor()));
            }
        }

        //获取渐变色
        if (toolbarTitle.getGradientBackgroundColors() != null
                && toolbarTitle.getGradientBackgroundColors().size() == 2) {
            mCommonTitleView.setTopViewHeight(Utils.getStatusBarHeight(mActivity));
            mCommonTitleView.setToolBarColor(toolbarTitle.getGradientBackgroundColors(),
                    toolbarTitle.getGradientDirection());
            //状态栏和toolbar整体渐变需要沉浸式效果
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = mActivity.getWindow();
                //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window window = mActivity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                mActivity.getWindow().getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        } else {
            mCommonTitleView.setTopViewHeight(0);
        }
        List<ToolbarBeanNew.BtnOpts> leftBtns = toolbarTitle.getLeftBtns();
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
                                mActivity.finish();
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
        final List<ToolbarBeanNew.BtnOpts> rightBtns = toolbarTitle.getRightBtns();
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
                            callHandler(opts.getAction(), "", new CallBackFunction() {
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
                                    callHandler(opts.getAction(), "", new CallBackFunction() {
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
                            if (toolbarTitle.getTintColor() != null && "#FFFFFF".equals(
                                    toolbarTitle.getTintColor()) && "#ffffff".equals(
                                    toolbarTitle.getTintColor())) {
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
                            callHandler(opts.getAction(), "", new CallBackFunction() {
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

    /**
     * 显示创建的popupwindow，右上角弹框
     */
    private void showCreatePopup(View view, List<ToolbarBeanNew.BtnOpts> rightBtns) {
        if (customPopup == null) {
            customPopup = new CustomPopup(mActivity);
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
                    if (toolbarTitle.getTintColor() != null
                            && "#FFFFFF".equals(toolbarTitle.getTintColor())
                            && "#ffffff".equals(toolbarTitle.getTintColor())) {
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
                                            (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                                    // 创建普通字符型ClipData
                                    ClipData mClipData = ClipData.newPlainText("copy", url);
                                    // 将ClipData内容放到系统剪贴板里。
                                    if (cm != null) {
                                        cm.setPrimaryClip(mClipData);
                                        Toast.makeText(mActivity, mActivity.getText(R.string.hybrid_copy_success),
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else if (opts.getIconType() == 6) {
                                    //浏览器打开
                                    Uri uri = Uri.parse(url);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                } else if (opts.getIconType() == 7) {
                                    loadUrl(url);
                                }
                                callHandler(opts.getAction(), "", new CallBackFunction() {
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
                                            (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
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
                                    loadUrl(url);
                                }
                                callHandler(opts.getAction(), "", new CallBackFunction() {
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
    public void onProgressChanged(int newProgress) {
        if (newProgress >= 100) {
            mProgressbar.setProgress(100);
            mProgressbar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgressbar.setProgress(0);
                    mProgressbar.setVisibility(View.GONE);
                }
            }, 500);
        } else if (newProgress - mCurrentProgress > 0) {
            mCurrentProgress = newProgress;
            startProgressAnim();
        }
        if (PascHybrid.getInstance().getHybridInitConfig().getHybridInitCallback() != null) {
            PascHybrid.getInstance()
                    .getHybridInitConfig()
                    .getHybridInitCallback()
                    .onWebViewProgressChanged(mWebView, newProgress);
        }

        //页面快加载完成的时候控制title显示
        if (mProgress < 80) {
            mProgress = newProgress;
        } else {
            mProgress = -1;
            if (null == mActivity || webStrategy == null || mWebView == null) {
                return;
            }
            if (webStrategy.title == null) {
                mCommonTitleView.setTitleText(mWebView.getTitle());
            } else {
                mCommonTitleView.setTitleText(webStrategy.title);
            }
            if (PascHybrid.getInstance().mHybridInitConfig.getHybridInitCallback().titleCloseButton()
                    == WebStrategyType.CLOSEBUTTON_FRISTPAGE_GONE
                    && mWebView != null
                    && mWebView.canGoBack()) {
                mCommonTitleView.setLeftTextVisibility(View.VISIBLE);
            } else if (PascHybrid.getInstance().mHybridInitConfig.getHybridInitCallback()
                    .titleCloseButton()
                    == WebStrategyType.CLOSEBUTTON_ALWAKES_VISIBLE) {
                mCommonTitleView.setLeftTextVisibility(View.VISIBLE);
            } else {
                mCommonTitleView.setLeftTextVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.i(TAG, "onActivityResult intent:" + intent);
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
                String[] contacts = Utils.getPhoneContacts(mActivity, uri);
                SelectContactBean contactBean = new SelectContactBean(contacts[0], contacts[1]);
                PascHybrid.getInstance()
                        .triggerCallbackFunction(ConstantBehaviorName.OPEN_CONTACT, contactBean);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (PascHybrid.getInstance().activityResultCallback == null) {
            return;
        } else {
            PascHybrid.getInstance().activityResultCallback.activityResult(requestCode, resultCode,
                    intent);
        }

        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) {
                return;
            }
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == mUploadMessageForAndroid5) {
                return;
            }
            Uri result = (intent == null || resultCode != RESULT_OK) ? null : intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[] { result });
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[] {});
            }
            mUploadMessageForAndroid5 = null;
        }
    }
    private boolean isFristOnRes = true;

    @Override
    public void onResume() {
        super.onResume();
        if (isFristOnRes){
            isFristOnRes = false;
        //开启js
        if (mWebView != null) {
//            mWebView.resumeTimers();
            mWebView.onResume();
        }}
        DefaultBehaviorManager.getInstance().setUIHandler(mHandler);

        //fragment获取返回键事件
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    onBackPressed();
                    return true;
                }
                return false;
            }
        });

        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver(new NetworkStatusCallback() {
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
                        callHandler(jsonObject.get("action").getAsString(), jsonData,
                                new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {

                                    }
                                });
                    } catch (Exception e) {

                    }
                }
            });
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mActivity.registerReceiver(netWorkStateReceiver, filter);

        if (!Utils.isBackground(mActivity)) {
            mWebView.callHandler(ConstantBehaviorName.CALL_ENTER_APP, "", new CallBackFunction() {
                @Override
                public void onCallBack(String data) {

                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //关闭js
//        if (mWebView != null && stopJs) {
//            mWebView.onPause();
//            mWebView.pauseTimers();
//        }
        if (netWorkStateReceiver != null) {
            mActivity.unregisterReceiver(netWorkStateReceiver);
        }
        mWebView.callHandler(ConstantBehaviorName.CALL_CLOSE_WEBVIEW, "", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        //if (Utils.isBackground(mActivity)) {
            mWebView.callHandler(ConstantBehaviorName.CALL_EXIT_APP, "", new CallBackFunction() {
                @Override
                public void onCallBack(String data) {

                }
            });
        //}
    }

    private void startProgressAnim() {
        if (mValueAnimator == null) {
            mValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            mValueAnimator.setDuration(400);
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    int progress = (int) (value * mCurrentProgress);
                    if (progress > mProgressbar.getProgress()) {
                        mProgressbar.setProgress(progress);
                    }
                }
            });
        }
        if (mValueAnimator.isRunning()) {
            mValueAnimator.cancel();
        }
        mValueAnimator.start();
    }

    @Override
    public void onDestroy() {
        if (keyboardListener != null) {
            keyboardListener.removeGlobalLayoutListener();
        }

        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
        DefaultBehaviorManager.getInstance().destroyHandler(mHandler);
        PascHybrid.getInstance().removeCurrentParams(mActivity.hashCode());
        //解决org.chromium.android_webview.AwContents.isDestroyed问题
        try {
            if (mWebView.getParent() != null) {
                ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            }
            mWebView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.clearView();
            mWebView.removeAllViews();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.clearHistory();
            mWebView.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (PascHybrid.getInstance().webActivityDestroyCallback != null) {
            PascHybrid.getInstance().webActivityDestroyCallback.webActivityDestroy();
        }

        Constants.TEMP_CLEAR_CACHE_TIME = System.currentTimeMillis();

        super.onDestroy();

    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        mProgress = 0;
        if (PascHybrid.getInstance().getHybridInitConfig().getOldLogicCallback() != null) {
            PascHybrid.getInstance()
                    .getHybridInitConfig()
                    .getOldLogicCallback()
                    .onInterceptPageStarted(mWebView, url);
        }
        if (null == mActivity || null == webStrategy) {
            return;
        }
        if (webStrategy.statusBarVisibility
                == WebStrategyType.STATUSBAR_VISIBLE) {
            if (webStrategy.statusBarColor != null) {
                Utils.setStatusBarBgColor(mActivity,
                        Color.parseColor(webStrategy.statusBarColor));
            } else {
                Utils.setStatusBarBgColor(mActivity,Color.WHITE);
            }
        } else if (webStrategy.statusBarVisibility
                == WebStrategyType.STATUSBAR_FOLLOWING) {
            if (webStrategy.statusBarColor != null) {
                Utils.setStatusBarBgColor(mActivity,
                        Color.parseColor(webStrategy.statusBarColor));
            }
        }
    }

    @Override
    public void onPageFinished(WebView webView, String url) {
        if (!isErrorPage) {
            hideErrorPage();
        }
        if (PascHybrid.getInstance().getHybridInitConfig().getHybridInitCallback() != null) {
            PascHybrid.getInstance()
                    .getHybridInitConfig()
                    .getHybridInitCallback()
                    .onWebViewPageFinished(webView, url);
        }
    }

    @Override
    public void onLoadResource(String url) {

    }

    @Override
    public void onReceivedError(int errorCode, String description, String failingUrl) {
        boolean logEnable = PascHybrid.getInstance().getHybridInitConfig().isLogEnable();
        if (logEnable) {
            Log.d(TAG, "Load web failed. errorCode=" + errorCode + ",description=" + description + ",failingUrl=" + failingUrl);
        }

        if(webStrategy !=null && webStrategy.mainPageModule == WebStrategyType.MAINPAGE){
            mWebView.loadUrl("about:blank");
        }else{
            //显示错误页或无网页时恢复页面的状态栏和toolbar
            if (null != mActivity) {
                BrowserUtils.setStatusBarTxColor(mActivity,true);
                if (null != mCommonTitleView) {
                    mCommonTitleView.setVisibility(View.VISIBLE);
                }
            }
            if (NetWorkUtils.isNetworkConnected(mActivity)) {
                showErrorPage();
            } else {
                showNoNetPage();
            }
        }
        if (null != PascHybrid.getInstance()
                .getHybridInitConfig()
                .getWebErrorListener()) {
            PascHybrid.getInstance()
                    .getHybridInitConfig()
                    .getWebErrorListener()
                    .onWebError(errorCode, description, failingUrl);
        }
    }

    @Override
    public void onReceivedError(WebResourceRequest request, WebResourceError error) {

    }

    @Override
    public void onReceivedHttpError(WebResourceRequest request, WebResourceResponse errorResponse) {

    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

    }

    @Override
    public void onReceivedClientCertRequest(ClientCertRequest request) {

    }

    @Override
    public void onUnhandledKeyEvent(KeyEvent event) {

    }

    @Override
    public void onScaleChanged(float oldScale, float newScale) {

    }

    @Override
    public void onReceivedLoginRequest(String realm, String account, String args) {

    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (mActivity != null && webStrategy != null) {
            if (webStrategy.oldIntercept == WebStrategyType.OLD_INTERCEPT_ON
                    && PascHybrid.getInstance().getHybridInitConfig().getOldLogicCallback() != null) {
                if (PascHybrid.getInstance()
                        .getHybridInitConfig()
                        .getOldLogicCallback()
                        .oldInterceptCallback(view, url)) {
                    return true;
                }
            }
        }
        if (url.endsWith(".rar") || url.endsWith(".zip")) {
            Toast.makeText(mActivity, R.string.hybrid_file_open_error, Toast.LENGTH_LONG).show();
            return true;
        } else if (url.startsWith("tel:")) {
            final String phoneNum = url.replaceAll("tel:", "");
            new CommonDialog(mActivity).setContent(phoneNum)
                    .setButton1("取消")
                    .setButton2("确定", CommonDialog.Blue_4d73f4)
                    .setOnButtonClickListener(new CommonDialog.OnButtonClickListener() {
                        @Override
                        public void button1Click() {

                        }

                        @Override
                        public void button2Click() {
                            callPhone(phoneNum);
                        }
                    })
                    .show();
            return true;
        } else if (url.startsWith("mailto:") || url.startsWith("wtai:") || url.startsWith("getpoint:")
                || url.startsWith("sms:") || url.startsWith("javascript:;")) {
            return true;
        }
        if (url.startsWith("alipays:") || url.startsWith("alipay")) {
            try {
                mActivity.startActivity(
                        new Intent("android.intent.action.VIEW", Uri.parse(url)));
            } catch (Exception e) {
                Toast.makeText(mActivity, "未检测到支付宝客户端，请安装后重试。", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        if (url.startsWith("weixin:") || url.startsWith("weixin")) {
            try {
                mActivity.startActivity(
                        new Intent("android.intent.action.VIEW", Uri.parse(url)));
            } catch (Exception e) {
                Toast.makeText(mActivity, "未检测到微信客户端，请安装后重试。", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        //拦截点评的私有协议
        if(url.startsWith("dianping:")){
            return true;
        }
        if (url.startsWith("baidumap:") || url.startsWith("baidumap")) {//百度地图
            try {
                mActivity.startActivity(
                        new Intent("android.intent.action.VIEW", Uri.parse(url)));
                return true;
            } catch (Exception e) {
                Intent intent = new Intent ();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
                // 官方解释 : Name of the component implementing an activity that can display the intent
                if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
                    mActivity.startActivity(Intent.createChooser(intent, "请选择浏览器"));
                } else {
                    //GlobalMethod.showToast(context, "链接错误或无浏览器");
                }
                return true;
            }
        }
        if (url.startsWith("qqmap:")) {//腾讯地图
            try {
                mActivity.startActivity(
                        new Intent("android.intent.action.VIEW", Uri.parse(url)));
                return true;
            } catch (Exception e) {
                Intent intent = new Intent ();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
                // 官方解释 : Name of the component implementing an activity that can display the intent
                if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
                    mActivity.startActivity(Intent.createChooser(intent, "请选择浏览器"));
                } else {
                    //GlobalMethod.showToast(context, "链接错误或无浏览器");
                }
                return true;
            }
        }
        if (url.startsWith("amapuri:")) {//高德地图
            try {
                mActivity.startActivity(
                        new Intent("android.intent.action.VIEW", Uri.parse(url)));
                return true;
            } catch (Exception e) {
                Intent intent = new Intent ();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
                // 官方解释 : Name of the component implementing an activity that can display the intent
                if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
                    mActivity.startActivity(Intent.createChooser(intent, "请选择浏览器"));
                } else {
                    //GlobalMethod.showToast(context, "链接错误或无浏览器");
                }
                return true;
            }
        }
        if (url.startsWith("tmast:")) {//应用宝下载
            Intent intent = new Intent ();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
            // 官方解释 : Name of the component implementing an activity that can display the intent
            if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
                mActivity.startActivity(Intent.createChooser(intent, "请选择浏览器"));
            } else {
                //GlobalMethod.showToast(context, "链接错误或无浏览器");
            }
            return true;
        }
        //屏蔽引入未微信公众号文章时，公众号title跳转
        if(url.startsWith("https://mp.weixin.qq.com/mp/profile_ext")||url.startsWith("http://mp.weixin.qq.com/mp/profile_ext")){
            return true;
        }
        if (mActivity != null && webStrategy != null) {
            if (webStrategy.overrideUrl == WebStrategyType.OVERRIDEURL_ON) {
                if (webStrategy.overrideCallback != null) {
                    return webStrategy.overrideCallback.overrideUrl(view, url);
                }
            }
        }
        return false;
    }

    /**
     * 跳转到拨打电话面板
     */
    private void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    /**
     * 下载监听
     *
     * @return
     * @date 2018/4/23
     */
    private class MyWebViewDownLoadListener implements DownloadListener {

        @SuppressLint("NewApi")
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                    String mimetype, long contentLength) {
            if (mActivity != null) {
                try {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onBackPressed() {
        if (!NetWorkUtils.isNetworkConnected(mActivity)) {
            toolbarTitle = null;
        }
        if (null != toolbarTitle && null != toolbarTitle.getLeftBtns()) {
            if (toolbarTitle.getLeftBtns().size() > 0) {
                ToolbarBeanNew.BtnOpts btnOpts = toolbarTitle.getLeftBtns().get(0);

                if (null != btnOpts && !TextUtils.isEmpty(btnOpts.getAction())) {
                    callHandler(btnOpts.getAction(), "", new CallBackFunction() {
                        @Override
                        public void onCallBack(String data) {

                        }
                    });
                    return;
                }
            }
        }


        if (photoViewPager != null && photoViewPager.getVisibility() == View.VISIBLE) {
            photoViewPager.backClick();
        } else if (mWebView.canGoBack()) {
            //获取历史列表
            WebBackForwardList mWebBackForwardList = mWebView.copyBackForwardList();
            //判断当前历史列表是否最顶端,其实canGoBack已经判断过
            if (mWebBackForwardList.getCurrentIndex() > 0) {
                mWebView.goBack();
                return;
            }
        } else if (mActivity != null){
            mActivity.finish();
        }
    }

    // 向webview发出信息
    private void enableX5FullscreenFunc() {

        if (mWebView.getX5WebViewExtension() != null) {
            Bundle data = new Bundle();

            data.putBoolean("standardFullScreen", false);// true表示标准全屏，false表示X5全屏；不设置默认false，

            data.putBoolean("supportLiteWnd", false);// false：关闭小窗；true：开启小窗；不设置默认true，

            data.putInt("DefaultVideoScreen", 2);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1

            mWebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
        }
    }

    private void disableX5FullscreenFunc() {
        if (mWebView.getX5WebViewExtension() != null) {
            Bundle data = new Bundle();

            data.putBoolean("standardFullScreen",
                    true);// true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，

            data.putBoolean("supportLiteWnd", false);// false：关闭小窗；true：开启小窗；不设置默认true，

            data.putInt("DefaultVideoScreen", 2);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1

            mWebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
        }
    }

    private void enableLiteWndFunc() {
        if (mWebView.getX5WebViewExtension() != null) {
            Bundle data = new Bundle();

            data.putBoolean("standardFullScreen",
                    false);// true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，

            data.putBoolean("supportLiteWnd", true);// false：关闭小窗；true：开启小窗；不设置默认true，

            data.putInt("DefaultVideoScreen", 2);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1

            mWebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
        }
    }

    private void enablePageVideoFunc() {
        if (mWebView.getX5WebViewExtension() != null) {
            Bundle data = new Bundle();

            data.putBoolean("standardFullScreen",
                    false);// true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，

            data.putBoolean("supportLiteWnd", false);// false：关闭小窗；true：开启小窗；不设置默认true，

            data.putInt("DefaultVideoScreen", 1);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1

            mWebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
        }
    }

    /***
     * 显示加载失败时自定义的网页
     */
    protected void showErrorPage() {
        if (mActivity != null && mCommonTitleView != null) {
            visibility = mCommonTitleView.getVisibility();
            mCommonTitleView.setVisibility(visibility);
        }

        if (webParentView == null) {
            webParentView = (RelativeLayout) mWebView.getParent();
        }
        if (mErrorView == null) {
            mErrorView = View.inflate(mActivity, R.layout.layout_networkerror, null);

            TextView tvRetryLoad = mErrorView.findViewById(R.id.tv_retryload);
            TextView tvEmptyTtips = mErrorView.findViewById(R.id.tv_empty_tips);
            ImageView ivEmptyIcon = mErrorView.findViewById(R.id.iv_empty_icon);

            if (PascHybrid.getInstance().mHybridInitConfig.getErrorPagek() != null) {
                CharSequence retryload =
                        PascHybrid.getInstance().mHybridInitConfig.getErrorPagek().RetryLoadText();
                tvRetryLoad.setText(retryload);
            }
            if (PascHybrid.getInstance().mHybridInitConfig.getErrorPagek() != null) {
                CharSequence emptytips =
                        PascHybrid.getInstance().mHybridInitConfig.getErrorPagek().EmptyTtipsText();
                tvEmptyTtips.setText(emptytips);
            }
            if (PascHybrid.getInstance().mHybridInitConfig.getErrorPagek() != null) {
                int emptyicon = PascHybrid.getInstance().mHybridInitConfig.getErrorPagek().EmptyIcon();
                ivEmptyIcon.setImageResource(emptyicon);
            }

            tvRetryLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    retryLoad();
                }
            });
        }
        while (webParentView.getChildCount() > 1) {
            webParentView.removeViewAt(0);
        }
        @SuppressWarnings("deprecation")
        RelativeLayout.LayoutParams lp =
                new RelativeLayout.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT,
                        ViewPager.LayoutParams.MATCH_PARENT);
        webParentView.addView(mErrorView, 0, lp);
        isErrorPage = true;
    }

    public void retryLoad() {
        mWebView.reload();
        isErrorPage = false;
        if (mActivity != null && mCommonTitleView != null) {
            mCommonTitleView.setVisibility(visibility);
        }
    }

    /***
     * 显示无网时自定义的网页
     */
    protected void showNoNetPage() {
        if (mActivity != null && mCommonTitleView != null) {
            visibility = mCommonTitleView.getVisibility();
            mCommonTitleView.setVisibility(visibility);
        }

        if (webParentView == null) {
            webParentView = (RelativeLayout) mWebView.getParent();
        }
        if (mErrorView == null) {
            mErrorView = View.inflate(mActivity, R.layout.layout_networkerror, null);

            TextView tvRetryLoad = mErrorView.findViewById(R.id.tv_retryload);
            TextView tvEmptyTtips = mErrorView.findViewById(R.id.tv_empty_tips);
            ImageView ivEmptyIcon = mErrorView.findViewById(R.id.iv_empty_icon);

            if (PascHybrid.getInstance().mHybridInitConfig.getNoNetPagek() != null) {
                CharSequence retryload =
                        PascHybrid.getInstance().mHybridInitConfig.getNoNetPagek().RetryLoadText();
                tvRetryLoad.setText(retryload);
            }
            if (PascHybrid.getInstance().mHybridInitConfig.getNoNetPagek() != null) {
                CharSequence emptytips =
                        PascHybrid.getInstance().mHybridInitConfig.getNoNetPagek().EmptyTtipsText();
                tvEmptyTtips.setText(emptytips);
            }
            if (PascHybrid.getInstance().mHybridInitConfig.getNoNetPagek() != null) {
                int emptyicon = PascHybrid.getInstance().mHybridInitConfig.getNoNetPagek().EmptyIcon();
                ivEmptyIcon.setImageResource(emptyicon);
            }

            tvRetryLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    retryLoad();
                }
            });
        }
        while (webParentView.getChildCount() > 1) {
            webParentView.removeViewAt(0);
        }
        @SuppressWarnings("deprecation")
        RelativeLayout.LayoutParams lp =
                new RelativeLayout.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT,
                        ViewPager.LayoutParams.MATCH_PARENT);
        webParentView.addView(mErrorView, 0, lp);
        isErrorPage = true;
    }

    /****
     * 把系统自身请求失败时的网页隐藏
     */
    protected void hideErrorPage() {
        if (webParentView == null) {
            return;
        }
        while (webParentView.getChildCount() > 1) {
            webParentView.removeViewAt(0);
        }
        @SuppressWarnings("deprecation")
        RelativeLayout.LayoutParams lp =
                new RelativeLayout.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT,
                        ViewPager.LayoutParams.MATCH_PARENT);
        webParentView.addView(mWebView, 0, lp);
    }

    View myVideoView;
    IX5WebChromeClient.CustomViewCallback callback;
    private FrameLayout fullscreenContainer;

    /**
     * 全屏播放配置
     */
    @Override
    public void onShowCustomView(View view,
                                 IX5WebChromeClient.CustomViewCallback customViewCallback) {
        //设置横屏
        mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        showCoustomView(view, customViewCallback);
    }

    @Override
    public void onShowCustomView(View view, int i,
                                 IX5WebChromeClient.CustomViewCallback customViewCallback) {
        //设置横屏
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        showCoustomView(view, customViewCallback);
    }

    @Override
    public void onHideCustomView() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (myVideoView == null) {
            return;
        }

        setStatusBarVisibility(true);
        FrameLayout decor = (FrameLayout) getActivity().getWindow().getDecorView();
        decor.removeView(fullscreenContainer);
        fullscreenContainer = null;
        myVideoView = null;
        callback.onCustomViewHidden();
    }

    public void showCoustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
        FrameLayout decor = (FrameLayout) getActivity().getWindow().getDecorView();
        fullscreenContainer = new PascWebviewFragment.FullscreenHolder(getActivity());
        fullscreenContainer.addView(view,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        decor.addView(fullscreenContainer,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        myVideoView = view;
        setStatusBarVisibility(false);
        callback = customViewCallback;
    }

    /**
     * 全屏容器界面
     */
    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getActivity().getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 录像
     */
    private void recordVideo(int requestCode) {
        new RxPermissions(getActivity()).request(Manifest.permission.CAMERA, Manifest.permission
                .WRITE_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    //限制时长
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
                    //开启摄像机
                    startActivityForResult(intent, requestCode);
                }
            }
        });
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
