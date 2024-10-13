package com.pasc.libbrowser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.widget.ImageView;
import com.example.chenkun305.libbrowser.BuildConfig;
import com.example.chenkun305.libbrowser.R;
import com.pasc.lib.base.AppProxy;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.hybrid.HybridInitConfig;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.hybrid.callback.ErrorPageback;
import com.pasc.lib.hybrid.callback.HybridInitCallback;
import com.pasc.lib.hybrid.callback.InjectJsCallback;
import com.pasc.lib.hybrid.callback.NoNetPageback;
import com.pasc.lib.hybrid.callback.OldLogicCallback;
import com.pasc.lib.hybrid.eh.HybridEH;
import com.pasc.lib.hybrid.eh.behavior.AddressNavigationBehavior;
import com.pasc.lib.hybrid.eh.behavior.ChooseImageBehavior;
import com.pasc.lib.hybrid.eh.behavior.GetUserInfoBehavior;
import com.pasc.lib.hybrid.eh.behavior.GetVideoBehavior;
import com.pasc.lib.hybrid.eh.behavior.MapRouteBehavior;
import com.pasc.lib.hybrid.eh.behavior.OPGetGPSInfoBehavior;
import com.pasc.lib.hybrid.eh.behavior.PlayVideoBehavior;
import com.pasc.lib.hybrid.eh.behavior.PreviewPhotoBehavior;
import com.pasc.lib.hybrid.eh.behavior.RecordAudioBehavior;
import com.pasc.lib.hybrid.nativeability.WebStrategyType;
import com.pasc.lib.hybrid.webview.PascWebView;
import com.pasc.lib.hybrid.widget.WebCommonTitleView;
import com.pasc.lib.net.NetConfig;
import com.pasc.lib.net.NetManager;
import com.pasc.lib.net.download.DownLoadManager;
import com.pasc.lib.openplatform.CertificationCallback;
import com.pasc.lib.openplatform.IBizCallback;
import com.pasc.lib.openplatform.InitJSSDKBehavior;
import com.pasc.lib.openplatform.OpenPlatformProvider;
import com.pasc.lib.openplatform.PascOpenPlatform;
import com.pasc.lib.openplatform.UserAuthBehavior;
import com.pasc.libbrowser.behavior.NativeRouteBehavior;
import com.pasc.libbrowser.behavior.ScanQRBehavior;
import com.pasc.libbrowser.behavior.ShareBehavior;
import com.pasc.libbrowser.behavior.UserAddressAuthBehaviorImpl;
import com.pasc.libbrowser.behavior.WebCallbackBehavior;
import com.pasc.libbrowser.utils.ConvertUtil;
import com.pasc.libbrowser.utils.DeviceUtils;
import com.pasc.libbrowser.utils.HeaderUtil;
import com.squareup.picasso.Picasso;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class App extends Application {

  private static final String TAG = "App";
  private static String SDCARD_LOG_FILE_DIR = "Smart/log";//日志保存目录
  private static String DEFAULT_LOG_TAG = "smt";//日志tag
  private static String SYSTEM_ID = "wdsz";//日志搜集app系统标识

  private static Context applicationContext;

  public static IWXAPI api;

  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  @TargetApi(Build.VERSION_CODES.KITKAT)
  @Override
  public void onCreate() {
    super.onCreate();
    applicationContext = getApplicationContext();

    Runtime rt = Runtime.getRuntime();
    long maxMemory = rt.maxMemory();
    Log.i("maxMemory:", Long.toString(maxMemory / (1024 * 1024)));

    AppProxy.getInstance().init(this,false).setHost("https://csapi.csx.cn/");
    android.webkit.WebView.setWebContentsDebuggingEnabled(true);
    initNet();
    HybridEH.init(this);
    //com.tencent.smtt.sdk.WebView.setWebContentsDebuggingEnabled(true);

    PascHybrid pascHybrid = PascHybrid.getInstance();
    HybridInitConfig hybridInitConfig = new HybridInitConfig();
    hybridInitConfig.setLogEnable(true);
    pascHybrid.init(hybridInitConfig
        .addCustomerBehavior(ConstantBehaviorName.GET_GPS_INFO, new OPGetGPSInfoBehavior())
            .addCustomerBehavior(ConstantBehaviorName.OPEN_SHARE, new ShareBehavior())
        .addCustomerBehavior(ConstantBehaviorName.QR_CODE_SCAN, new ScanQRBehavior())
        .addCustomerBehavior(ConstantBehaviorName.INIT_JSSDK, new InitJSSDKBehavior())
        .addCustomerBehavior(ConstantBehaviorName.USER_AUTH, new UserAuthBehavior())
            .addCustomerBehavior(ConstantBehaviorName.USER_ADDRESS_AUTH, new UserAddressAuthBehaviorImpl())
        .addCustomerBehavior(ConstantBehaviorName.CHOOSE_VIDEO, new GetVideoBehavior())
        .addCustomerBehavior(ConstantBehaviorName.PLAY_VIDEO, new PlayVideoBehavior())
        .addCustomerBehavior(ConstantBehaviorName.CHOOSEIMAGE, new ChooseImageBehavior())
        .addCustomerBehavior(ConstantBehaviorName.PREVIEW_IMAGE, new PreviewPhotoBehavior())
        .addCustomerBehavior(ConstantBehaviorName.WEB_CALLBACK, new WebCallbackBehavior())
            .addCustomerBehavior(ConstantBehaviorName.OPEN_LOCATION, new MapRouteBehavior())
        .addCustomerBehavior("PASC.app.openLocation", new AddressNavigationBehavior())
        .addCustomerBehavior (ConstantBehaviorName.AUDIO_RECORD, new RecordAudioBehavior(3600), "录音能力")
        .setHybridInitCallback(new HybridInitCallback() {
          @Override
          public void loadImage(ImageView imageView, String url) {
  
          }

          @Override
          public void setWebSettings(WebSettings settings) {
            settings.setUserAgent(settings.getUserAgentString()
                + "/openweb=paschybrid/SZSMT_Android,VERSION:"
                + DeviceUtils.getVersionName(App.this));
          }

          @Override
          public String themeColorString() {
            return "#ff0000";
          }

          @Override
          public int titleCloseStyle() {
            return WebStrategyType.CLOSEBUTTON_IMAGE;
          }

          @Override public void onWebViewCreate(WebView webView) {

          }

          @Override public void onWebViewProgressChanged(WebView webView, int progress) {

          }

          @Override public void onWebViewPageFinished(WebView webView, String url) {

          }

          @Override
          public int titleCloseButton() {
            return WebStrategyType.CLOSEBUTTON_FRISTPAGE_GONE;
          }
        }).setOldLogicCallback(new OldLogicCallback() {
          @Override
          public void oldCollection(WebCommonTitleView mCommonTitleView) {

          }

          @Override
          public boolean oldInterceptCallback(WebView webView, String url) {
            return false;
          }

          @Override
          public void oldInterfaceCallback(WebCommonTitleView webCommonTitleView, WebView webView) {

          }

          @Override
          public void oldActivityResultCallback(WebView webView, int requestCode, int resultCode,
              Intent data) {

          }

          @Override
          public void oldNativeClose(PascWebviewActivity pascWebviewActivity) {

          }

          @Override
          public void onInterceptPageStarted(WebView webView, String url) {

          }
        })
        .setErrorPagek(new ErrorPageback() {
          @Override
          public CharSequence RetryLoadText() {
            return "重新加载";
          }

          @Override
          public CharSequence EmptyTtipsText() {
            return "加载发生异常";
          }

          @Override
          public int EmptyIcon() {
            return R.drawable.ic_empty_zhihu;
          }
        })
        .setNoNetPagek(new NoNetPageback() {
          @Override
          public CharSequence RetryLoadText() {
            return "重新加载";
          }

          @Override
          public CharSequence EmptyTtipsText() {
            return "网络开小差";
          }

          @Override
          public int EmptyIcon() {
            return R.drawable.ic_empty_dracula;
          }
        })
        .setInjectJsCallback(new InjectJsCallback() {
          @Override
          public void injectJs(WebView mWebView) {
            String jsStr = "";
            try {
              InputStream in = getContext().getAssets().open("statsEvent.js");
              byte buff[] = new byte[1024];
              ByteArrayOutputStream fromFile = new ByteArrayOutputStream();
              do {
                int numRead = in.read(buff);
                if (numRead <= 0) {
                  break;
                }
                fromFile.write(buff, 0, numRead);
              } while (true);
              jsStr = fromFile.toString();
              in.close();
              fromFile.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
            mWebView.evaluateJavascript(jsStr, new ValueCallback<String>() {
              @Override public void onReceiveValue(String value) {//js与native交互的回调函数
                Log.d(TAG, "value=" + value);
              }
            });

            //                        String jsStr1 = "";
            //                        try {
            //                            InputStream in = getContext().getAssets().open("backOverride");
            //                            byte buff[] = new byte[1024];
            //                            ByteArrayOutputStream fromFile = new ByteArrayOutputStream();
            //                            do {
            //                                int numRead = in.read(buff);
            //                                if (numRead <= 0) {
            //                                    break;
            //                                }
            //                                fromFile.write(buff, 0, numRead);
            //                            } while (true);
            //                            jsStr1 = fromFile.toString();
            //                            in.close();
            //                            fromFile.close();
            //                        } catch (IOException e) {
            //                            e.printStackTrace();
            //                        }
            //                        mWebView.evaluateJavascript(jsStr1, new ValueCallback<String>() {
            //                            @Override public void onReceiveValue(String value) {//js与native交互的回调函数
            //                                Log.d(TAG, "value=" + value);
            //                            }
            //                        });
          }
        })
    );

    PascOpenPlatform.getInstance().init(new OpenPlatformProvider() {
      /*
         开放平台的baseURL，不同城市有不同的host
         */
      @Override
      public String getOpenPlatformBaseUrl() {
        return "https://csapi.csx.cn/api/opening";
        //                return BuildConfig.DEBUG ? "http://sz-smt-zag-stg1.pingan.com.cn:10080/smtapp"
        //                        : "https://smt-app.pingan.com.cn/smtapp";
        // https://isz-cloud-stg2.yun.city.pingan.com/smtapp/openPlatform/initCode/checkInitCode.do
      }

      @Override
      public void getUserToken(IBizCallback iBizCallback) {
        //13008814471: E32BCF0F373343BEB307D4048CC0FC5C1586488315998
        //13008814475: 9D0D10809F254E33AFCFE8A21051CC001586487672839
        iBizCallback.onLoginSuccess("88C814C18DC1428196469CC7046D22491661410179940");
      }

      @Override
      public void getCertification(Context context, CertificationCallback certificationCallback) {
        certificationCallback.certification(true);
      }

      /*
         开放平台可以动态注册交互行为，由server端返回交互行为名称列表，我们再去动态注册交互行为，可以保证
         一定的安全性
         nativeApis: 需要注册的交互行为协议名称列表
         */
      @Override
      public void openPlatformBehavior(PascWebView pascWebView, List<String> nativeApis) {
        if (nativeApis != null) {
          for (int i = 0; i < nativeApis.size(); i++) {
            switch (nativeApis.get(i)) {
              case ConstantBehaviorName.OP_LOCATION:
                pascWebView.registerBehavior(ConstantBehaviorName.OP_LOCATION,
                    new AddressNavigationBehavior());
                break;
              case ConstantBehaviorName.OP_GPS:
                pascWebView.registerBehavior(ConstantBehaviorName.OP_GPS,
                    new OPGetGPSInfoBehavior());
                break;
              case ConstantBehaviorName.OP_NAVIGATION:
                pascWebView.registerBehavior(ConstantBehaviorName.OP_NAVIGATION,
                    new MapRouteBehavior());
              case ConstantBehaviorName.USER_ADDRESS_AUTH:
                pascWebView.registerBehavior(ConstantBehaviorName.USER_ADDRESS_AUTH,
                        new UserAddressAuthBehaviorImpl());
                break;
              case ConstantBehaviorName.OP_QRCODE:
                pascWebView.registerBehavior(ConstantBehaviorName.OP_QRCODE,
                        new ScanQRBehavior());
                break;
              case ConstantBehaviorName.AUDIO_RECORD:
                pascWebView.registerBehavior (ConstantBehaviorName.AUDIO_RECORD,
                    new RecordAudioBehavior (3600));
                break;
              case ConstantBehaviorName.OP_USERINFO:
                pascWebView.registerBehavior(ConstantBehaviorName.OP_USERINFO,
                        new GetUserInfoBehavior());
                break;
              case ConstantBehaviorName.OP_ROUTER:
                pascWebView.registerBehavior(ConstantBehaviorName.OP_ROUTER,
                        new NativeRouteBehavior());
                break;
            }
          }
        }
      }

      @Override public void onOpenPlatformError(int code, String msg) {
        ToastUtils.toastMsg(msg);
      }

      @Override public int getAppIcon() {
        return 0;
      }

      @Override public int getStyleColor() {
        return R.color.blue_19b2ba;
      }

      @Override
      public int getBackIconColor() {
        return 0;
      }

      @Override
      public void authClick(boolean b, String s, String s1) {

      }
    });

    QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

      @Override
      public void onViewInitFinished(boolean arg0) {
        //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
        Log.d(TAG, " onViewInitFinished is " + arg0);
      }

      @Override
      public void onCoreInitFinished() {
      }
    };
    //x5内核初始化接口
    QbSdk.initX5Environment(getApplicationContext(), cb);
    initWXShare();


    registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
      @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(activity.getClass().getSimpleName() , "onActivityCreated");
      }

      @Override public void onActivityStarted(Activity activity) {
        Log.d(activity.getClass().getSimpleName() , "onActivityStarted");
      }

      @Override public void onActivityResumed(Activity activity) {
        Log.d(activity.getClass().getSimpleName() , "onActivityResumed");
      }

      @Override public void onActivityPaused(Activity activity) {
        Log.d(activity.getClass().getSimpleName() , "onActivityPaused");
      }

      @Override public void onActivityStopped(Activity activity) {
        Log.d(activity.getClass().getSimpleName() , "onActivityStopped");
      }

      @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d(activity.getClass().getSimpleName() , "onActivitySaveInstanceState");
      }

      @Override public void onActivityDestroyed(Activity activity) {
        Log.d(activity.getClass().getSimpleName() , "onActivityDestroyed");
      }
    });
  }

  private void initWXShare() {
    api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
    api.registerApp(Constants.APP_ID);
  }

  public static Context getContext() {
    return applicationContext;
  }

  void initNet() {
    NetConfig config = new NetConfig.Builder(this)
        .baseUrl("https://csapi.csx.cn/")
        .headers(HeaderUtil.getHeaders(BuildConfig.DEBUG, null))
        .gson(ConvertUtil.getConvertGson())
        .isDebug(BuildConfig.DEBUG)
        .build();
    NetManager.init(config);

    DownLoadManager.getDownInstance().init(this, 3, 5, 0);
  }
}
