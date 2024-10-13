package com.pasc.lib.hybrid;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.hybrid.behavior.DefaultBehaviorManager;
import com.pasc.lib.hybrid.behavior.WebPageConfig;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.hybrid.callback.WebChromeClientCallback;
import com.pasc.lib.hybrid.callback.WebViewClientListener;
import com.pasc.lib.hybrid.callback.WebViewJavaScriptFunction;
import com.pasc.lib.hybrid.nativeability.WebStrategyType;
import com.pasc.lib.hybrid.util.Constants;
import com.pasc.lib.hybrid.util.NetWorkUtils;
import com.pasc.lib.hybrid.util.Utils;
import com.pasc.lib.hybrid.webview.PascWebView;
import com.pasc.lib.hybrid.widget.CommonDialog;
import com.pasc.lib.smtbrowser.util.BrowserUtils;
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class PascWebviewFragment extends BaseFragment
        implements WebChromeClientCallback, WebViewClientListener {
    private final String TAG = "PASC_HYBRID";
    public final static int FILECHOOSER_RESULTCODE = 0x1000;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 0x1001;
    /**
     * hybrid调用openNewWebView需要调用startActivityForResult才有activity finish的回调
     */
    public static final int REQUEST_CODE_OPEN_NEW_WEBVIEW = 0x3000;

    /**
     * 调用相机拍照文件名
     */
    private static final String TAKE_PHOTO_FILE_NAME = "smtphoto.jpg";

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
    private PascWebviewActivity pascWebviewActivity;
    int mProgress;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PascWebviewActivity) {
            pascWebviewActivity = (PascWebviewActivity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        pascWebviewActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_webview, container, false);
        mWebView = mRootView.findViewById(R.id.pasc_webview);
        mProgressbar = mRootView.findViewById(R.id.mprogressBar);
        mProgressbar.setVisibility(View.VISIBLE);
        initWebView();

        mActivity.getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mWebView.getView().setOverScrollMode(View.OVER_SCROLL_ALWAYS);
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
                if (null != pascWebviewActivity
                        && null != pascWebviewActivity.toolbarTitleBean
                        && pascWebviewActivity
                        .toolbarTitleBean.isProgressiveOpacity()) {
                    int height = mActivity.getWindowManager().getDefaultDisplay().getHeight() / 3;
                    float f = (float) t / height;
                    if (f > 1.0f) {
                        f = 1.0f;
                    }
                    pascWebviewActivity.mCommonTitleView.setAlpha(f);
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
                            .setItems(new String[]{"保存图片"}, (dialog, which) -> {
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

    private void initWebView() {
        if (null == mWebView) {
            throw new RuntimeException("WebView can not be null!");
        }

        if (null != mWebView.getWebChromeClient()) {
            mWebView.getWebChromeClient().setWebChromeClientCallback(this);
        }
        mWebView.setWebViewClientListener(this);
        if (PascHybrid.getInstance().getHybridInitConfig() != null && PascHybrid.getInstance().getHybridInitConfig().getHybridInitCallback() != null) {
            PascHybrid.getInstance()
                    .getHybridInitConfig()
                    .getHybridInitCallback()
                    .onWebViewCreate(mWebView);
        }
        if (mUrl.equals(Constants.TEMP_URL)
                && (System.currentTimeMillis() - Constants.TEMP_CLEAR_CACHE_TIME) < 5000) {
            mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            Constants.TEMP_URL = "";
            Constants.TEMP_CLEAR_CACHE_TIME = 0;
        } else {
            Constants.TEMP_URL = mUrl;
            Constants.TEMP_CLEAR_CACHE_TIME = System.currentTimeMillis();
        }
        initWebConfig();
        initStrategy();
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
            mWebView.setDownloadListener(new MyWebViewDownLoadListener());
            if (!TextUtils.isEmpty(mUrl)) {
                loadUrlIntoWebView(mUrl);
            }
        }

        mWebView.callHandler(ConstantBehaviorName.CALL_ENTER_WEBVIEW, "", null);
    }

    public void initStrategy() {

        if (null == pascWebviewActivity || null == pascWebviewActivity.webStrategy) {
            return;
        }
        if (PascHybrid.getInstance().getHybridInitConfig() != null
                && PascHybrid.getInstance().getHybridInitConfig().getOldLogicCallback() != null
                && pascWebviewActivity.webStrategy.oldJsInterface == WebStrategyType.OLD_INTERFACE_ON) {
            //兼容旧框架注册interface
            PascHybrid.getInstance()
                    .getHybridInitConfig()
                    .getOldLogicCallback()
                    .oldInterfaceCallback(pascWebviewActivity.mCommonTitleView, mWebView);
        }
        //控制toolbarcolor时隐藏progressbar
        if (pascWebviewActivity.webStrategy.toolBarColor != null) {
            mProgressbar.setVisibility(View.GONE);
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
        boolean logEnable = PascHybrid.getInstance().getHybridInitConfig() != null
                && PascHybrid.getInstance().getHybridInitConfig().isLogEnable();
        if (logEnable) {
            Log.d(TAG, "WebView start to load url. url=" + url);
        }
        boolean isOfflineMode = false;
        if (!TextUtils.isEmpty(url) && url.startsWith("file:///")) {
            isOfflineMode = true;
        }
        if (isOfflineMode) {
            mWebView.getSettings().setAllowFileAccess(true);
            //通过此API可以设置是否允许通过file url加载的Javascript读取其他的本地文件
            mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
            //通过此API可以设置是否允许通过file url加载的Javascript可以访问其他的源，包括其他的文件和http,https等其他的源。
            mWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            //设置缓存模式：判断是否有网络，有的话，使用LOAD_DEFAULT，无网络时，使用LOAD_CACHE_ELSE_NETWORK
            if (NetWorkUtils.isNetworkConnected(getContext())) {
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            } else {
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }
        }
        if (url.contains("https://www.honglutec.com")) {
            Map<String, String> map = new HashMap<>();
            map.put("Referer", "https://www.honglutec.com");
            mWebView.loadUrl(url, map);
        } else if (url.contains("wx.tenpay.com")) {
            Map<String, String> map = new HashMap<>();
            map.put("Referer", "https://www.honglutec.com");
            mWebView.loadUrl(url, map);
        } else {
            mWebView.loadUrl(url);
        }
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
        } else if ("image/*".equals(acceptType) && "camera".equals(capture)) {
            recordImage(FILECHOOSER_RESULTCODE);
        } else {
            new RxPermissions(getActivity()).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                                i.addCategory(Intent.CATEGORY_OPENABLE);
                                if (TextUtils.isEmpty(acceptType)) {
                                    i.setType("*/*");
                                } else {
                                    i.setType(acceptType);
                                }
                                i.setType(acceptType);
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
        } else if ("image/*".equals(fileChooserParams.getAcceptTypes()[0])
                && fileChooserParams.isCaptureEnabled()) {
            recordImage(FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
        } else {
            new RxPermissions(getActivity()).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                                if (TextUtils.isEmpty(fileChooserParams.getAcceptTypes()[0])) {
                                    contentSelectionIntent.setType("*/*");
                                } else {
                                    contentSelectionIntent.setType(fileChooserParams.getAcceptTypes()[0]);
                                }
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
        if (null == pascWebviewActivity) {
            return;
        }

        if (mWebView.copyBackForwardList().getCurrentIndex() > 0) {
            //pascWebviewActivity.mCommonTitleView.setLeftTextVisibility(View.VISIBLE);
            if (pascWebviewActivity.webStrategy != null
                    && pascWebviewActivity.webStrategy.oldToolBarRecycle
                    == WebStrategyType.OLD_TOOLBAR_RECYCLE_ON) {
                if (PascHybrid.getInstance().toolBarCallback != null
                        && pascWebviewActivity.mCommonTitleView != null && pascWebviewActivity != null) {
                    PascHybrid.getInstance().toolBarCallback.toolBarRecycleCallback(pascWebviewActivity
                            .mCommonTitleView, mActivity, false);
                }
            }
        } else {
            //pascWebviewActivity.mCommonTitleView.setLeftTextVisibility(View.GONE);
            if (pascWebviewActivity.webStrategy != null
                    && pascWebviewActivity.webStrategy.oldToolBarRecycle
                    == WebStrategyType.OLD_TOOLBAR_RECYCLE_ON) {
                if (PascHybrid.getInstance().toolBarCallback != null
                        && pascWebviewActivity.mCommonTitleView != null && pascWebviewActivity != null) {
                    PascHybrid.getInstance().toolBarCallback.toolBarRecycleCallback(pascWebviewActivity
                            .mCommonTitleView, mActivity, true);
                }
            }
            if (pascWebviewActivity.titleLevel == PascWebviewActivity.TITLE_FRIST_PAGE) {
                pascWebviewActivity.mCommonTitleView.setTitleText(title);
            }
        }
        String titleText = title;
        //toolbartitle由web主动控制或者通过url下发优先级最高，webstrategy为本地控制优先级中，网页获取title优先级最低
        if (pascWebviewActivity.toolbarTitleBean != null && TextUtils.isEmpty(pascWebviewActivity
                .toolbarTitleBean.getWebViewTitle())) {
            if (pascWebviewActivity.webStrategy == null
                    || pascWebviewActivity.webStrategy.title == null) {
                titleText = title;
                if (!TextUtils.isEmpty(title) && !title.startsWith("http")) {
                    //不为http开头的地址时才显示标题
                    pascWebviewActivity.mCommonTitleView.setTitleText(title);
                }
            } else {
                titleText = pascWebviewActivity.webStrategy.title;
                pascWebviewActivity.mCommonTitleView.setTitleText(pascWebviewActivity.webStrategy.title);
            }
        } else if (pascWebviewActivity.toolbarTitleBean == null
                && pascWebviewActivity.webStrategy != null
                && pascWebviewActivity.webStrategy.title != null) {
            titleText = pascWebviewActivity.webStrategy.title;
            pascWebviewActivity.mCommonTitleView.setTitleText(pascWebviewActivity.webStrategy.title);
        } else {
            titleText = title;
            pascWebviewActivity.mCommonTitleView.setTitleText(title);
        }
        //上报页面埋点
        if (PascHybrid.getInstance().statisticsCallback != null) {
            PascHybrid.getInstance().statisticsCallback.uploadWebviewPageData(titleText);
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
        if (PascHybrid.getInstance().getHybridInitConfig() != null
                && PascHybrid.getInstance().getHybridInitConfig().getHybridInitCallback() != null) {
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
            if (null == pascWebviewActivity
                    || pascWebviewActivity.webStrategy == null
                    || mWebView == null) {
                return;
            }
            if (pascWebviewActivity.webStrategy.title == null) {
                if (!TextUtils.isEmpty(mWebView.getTitle()) && !mWebView.getTitle().startsWith("http")) {
                    //不为http开头的地址时才显示标题
                    pascWebviewActivity.mCommonTitleView.setTitleText(mWebView.getTitle());
                }
            } else {
                pascWebviewActivity.mCommonTitleView.setTitleText(pascWebviewActivity.webStrategy.title);
            }
            if (PascHybrid.getInstance().mHybridInitConfig.getHybridInitCallback().titleCloseButton()
                    == WebStrategyType.CLOSEBUTTON_FRISTPAGE_GONE
                    && mWebView != null
                    && mWebView.canGoBack()) {
                pascWebviewActivity.mCommonTitleView.setLeftTextVisibility(View.VISIBLE);
            } else if (PascHybrid.getInstance().mHybridInitConfig.getHybridInitCallback()
                    .titleCloseButton()
                    == WebStrategyType.CLOSEBUTTON_ALWAKES_VISIBLE) {
                pascWebviewActivity.mCommonTitleView.setLeftTextVisibility(View.VISIBLE);
            } else {
                pascWebviewActivity.mCommonTitleView.setLeftTextVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.i(TAG, "onActivityResult intent:" + intent);
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
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                if (resultCode == RESULT_OK) { //拍照如果指定了URL，则intent为空
                    File photoFile = new File(Environment.getExternalStorageDirectory(), TAKE_PHOTO_FILE_NAME);

                    if (photoFile != null && photoFile.exists()) {
                        result = Uri.fromFile(photoFile);
                        mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
                    } else {
                        mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
                    }
                } else {
                    mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
                }

            }
            mUploadMessageForAndroid5 = null;
        } else if (requestCode == REQUEST_CODE_OPEN_NEW_WEBVIEW) {
            if (mWebView != null) {
                mWebView.callHandler(ConstantBehaviorName.CALL_ENTER_WEBVIEW, "", null);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
        mWebView.callHandler(ConstantBehaviorName.CALL_CLOSE_WEBVIEW, "", new CallBackFunction() {
            @Override
            public void onCallBack(String data) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Utils.isBackground(mActivity)) {
            mWebView.callHandler(ConstantBehaviorName.CALL_EXIT_APP, "", new CallBackFunction() {
                @Override
                public void onCallBack(String data) {

                }
            });
        }
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
        super.onDestroy();
        Constants.TEMP_CLEAR_CACHE_TIME = System.currentTimeMillis();
        if (null != mWebView) {
            mWebView.clearHistory();
            if (mWebView.getParent() != null) {
                ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            }
            loadUrlIntoWebView("about:blank");
            mWebView.stopLoading();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.destroy();
        }
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        mProgress = 0;
        if (PascHybrid.getInstance().getHybridInitConfig() != null
                && PascHybrid.getInstance().getHybridInitConfig().getOldLogicCallback() != null) {
            PascHybrid.getInstance()
                    .getHybridInitConfig()
                    .getOldLogicCallback()
                    .onInterceptPageStarted(mWebView, url);
        }
        if (null == pascWebviewActivity) {
            return;
        }
        if (null == pascWebviewActivity.webStrategy) {
            pascWebviewActivity.setStatusBarBgColor(Color.WHITE);
            BrowserUtils.setStatusBarTxColor(getActivity(), true);
            return;
        }
        if (pascWebviewActivity.webStrategy.statusBarVisibility
                == WebStrategyType.STATUSBAR_VISIBLE) {
            if (pascWebviewActivity.webStrategy.statusBarColor != null) {
                pascWebviewActivity.setStatusBarBgColor(
                        Color.parseColor(pascWebviewActivity.webStrategy.statusBarColor));
            } else {
                pascWebviewActivity.setStatusBarBgColor(Color.WHITE);
                BrowserUtils.setStatusBarTxColor(getActivity(), true);

            }
        } else if (pascWebviewActivity.webStrategy.statusBarVisibility
                == WebStrategyType.STATUSBAR_FOLLOWING) {
            if (pascWebviewActivity.webStrategy.statusBarColor != null) {
                pascWebviewActivity.setStatusBarBgColor(
                        Color.parseColor(pascWebviewActivity.webStrategy.statusBarColor));
            }
        } else if (pascWebviewActivity.webStrategy.statusBarVisibility
                == WebStrategyType.STATUSBAR_GONE) {
            pascWebviewActivity.setStatusBarVisibility(View.GONE);
        }

        //        if (mWebView != null && mWebView.canGoBack()) {
        //            pascWebviewActivity.mCommonTitleView.setLeftTextVisibility(View.VISIBLE);
        //        } else {
        //            pascWebviewActivity.mCommonTitleView.setLeftTextVisibility(View.GONE);
        //        }

        //        String jsStr = "";
        //        try {
        //            InputStream in = getContext().getAssets().open("testOverride.js");
        //            byte buff[] = new byte[1024];
        //            ByteArrayOutputStream fromFile = new ByteArrayOutputStream();
        //            do {
        //                int numRead = in.read(buff);
        //                if (numRead <= 0) {
        //                    break;
        //                }
        //                fromFile.write(buff, 0, numRead);
        //            } while (true);
        //            jsStr = fromFile.toString();
        //            in.close();
        //            fromFile.close();
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }
        //        mWebView.evaluateJavascript(jsStr, new ValueCallback<String>() {
        //            @Override public void onReceiveValue(String value) {//js与native交互的回调函数
        //                Log.d(TAG, "value=" + value);
        //            }
        //        });
    }

    @Override
    public void onPageFinished(WebView webView, String url) {
        if (!isErrorPage) {
            hideErrorPage();
        }
        if (PascHybrid.getInstance().getHybridInitConfig() != null
                && PascHybrid.getInstance().getHybridInitConfig().getHybridInitCallback() != null) {
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
        boolean logEnable = PascHybrid.getInstance().getHybridInitConfig() != null
                && PascHybrid.getInstance().getHybridInitConfig().isLogEnable();
        if (logEnable) {
            Log.d(TAG, "Load web failed. errorCode=" + errorCode + ",description=" + description + ",failingUrl=" + failingUrl);
        }

        //显示错误页或无网页时恢复页面的状态栏和toolbar
        if (null != pascWebviewActivity) {
            BrowserUtils.setStatusBarTxColor(pascWebviewActivity, true);
            if (null != pascWebviewActivity.mCommonTitleView) {
                pascWebviewActivity.mCommonTitleView.setVisibility(View.VISIBLE);
            }
        }

        if (NetWorkUtils.isNetworkConnected(mActivity)) {
            showErrorPage();
        } else {
            showNoNetPage();
        }

        if (PascHybrid.getInstance().getHybridInitConfig() != null
                && null != PascHybrid.getInstance().getHybridInitConfig().getWebErrorListener()) {
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

        //Utils.showSslErrorDialog(view.getContext(), handler, mUrl);

        //        boolean enableHostCheck = false;
        //        if (enableHostCheck) {
        ////            if (view != null && !TextUtils.isEmpty(view.getUrl())) {
        ////                String url = view.getUrl();
        ////                Uri uri = Uri.parse(url);
        ////                if (uri != null) {
        ////                    String host = uri.getHost();
        ////                    //白名单
        ////                    for (String filterHost : WHITE_HTTPS_HOST_FILTER) {
        ////                        if (host.matches(filterHost)) {
        ////                            handler.proceed();
        ////                            return;
        ////                        }
        ////                    }
        ////                }
        ////            }
        //            Utils.showSslErrorDialog(view.getContext(), handler, mUrl);
        //        } else {
        //            handler.proceed();
        //        }
        //        handler.proceed();
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
        if (pascWebviewActivity != null && pascWebviewActivity.webStrategy != null) {
            if (pascWebviewActivity.webStrategy.oldIntercept == WebStrategyType.OLD_INTERCEPT_ON
                    && PascHybrid.getInstance().getHybridInitConfig() != null
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
//        if (url.contains("://")) {
//            if (url.startsWith("http://") || url.contains("https://")) {
//            } else {
//                try {
//                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                    startActivity(intent);
//                } catch (Exception e) {
//                    Log.e("Exception", "" + e.getMessage());
//                    Toast.makeText(getContext(), "未发现需要打开的页面", Toast.LENGTH_LONG);
//
//                }
//            }
//            return true;
//        }
        //拦截点评的私有协议
        if (url.startsWith("dianping:")) {
            return true;
        }
        if (url.startsWith("baidumap:") || url.startsWith("baidumap")) {//百度地图
            try {
                mActivity.startActivity(
                        new Intent("android.intent.action.VIEW", Uri.parse(url)));
                return true;
            } catch (Exception e) {
                Intent intent = new Intent();
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
                Intent intent = new Intent();
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
                Intent intent = new Intent();
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
            Intent intent = new Intent();
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
        if (url.startsWith("https://mp.weixin.qq.com/mp/profile_ext") || url.startsWith("http://mp.weixin.qq.com/mp/profile_ext")) {
            return true;
        }
        if (pascWebviewActivity != null && pascWebviewActivity.webStrategy != null) {
            if (pascWebviewActivity.webStrategy.overrideUrl == WebStrategyType.OVERRIDEURL_ON) {
                if (pascWebviewActivity.webStrategy.overrideCallback != null) {
                    return pascWebviewActivity.webStrategy.overrideCallback.overrideUrl(view, url);
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

    public boolean onBackPressed() {
        if (photoViewPager != null && photoViewPager.getVisibility() == View.VISIBLE) {
            photoViewPager.backClick();
        } else if (mWebView.canGoBack()) {
            //获取历史列表
            WebBackForwardList mWebBackForwardList = mWebView.copyBackForwardList();
            //判断当前历史列表是否最顶端,其实canGoBack已经判断过
            if (mWebBackForwardList.getCurrentIndex() > 0) {
                mWebView.goBack();
                return true;
            }
        } else {
            if (mActivity != null) {
                mActivity.finish();
            }
            return true;
        }
        return false;
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
        if (pascWebviewActivity != null && pascWebviewActivity.mCommonTitleView != null) {
            visibility = pascWebviewActivity.mCommonTitleView.getVisibility();
            pascWebviewActivity.mCommonTitleView.setVisibility(View.VISIBLE);
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
                    mWebView.reload();
                    isErrorPage = false;
                    if (pascWebviewActivity != null && pascWebviewActivity.mCommonTitleView != null) {
                        pascWebviewActivity.mCommonTitleView.setVisibility(visibility);
                    }
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

    /***
     * 显示无网时自定义的网页
     */
    protected void showNoNetPage() {
        if (pascWebviewActivity != null && pascWebviewActivity.mCommonTitleView != null) {
            visibility = pascWebviewActivity.mCommonTitleView.getVisibility();
            pascWebviewActivity.mCommonTitleView.setVisibility(View.VISIBLE);
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
                    mWebView.reload();
                    isErrorPage = false;
                    if (pascWebviewActivity != null && pascWebviewActivity.mCommonTitleView != null) {
                        pascWebviewActivity.mCommonTitleView.setVisibility(visibility);
                    }
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
        fullscreenContainer = new FullscreenHolder(getActivity());
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
     * 拍照
     */
    private void recordImage(int requestCode) {
        new RxPermissions(getActivity()).request(Manifest.permission.CAMERA, Manifest.permission
                .WRITE_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    //这里需要写死拍照的图片地址，不然回调的时候没有照片
                    Uri uri = null;
                    ;
                    try {
                        uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", new File(Environment.getExternalStorageDirectory(), TAKE_PHOTO_FILE_NAME));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    //开启摄像头
                    startActivityForResult(intent, requestCode);
                }
            }
        });
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
}
