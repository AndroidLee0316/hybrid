package com.pasc.lib.openplatform;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.base.widget.LoadingDialog;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.hybrid.widget.WebCommonTitleView;
import com.pasc.lib.net.resp.BaseRespThrowableObserver;
import com.pasc.lib.openplatform.adapter.OpenAuthSelectAdapter;
import com.pasc.lib.openplatform.address.AddressResp;
import com.pasc.lib.openplatform.bean.UserAuthContentBean;
import com.pasc.lib.openplatform.forthird.OpenPlatformForThird;
import com.pasc.lib.openplatform.network.OpenBiz;
import com.pasc.lib.openplatform.resp.AuthSelectRequestCodeResp;
import com.pasc.lib.openplatform.resp.ServiceInfoResp;
import com.pasc.lib.openplatform.util.OpenPlatformUtils;
import com.pasc.libopenplatform.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * create by wujianning385 on 2018/11/21.
 */
public class OpenAuthSelectActivity extends AppCompatActivity implements View.OnClickListener {

  private static String KEY_APPID = "key_appId";
  private static String KEY_TOKEN = "key_token";
  private static String KEY_UNION_ID = "key_unionId";
  private static String KEY_AUTH_DATAS = "key_need_auth_datas";
  private static String KEY_AUTH_DATA_TYPE = "authDataType";
  /**
   * 显示的服务数据，包括请求授权方数据、授权方数据，授权内容
   */
  private static String KEY_SHOW_SERVICE_INFO = "key_show_service_info";
  /**
   * 显示的内容，包括标题 + 列表数据
   */
  private static String KEY_SHOW_CONTENT = "key_show_content";

  private Context mContext;
  private WebCommonTitleView mTitleView;
  private TextView tvApp, tvService, tvAuthTip, tvOpenInfo;
  private TextView tvConfirm, tvCancel;
  private ImageView ivApp, ivService;
  //授权标题
  private TextView mAuthContentTitle;
  //授权列表
  private RecyclerView mAuthContentRV;
  private View authView;

  private String serviceName;
  private String appId;
  private String token;
  private ArrayList<String> mNeedAuthDatas;
  private String authDataType;
  /**
   * 显示内容
   */
  private UserAuthContentBean showBean;
  /**
   * 服务数据
   */
  private ServiceInfoResp serviceInfoResp;
  public CompositeDisposable disposables = new CompositeDisposable();
  private List<TextView> tvInfos = new ArrayList<>();
  private String url = "";

  private LoadingDialog loadingDialog;

  /**
   * 选中的获取授权码的原数据
   */
  private AddressResp selectSourceData;

