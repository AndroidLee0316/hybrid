package com.pasc.lib.openplatform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.android.arouter.launcher.ARouter;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.hybrid.widget.WebCommonTitleView;
import com.pasc.lib.net.resp.BaseRespThrowableObserver;
import com.pasc.lib.openplatform.forthird.OpenPlatformForThird;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * create by wujianning385 on 2018/11/21.
 */
public class OpenAuthorizationActivity extends AppCompatActivity implements View.OnClickListener {

  private static String KEY_APPID = "key_appId";
  private static String KEY_TOKEN = "key_token";
  private static String KEY_CERTIFICATION = "key_certification";
  private static String KEY_AUTH_DATAS = "key_need_auth_datas";
  private static String KEY_LIST_PARAMS = "key_list_params";

  private Context mContext;
  private WebCommonTitleView mTitleView;
  private TextView tvApp, tvService, tvAuthTip;
  private TextView tvOpenInfo;
  private TextView tvConfirm, tvCancel;
  private LinearLayout llInfoContent;
  private ImageView ivApp, ivService;
  private String serviceName;
  private String appId;
  private String token;
  private String certification;
  private ArrayList<String> mNeedAuthDatas;
  private ArrayList<String> mListParams;
  public CompositeDisposable disposables = new CompositeDisposable();
  private List<TextView> tvInfos = new ArrayList<>();
  private String url = "";
  private View authView;
  private String unionId;

  public static void start(Context context, String appId, String token, String certification) {
    start(context, appId, token, certification, new ArrayList<String>(), new ArrayList<String>());
  }

