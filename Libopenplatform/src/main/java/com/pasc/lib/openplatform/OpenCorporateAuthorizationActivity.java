package com.pasc.lib.openplatform;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.hybrid.widget.WebCommonTitleView;
import com.pasc.lib.net.resp.BaseRespThrowableObserver;
import com.pasc.lib.openplatform.network.OpenBiz;
import com.pasc.lib.openplatform.resp.OpenIdResp;
import com.pasc.lib.openplatform.resp.RequestCodeResp;
import com.pasc.lib.openplatform.resp.ServiceAuthResult;
import com.pasc.lib.openplatform.resp.ServiceInfoResp;
import com.pasc.lib.openplatform.util.OpenPlatformUtils;
import com.pasc.libopenplatform.R;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import org.json.JSONException;
import org.json.JSONObject;

public class OpenCorporateAuthorizationActivity extends AppCompatActivity
    implements View.OnClickListener {

  private static String KEY_APPID = "key_appId";
  private static String KEY_TOKEN = "key_token";
  private static String KEY_CERTIFICATION = "key_certification";
  private Context mContext;
  private WebCommonTitleView mTitleView;
  private TextView tvApp, tvService, tvAuthTip;
  private TextView tvConfirm, tvCancel;
  private ImageView ivApp, ivService;
  private String serviceName;
  private String appId;
  private String unionId;
  private String token;
  private String certification;
  public CompositeDisposable disposables = new CompositeDisposable();
  private String url = "";
  private View authView;

  public static void start(Context context, String appId, String token, String certification) {
    Intent intent = new Intent(context, OpenCorporateAuthorizationActivity.class);
    intent.putExtra(KEY_APPID, appId);
    intent.putExtra(KEY_TOKEN, token);
    intent.putExtra(KEY_CERTIFICATION, certification);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = OpenCorporateAuthorizationActivity.this;
    setContentView(R.layout.dialog_open_authorization);
    initView();
    initData();
  }

  private void initView() {
    tvConfirm = findViewById(R.id.tv_confirm);
    tvCancel = findViewById(R.id.tv_cancel);
    tvService = findViewById(R.id.tv_service);
    tvApp = findViewById(R.id.tv_app);
    tvAuthTip = findViewById(R.id.tv_auth_tip);
    ivApp = findViewById(R.id.iv_appIcon);
    ivService = findViewById(R.id.iv_serviceIcon);
    mTitleView = findViewById(R.id.view_title_service);
    View infoContent = findViewById(R.id.ll_info_content);
    infoContent.setVisibility(View.GONE);
    authView = findViewById(R.id.authView);
    authView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        OpenPlatformUtils.toAuthProtocol(OpenCorporateAuthorizationActivity.this, appId, unionId,
            serviceName);
      }
    });
    tvConfirm.setOnClickListener(this);
    tvCancel.setOnClickListener(this);
    mTitleView.setOnLeftClickListener(this);
    mTitleView.setUnderLineVisible(true);

    setStatusBarBgColor();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    outState.putString("serviceName", serviceName);
    outState.putString("url", url);
    outState.putString("appId", appId);
    outState.putString("unionId", unionId);
    outState.putString("token", token);
    outState.putString("certification", certification);
    super.onSaveInstanceState(outState);
  }

  @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    serviceName = savedInstanceState.getString("serviceName");
    url = savedInstanceState.getString("url");
    appId = savedInstanceState.getString("appId");
    unionId = savedInstanceState.getString("unionId");
    token = savedInstanceState.getString("token");
    certification = savedInstanceState.getString("certification");
  }

  private void initData() {
    appId = getIntent().getStringExtra(KEY_APPID);
    token = getIntent().getStringExtra(KEY_TOKEN);
    certification = getIntent().getStringExtra(KEY_CERTIFICATION);
    tvApp.setText(getApplicationName());
    Disposable disposable =
        OpenBiz.getServiceInfo(appId).subscribe(new Consumer<ServiceInfoResp>() {
          @Override
          public void accept(ServiceInfoResp infoResp) throws Exception {
            unionId = infoResp.unionId;
            setServiceInfo(infoResp);
          }
        }, new BaseRespThrowableObserver() {
          @Override
          public void onError(int code, String msg) {
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
          }
        });
    disposables.add(disposable);
  }

  @Override
  public void onClick(View v) {
    if (mContext != null
        && mContext instanceof PascWebviewActivity
        && ((PascWebviewActivity) mContext).mWebviewFragment != null
        && ((PascWebviewActivity) mContext).mWebviewFragment.mWebView != null) {
      url = ((PascWebviewActivity) mContext).mWebviewFragment.mWebView.getUrl();
    }
    if (v.getId() == R.id.tv_confirm) {
      getOpenId();
      PascOpenPlatform.getInstance()
          .getOpenPlatformProvider()
          .authClick(true, serviceName, url);
    } else if (v.getId() == R.id.tv_cancel) {
      PascOpenPlatform.getInstance()
          .getOpenPlatformProvider()
          .authClick(false, serviceName, url);
      PascHybrid.getInstance()
          .triggerCallbackFunction(ConstantBehaviorName.ENTERPRISE_USER_AUTH, -10002, getString(R.string.openplatform_user_cancel),
              null);
      this.finish();
    } else if (v.getId() == R.id.iv_title_left) {
      PascOpenPlatform.getInstance()
          .getOpenPlatformProvider()
          .authClick(false, serviceName, url);
      PascHybrid.getInstance()
          .triggerCallbackFunction(ConstantBehaviorName.ENTERPRISE_USER_AUTH, -10002, getString(R.string.openplatform_user_cancel),
              null);
      this.finish();
    }
  }

  void getOpenId() {
    tvConfirm.setClickable(false);
    Disposable disposable = OpenBiz.getCorporateOpenId(appId, token)
        .subscribe(new Consumer<OpenIdResp>() {
          @Override
          public void accept(OpenIdResp openIdResp) throws Exception {
            getRequestCode();
          }
        }, new BaseRespThrowableObserver() {
          @Override
          public void onError(int code, String msg) {
            tvConfirm.setClickable(true);
            //token状态101不合法，103已失效，104空
            if (code == 101 || code == 103 || code == 104) {
              OpenCorporateAuthorizationActivity.this.finish();
              PascOpenPlatform.getInstance()
                  .getOpenPlatformProvider()
                  .onCorporateOpenPlatformError(code, msg);
            } else {
              PascHybrid.getInstance().triggerCallbackFunction
                  (ConstantBehaviorName.ENTERPRISE_USER_AUTH, -1, msg, null);
            }
          }
        });
    disposables.add(disposable);
  }

  void getRequestCode() {
    Disposable disposable =
        OpenBiz.getCorporateRequestCode(appId, token).subscribe(new Consumer<RequestCodeResp>() {
          @Override
          public void accept(final RequestCodeResp requestCodeResp) throws Exception {
            if ("1".equals(certification)) {
              PascOpenPlatform.getInstance().getOpenPlatformProvider()
                  .getCertification(OpenCorporateAuthorizationActivity.this,
                      new CertificationCallback() {
                        @Override
                        public void certification(boolean isCertification) {
                          if (isCertification) {
                            ServiceAuthResult result = new ServiceAuthResult();
                            result.requestCode = requestCodeResp.requestCode;
                            result.openId = requestCodeResp.openId;
                            PascHybrid.getInstance()
                                .triggerCallbackFunction(ConstantBehaviorName.ENTERPRISE_USER_AUTH,
                                    result);
                            tvConfirm.setClickable(true);
                            OpenCorporateAuthorizationActivity.this.finish();
                          } else {
                            PascHybrid.getInstance()
                                .triggerCallbackFunction(ConstantBehaviorName.ENTERPRISE_USER_AUTH
                                    , UserAuthBehavior.CODE_USER_NOT_CERTIFICATION, "用户未实名认证",
                                    null);
                          }
                        }
                      });
            } else {
              ServiceAuthResult result = new ServiceAuthResult();
              result.requestCode = requestCodeResp.requestCode;
              result.openId = requestCodeResp.openId;
              PascHybrid.getInstance()
                  .triggerCallbackFunction(ConstantBehaviorName.ENTERPRISE_USER_AUTH, result);
              tvConfirm.setClickable(true);
              OpenCorporateAuthorizationActivity.this.finish();
            }
          }
        }, new BaseRespThrowableObserver() {
          @Override
          public void onError(int code, String msg) {
            tvConfirm.setClickable(true);
            //token状态101不合法，103已失效，104空
            if (code == 101 || code == 103 || code == 104) {
              OpenCorporateAuthorizationActivity.this.finish();
              PascOpenPlatform.getInstance()
                  .getOpenPlatformProvider()
                  .onCorporateOpenPlatformError(code, msg);
            } else {
              PascHybrid.getInstance().triggerCallbackFunction
                  (ConstantBehaviorName.ENTERPRISE_USER_AUTH, -1, msg, null);
            }
          }
        });
    disposables.add(disposable);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    PascOpenPlatform.getInstance().getOpenPlatformProvider().authClick(false, serviceName, url);
    PascHybrid.getInstance()
        .triggerCallbackFunction(ConstantBehaviorName.ENTERPRISE_USER_AUTH, -10002, getString(R.string.openplatform_user_cancel),
            null);
  }

  public void setStatusBarBgColor() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getWindow();
      //取消状态栏透明
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      //添加Flag把状态栏设为可绘制模式
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      //设置状态栏颜色
      window.setStatusBarColor(mContext.getResources().getColor(R.color.white_ffffff));
      //设置系统状态栏处于可见状态
      window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
      //让view不根据系统窗口来调整自己的布局
      ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
      View mChildView = mContentView.getChildAt(0);
      if (mChildView != null) {
        ViewCompat.setFitsSystemWindows(mChildView, false);
        ViewCompat.requestApplyInsets(mChildView);
      }
    }
  }

  private String getApplicationName() {
    PackageManager packageManager = null;
    ApplicationInfo applicationInfo = null;
    try {
      packageManager = mContext.getApplicationContext().getPackageManager();
      applicationInfo = packageManager.getApplicationInfo(mContext.getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      applicationInfo = null;
    }
    return (String) packageManager.getApplicationLabel(applicationInfo);
  }

  private void setServiceInfo(ServiceInfoResp infoResp) {
    if (0 != PascOpenPlatform.getInstance().getOpenPlatformProvider().getBackIconColor()) {
      mTitleView.getLeftIv()
          .setColorFilter(
              PascOpenPlatform.getInstance().getOpenPlatformProvider().getBackIconColor());
    }

    if (!TextUtils.isEmpty(infoResp.thirdPartyServicesLogo)
        && PascHybrid.getInstance().getHybridInitConfig() != null
        && PascHybrid.getInstance().getHybridInitConfig().getHybridInitCallback() != null) {
      PascHybrid.getInstance()
          .getHybridInitConfig()
          .getHybridInitCallback()
          .loadImage(ivService, infoResp.thirdPartyServicesLogo);
    }

    if (PascOpenPlatform.getInstance().getOpenPlatformProvider().getAppIcon() != 0) {
      ivApp.setImageResource(
          PascOpenPlatform.getInstance().getOpenPlatformProvider().getAppIcon());
    }
    serviceName = infoResp.thirdPartyServicesName;
    tvService.setText(serviceName);
    SpannableString ss =
        new SpannableString("该服务由 " + serviceName + " 提供，您同意 " + serviceName + " 获取以下权限");
    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 5,
        6 + serviceName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 5, 6 + serviceName.length(),
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    ss.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 13 + serviceName.length(),
        13 + serviceName.length() * 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 13 + serviceName.length(),
        13 + serviceName.length() * 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvAuthTip.setText(ss);
    tvConfirm.setBackgroundColor(mContext.getResources()
        .getColor(PascOpenPlatform.getInstance().getOpenPlatformProvider().getStyleColor()));
    //tvConfirm.getBackground()
    //        .setColorFilter(mContext.getResources()
    //                .getColor(PascOpenPlatform.getInstance()
    //                        .getOpenPlatformProvider()
    //                        .getStyleColor()), PorterDuff.Mode.SRC);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    disposables.dispose();
  }
}