  public static void start(Context context, String appId, String token,
                           ArrayList<String> needAuthDatas, String authDataType,
                           ServiceInfoResp serviceInfoResp, UserAuthContentBean showBean) {
    Intent intent = new Intent(context, OpenAuthSelectActivity.class);
    intent.putExtra(KEY_APPID, appId);
    intent.putExtra(KEY_TOKEN, token);
    intent.putStringArrayListExtra(KEY_AUTH_DATAS, needAuthDatas);
    intent.putExtra(KEY_AUTH_DATA_TYPE, authDataType);
    intent.putExtra(KEY_SHOW_SERVICE_INFO,serviceInfoResp);
    intent.putExtra(KEY_SHOW_CONTENT, showBean);
    context.startActivity(intent);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = OpenAuthSelectActivity.this;
    setContentView(R.layout.openplatform_activity_auth_select);
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
    tvConfirm.setOnClickListener(this);
    tvCancel.setOnClickListener(this);
    mTitleView.setOnLeftClickListener(this);
    mTitleView.setUnderLineVisible(true);
    mAuthContentTitle = findViewById(R.id.tv_auth_content_title);
    mAuthContentRV = findViewById(R.id.tv_auth_content_rv);
    authView = findViewById(R.id.authView);
    authView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        OpenPlatformUtils.toAuthProtocol(OpenAuthSelectActivity.this, appId, serviceInfoResp.unionId,
                serviceName);
      }
    });
    mTitleView.setTitleTypeface(Typeface.DEFAULT_BOLD);
    setStatusBarBgColor();


    if ("invisiable".equals(getString(R.string.paschybrid_config_auth_agreement_visibility))){
      authView.setVisibility(View.INVISIBLE);
    }else if ("gone".equals(getString(R.string.paschybrid_config_auth_agreement_visibility))){
      authView.setVisibility(View.GONE);
    }

  }


  private void initData() {
    appId = getIntent().getStringExtra(KEY_APPID);
    token = getIntent().getStringExtra(KEY_TOKEN);
    mNeedAuthDatas = getIntent().getStringArrayListExtra(KEY_AUTH_DATAS);
    authDataType = getIntent().getStringExtra(KEY_AUTH_DATA_TYPE);
    tvApp.setText(getApplicationName());
    showBean = (UserAuthContentBean) getIntent().getSerializableExtra(KEY_SHOW_CONTENT);
    serviceInfoResp = (ServiceInfoResp) getIntent().getSerializableExtra(KEY_SHOW_SERVICE_INFO);
    setServiceInfo(serviceInfoResp);
    setContentViewData();
  }

  @Override public void onClick(View v) {
    if (mContext != null
        && mContext instanceof PascWebviewActivity
        && ((PascWebviewActivity) mContext).mWebviewFragment != null
        && ((PascWebviewActivity) mContext).mWebviewFragment.mWebView != null) {
      url = ((PascWebviewActivity) mContext).mWebviewFragment.mWebView.getUrl();
    }
    if (v.getId() == R.id.tv_confirm) {
      getRequestCode();
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

  void getRequestCode() {
    tvConfirm.setClickable(false);
    showLoading();
    Disposable disposable = OpenBiz.getAuthSelectResquestCode(appId, token, authDataType, selectSourceData)
        .subscribe(new Consumer<AuthSelectRequestCodeResp>() {
          @Override public void accept(final AuthSelectRequestCodeResp requestCodeResp) throws Exception {

            dismissLoading();
            requestCodeResp.requestCode = requestCodeResp.everyTimeRequestCode;
            PascHybrid.getInstance()
                    .triggerCallbackFunction(ConstantBehaviorName.USER_AUTH, requestCodeResp);
            if (OpenPlatformForThird.getInstance().requestIdCallback != null) {
              OpenPlatformForThird.getInstance().requestIdCallback.getRequestId(
                      requestCodeResp.everyTimeRequestCode, requestCodeResp.expiresIn);
            }
            tvConfirm.setClickable(true);
            OpenAuthSelectActivity.this.finish();

          }
        }, new BaseRespThrowableObserver() {
          @Override public void onError(int code, String msg) {
            dismissLoading();
            tvConfirm.setClickable(true);
            //token状态101不合法，103已失效，108错误，109session失效
            //        //token状态101不合法，103已失效，108错误，109session失效
            if (101 == code || 103 == code || 108 == code || 109 == code) {
              OpenAuthSelectActivity.this.finish();
              PascOpenPlatform.getInstance().getOpenPlatformProvider().onOpenPlatformError(Integer.valueOf(code), msg);
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
    tvOpenInfo.setText(serviceInfoResp.remark);
  }

  /**
   * 刷新内容列表
   */
  private void setContentViewData(){
    mAuthContentTitle.setText(showBean.title);
    //查找默认的selectID
    if (showBean.itemList != null){
      for ( UserAuthContentBean.ItemBean itemBean : showBean.itemList) {
        if (itemBean.select){
          selectSourceData = itemBean.sourceData;
        }
      }
    }
    OpenAuthSelectAdapter adapter = new OpenAuthSelectAdapter(showBean.itemList);
    adapter.setClickCallBack(new OpenAuthSelectAdapter.OnClickCallBack() {
      @Override
      public void onItemSelect(UserAuthContentBean.ItemBean itemBean) {
        selectSourceData = itemBean.sourceData;
      }
    });
    mAuthContentRV.setAdapter(adapter);
    mAuthContentRV.setLayoutManager(new LinearLayoutManager(mContext));
    mAuthContentRV.setNestedScrollingEnabled(false);
    mAuthContentRV.setHasFixedSize(true);
    mAuthContentRV.setFocusable(false);
  }


  @Override protected void onDestroy() {
    super.onDestroy();
    disposables.dispose();
  }



  private void showLoading(){
    loadingDialog=new LoadingDialog(this);
    loadingDialog.setHasContent(false);
    loadingDialog.show();
  }

  private void dismissLoading(){
    if (loadingDialog != null && loadingDialog.isShowing()){
      loadingDialog.dismiss();
    }
    loadingDialog = null;

  }


}