  public static void start(Context context, String appId, String token, String certification,
      ArrayList<String> needAuthDatas, ArrayList<String> listParams) {
    Intent intent = new Intent(context, OpenAuthorizationActivity.class);
    intent.putExtra(KEY_APPID, appId);
    intent.putExtra(KEY_TOKEN, token);
    intent.putExtra(KEY_CERTIFICATION, certification);
    intent.putStringArrayListExtra(KEY_AUTH_DATAS, needAuthDatas);
    intent.putStringArrayListExtra(KEY_LIST_PARAMS, listParams);
    context.startActivity(intent);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = OpenAuthorizationActivity.this;
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
    tvOpenInfo = findViewById(R.id.tv_open_info);
    llInfoContent = findViewById(R.id.ll_info_content);
    authView = findViewById(R.id.authView);
    authView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        OpenPlatformUtils.toAuthProtocol(OpenAuthorizationActivity.this, appId, unionId,
            serviceName);
      }
    });
    tvConfirm.setOnClickListener(this);
    tvCancel.setOnClickListener(this);
    mTitleView.setOnLeftClickListener(this);
    mTitleView.setUnderLineVisible(true);
    setStatusBarBgColor();

    if ("invisiable".equals(getString(R.string.paschybrid_config_auth_agreement_visibility))){
      authView.setVisibility(View.INVISIBLE);
    }else if ("gone".equals(getString(R.string.paschybrid_config_auth_agreement_visibility))){
      authView.setVisibility(View.GONE);
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    outState.putString("url", url);
    outState.putString("serviceName", serviceName);
    outState.putString("appId", appId);
    outState.putString("unionId", unionId);
    outState.putString("token", token);
    outState.putString("certification", certification);
    outState.putStringArrayList("mNeedAuthDatas", mNeedAuthDatas);
    outState.putStringArrayList("mListParams", mListParams);
    super.onSaveInstanceState(outState);
  }

  @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    url = savedInstanceState.getString("url");
    serviceName = savedInstanceState.getString("serviceName");
    appId = savedInstanceState.getString("appId");
    unionId = savedInstanceState.getString("unionId");
    token = savedInstanceState.getString("token");
    certification = savedInstanceState.getString("certification");
    mNeedAuthDatas = savedInstanceState.getStringArrayList("mNeedAuthDatas");
    mListParams = savedInstanceState.getStringArrayList("mListParams");
  }

  private void initData() {
    appId = getIntent().getStringExtra(KEY_APPID);
    token = getIntent().getStringExtra(KEY_TOKEN);
    certification = getIntent().getStringExtra(KEY_CERTIFICATION);
    mNeedAuthDatas = getIntent().getStringArrayListExtra(KEY_AUTH_DATAS);
    mListParams = getIntent().getStringArrayListExtra(KEY_LIST_PARAMS);
    tvApp.setText(getApplicationName());
    Disposable disposable =
        OpenBiz.getServiceInfo(appId).subscribe(new Consumer<ServiceInfoResp>() {
          @Override public void accept(ServiceInfoResp infoResp) throws Exception {
            unionId = infoResp.unionId;
            setServiceInfo(infoResp);
          }
        }, new BaseRespThrowableObserver() {
          @Override public void onError(int code, String msg) {
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
          }
        });
    disposables.add(disposable);
  }

  @Override public void onClick(View v) {
    if (mContext != null
        && mContext instanceof PascWebviewActivity
        && ((PascWebviewActivity) mContext).mWebviewFragment != null
        && ((PascWebviewActivity) mContext).mWebviewFragment.mWebView != null) {
      url = ((PascWebviewActivity) mContext).mWebviewFragment.mWebView.getUrl();
    }
    if (v.getId() == R.id.tv_confirm) {
      getOpenId();
      PascOpenPlatform.getInstance().getOpenPlatformProvider().authClick(true, serviceName, url);
    } else if (v.getId() == R.id.tv_cancel) {
      PascOpenPlatform.getInstance().getOpenPlatformProvider().authClick(false, serviceName, url);
      PascHybrid.getInstance()
          .triggerCallbackFunction(ConstantBehaviorName.USER_AUTH, -10002, getString(R.string.openplatform_user_cancel), null);
      if (OpenPlatformForThird.getInstance().requestIdCallback != null) {
        OpenPlatformForThird.getInstance().requestIdCallback.authfail(-10002, getString(R.string.openplatform_user_cancel));
      }
      this.finish();
    } else if (v.getId() == R.id.iv_title_left) {
      PascOpenPlatform.getInstance().getOpenPlatformProvider().authClick(false, serviceName, url);
      PascHybrid.getInstance()
          .triggerCallbackFunction(ConstantBehaviorName.USER_AUTH, -10002, getString(R.string.openplatform_user_cancel), null);
      if (OpenPlatformForThird.getInstance().requestIdCallback != null) {
        OpenPlatformForThird.getInstance().requestIdCallback.authfail(-10002, getString(R.string.openplatform_user_cancel));
      }
      this.finish();
    }
  }

  void getOpenId() {
    tvConfirm.setClickable(false);
    Disposable disposable = OpenBiz.getOpenId(appId, token).subscribe(new Consumer<OpenIdResp>() {
      @Override public void accept(OpenIdResp openIdResp) throws Exception {
        getRequestCode();
      }
    }, new BaseRespThrowableObserver() {
      @Override public void onError(int code, String msg) {
        tvConfirm.setClickable(true);
        //token状态101不合法，103已失效，108错误，109session失效
        if (code == 101 || code == 103 || code == 108 || code == 109) {
          OpenAuthorizationActivity.this.finish();
          PascOpenPlatform.getInstance().getOpenPlatformProvider().onOpenPlatformError(code, msg);
        } else {
          PascHybrid.getInstance()
              .triggerCallbackFunction(ConstantBehaviorName.USER_AUTH, -1, msg, null);
          if (OpenPlatformForThird.getInstance().requestIdCallback != null) {
            OpenPlatformForThird.getInstance().requestIdCallback.authfail(-1, msg);
          }
        }
      }
    });
    disposables.add(disposable);
  }

  void getRequestCode() {
    List<String> dataTypes = new ArrayList<>();
    for (String code : mListParams) {
      if (!TextUtils.isEmpty(code)) {
        dataTypes.add(code);
      }
    }
    Disposable disposable = OpenBiz.getResquestCode(appId, token, dataTypes)
        .subscribe(new Consumer<RequestCodeResp>() {
          @Override public void accept(final RequestCodeResp requestCodeResp) throws Exception {
            if ("1".equals(certification)) {
              PascOpenPlatform.getInstance()
                  .getOpenPlatformProvider()
                  .getCertification(OpenAuthorizationActivity.this, new CertificationCallback() {
                    @Override public void certification(boolean isCertification) {
                      if (isCertification) {
                        ServiceAuthResult result = new ServiceAuthResult();
                        result.requestCode = requestCodeResp.requestCode;
                        result.openId = requestCodeResp.openId;
                        PascHybrid.getInstance()
                            .triggerCallbackFunction(ConstantBehaviorName.USER_AUTH, result);
                        if (OpenPlatformForThird.getInstance().requestIdCallback != null) {
                          OpenPlatformForThird.getInstance().requestIdCallback.getRequestId(
                              requestCodeResp.requestCode, requestCodeResp.expiresIn);
                        }
                        tvConfirm.setClickable(true);
                        OpenAuthorizationActivity.this.finish();
                      } else {
                        PascHybrid.getInstance()
                            .triggerCallbackFunction(ConstantBehaviorName.USER_AUTH,
                                UserAuthBehavior.CODE_USER_NOT_CERTIFICATION, "用户未实名认证", null);
                        if (OpenPlatformForThird.getInstance().requestIdCallback != null) {
                          OpenPlatformForThird.getInstance().requestIdCallback.authfail(
                              UserAuthBehavior.CODE_USER_NOT_CERTIFICATION, "用户未实名认证");
                        }
                      }
                    }
                  });
            } else {
              ServiceAuthResult result = new ServiceAuthResult();
              result.requestCode = requestCodeResp.requestCode;
              result.openId = requestCodeResp.openId;
              PascHybrid.getInstance()
                  .triggerCallbackFunction(ConstantBehaviorName.USER_AUTH, result);
              if (OpenPlatformForThird.getInstance().requestIdCallback != null) {
                OpenPlatformForThird.getInstance().requestIdCallback.getRequestId(
                    requestCodeResp.requestCode, requestCodeResp.expiresIn);
              }
              tvConfirm.setClickable(true);
              OpenAuthorizationActivity.this.finish();
            }
          }
        }, new BaseRespThrowableObserver() {
          @Override public void onError(int code, String msg) {
            tvConfirm.setClickable(true);
            //token状态101不合法，103已失效，108错误，109session失效
            if (code == 101 || code == 103 || code == 108 || code == 109) {
              PascOpenPlatform.getInstance()
                  .getOpenPlatformProvider()
                  .onOpenPlatformError(code, msg);
            } else {
              PascHybrid.getInstance()
                  .triggerCallbackFunction(ConstantBehaviorName.USER_AUTH, -1, msg, null);
              if (OpenPlatformForThird.getInstance().requestIdCallback != null) {
                OpenPlatformForThird.getInstance().requestIdCallback.authfail(-1, msg);
              }
            }
          }
        });
    disposables.add(disposable);
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    PascOpenPlatform.getInstance().getOpenPlatformProvider().authClick(false, serviceName, url);
    PascHybrid.getInstance()
        .triggerCallbackFunction(ConstantBehaviorName.USER_AUTH, -10002, getString(R.string.openplatform_user_cancel), null);
    if (OpenPlatformForThird.getInstance().requestIdCallback != null) {
      OpenPlatformForThird.getInstance().requestIdCallback.authfail(-10002, getString(R.string.openplatform_user_cancel));
    }
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
    int backColor = PascOpenPlatform.getInstance().getOpenPlatformProvider().getBackIconColor();
    if (0 != backColor) {
      mTitleView.getLeftIv().setColorFilter(mContext.getResources().getColor(backColor));
    }
    int btnColor = PascOpenPlatform.getInstance().getOpenPlatformProvider().getStyleColor();

    if (0 != btnColor) {
      Drawable drawable = mContext.getResources().getDrawable(R.drawable.paschybrid_bg_radius_3);
      drawable.setColorFilter(mContext.getResources().getColor(btnColor), PorterDuff.Mode.SRC);
      tvConfirm.setBackground(drawable);
    } else {
      tvConfirm.setBackgroundResource(R.drawable.paschybrid_bg_radius_3);
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
      ivApp.setImageResource(PascOpenPlatform.getInstance().getOpenPlatformProvider().getAppIcon());
    }
    serviceName = infoResp.thirdPartyServicesName;
    if (!TextUtils.isEmpty(serviceName)) {
      tvService.setText(serviceName);
      SpannableString ss =
          new SpannableString("该服务由 " + serviceName + " 提供，您同意 " + serviceName + " 获取以下权限");
      ss.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 5, 6 + serviceName.length(),
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 5, 6 + serviceName.length(),
          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      ss.setSpan(new ForegroundColorSpan(Color.parseColor("#333333")), 13 + serviceName.length(),
          13 + serviceName.length() * 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 13 + serviceName.length(),
          13 + serviceName.length() * 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
      tvAuthTip.setText(ss);
    }
    if (infoResp.applyUserDataTypeInfo != null && infoResp.applyUserDataTypeInfo.size() > 0) {
      tvOpenInfo.setVisibility(View.GONE);
      Map<String, ServiceInfoResp.UserDataTypeInfo> map = new HashMap<>();
      for (int i = 0; i < infoResp.applyUserDataTypeInfo.size(); i++) {
        ServiceInfoResp.UserDataTypeInfo info = infoResp.applyUserDataTypeInfo.get(i);
        map.put(info.userDataTypeCode, info);
      }
      if (mNeedAuthDatas.size() > 0) {
        for (int i = 0; i < mNeedAuthDatas.size(); i++) {
          String code = mNeedAuthDatas.get(i);
          ServiceInfoResp.UserDataTypeInfo info = map.get(code);
          String infoStr = info.userDataTypeName + "(" + info.relateNames + ")";
          llInfoContent.addView(createTextView(infoStr));
        }
      } else {
        for (int i = 0; i < infoResp.applyUserDataTypeInfo.size(); i++) {
          ServiceInfoResp.UserDataTypeInfo info = infoResp.applyUserDataTypeInfo.get(i);
          String infoStr = info.userDataTypeName + "(" + info.relateNames + ")";

          //用户第一次授权，什么都不传的话，并且是全部返回，就显示全部信息
          if ("1".equals(infoResp.userInfoGetType)) {
            this.llInfoContent.addView(this.createTextView(infoStr));
          } else if ("1".equals(info.isDefault)) {
            llInfoContent.addView(createTextView(infoStr));
          }
        }
      }
      llInfoContent.requestLayout();
    }

    //tvConfirm.getBackground()
    //        .setColorFilter(mContext.getResources()
    //                .getColor(PascOpenPlatform.getInstance()
    //                        .getOpenPlatformProvider()
    //                        .getStyleColor()), PorterDuff.Mode.SRC);
  }

  private TextView createTextView(String infoStr) {
    TextView textView = new TextView(mContext);
    LinearLayout.LayoutParams layoutParams =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
    layoutParams.gravity = Gravity.CENTER_VERTICAL;
    textView.setLayoutParams(layoutParams);
    textView.setPadding(40, 0, 40, 20);
    textView.setGravity(Gravity.CENTER_VERTICAL);
    Drawable drawable = getResources().getDrawable(R.drawable.paschybrid_ic_dot_gray);
    /// 这一步必须要做,否则不会显示.
    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
    textView.setCompoundDrawables(drawable, null, null, null);
    textView.setCompoundDrawablePadding(20);
    textView.setTextColor(mContext.getResources().getColor(R.color.gray_999999));
    textView.setText(infoStr);
    return textView;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    disposables.dispose();
  }
}
