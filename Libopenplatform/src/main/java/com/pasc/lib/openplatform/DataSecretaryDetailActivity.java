package com.pasc.lib.openplatform;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.pasc.lib.base.activity.BaseActivity;
import com.pasc.lib.base.event.BaseEvent;
import com.pasc.lib.base.util.StatusBarUtils;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.widget.CommonDialog;
import com.pasc.lib.hybrid.widget.WebCommonTitleView;
import com.pasc.lib.log.PascLog;
import com.pasc.lib.net.resp.BaseRespThrowableObserver;
import com.pasc.lib.net.resp.VoidObject;
import com.pasc.lib.openplatform.adapter.DataSecretaryInfoAdapter;
import com.pasc.lib.openplatform.network.OpenBiz;
import com.pasc.lib.openplatform.resp.DataSecretaryDetailResp;
import com.pasc.libopenplatform.R;
import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.functions.Consumer;
import org.greenrobot.eventbus.EventBus;

public class DataSecretaryDetailActivity extends BaseActivity {

    public static final String KEY_APPID = "key_app_id";

    private Context mContext;

    private WebCommonTitleView mTitleView;
    private RecyclerView mRvList;
    private TextView tvInfoName;
    private TextView tvInfoContent;
    private ImageView ivInfoIcon;
    private Button btnAuthCancel;

    private String mAppId;
    private String mToken;
    private DataSecretaryInfoAdapter mAdapter;

    public static void start(Context context, String appId) {
        Intent intent = new Intent(context, DataSecretaryDetailActivity.class);
        intent.putExtra(KEY_APPID, appId);
        context.startActivity(intent);
    }

    @Override
    protected int layoutResId() {
        return R.layout.openplatform_activity_data_secretary_detail;
    }

    @Override
    protected void onInit(@Nullable Bundle bundle) {
        mContext = DataSecretaryDetailActivity.this;
        mTitleView = findViewById(R.id.view_title);
        mRvList = findViewById(R.id.rv_list);
        tvInfoName = findViewById(R.id.tv_data_name);
        tvInfoContent = findViewById(R.id.tv_data_auth_info);
        ivInfoIcon = findViewById(R.id.iv_data_icon);
        btnAuthCancel = findViewById(R.id.btn_auth_cancel);
        int btnColor = PascOpenPlatform.getInstance().getOpenPlatformProvider().getStyleColor();
        if (0 != btnColor) {
            //btnAuthCancel.setBackgroundColor(mContext.getResources().getColor(btnColor));
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.paschybrid_bg_radius_3);
            drawable.setColorFilter(mContext.getResources().getColor(btnColor), PorterDuff.Mode.SRC);
            btnAuthCancel.setBackground(drawable);
        } else {
            btnAuthCancel.setBackgroundResource(R.drawable.paschybrid_bg_radius_3);
        }
        mTitleView.setTitleText("授权详情");
        mTitleView.setUnderLineVisible(true);
        mTitleView.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnAuthCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAuthCancelDialog();
            }
        });
        mTitleView.setBackDrawableLeft(R.drawable.paschybrid_ic_back);
        mRvList.setLayoutManager(new LinearLayoutManager(this));
        StatusBarUtils.setFlymeStatusBarLightMode(this,true);
        init();
    }

    private void init() {
        mAppId = getIntent().getStringExtra(KEY_APPID);
        PascOpenPlatform.getInstance().getOpenPlatformProvider().getUserToken(new IBizCallback() {
            @Override
            public void onLoginSuccess(String userToken) {
                if (TextUtils.isEmpty(userToken)) {
                    ToastUtils.toastMsg("请先登录");
                    return;
                }
                showLoading("",true);
                getData(userToken);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void getData(String token) {
        mToken = token;
        OpenBiz.getDataSecretaryDetail(mAppId, token)
                .compose(this.<DataSecretaryDetailResp>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<DataSecretaryDetailResp>() {
                    @Override
                    public void accept(DataSecretaryDetailResp dataSecretaryDetailResp) throws Exception {
                        dismissLoading();
                        setData(dataSecretaryDetailResp);
                    }
                }, new BaseRespThrowableObserver() {
                    @Override
                    public void onError(int code, String msg) {
                        dismissLoading();
                        if (code == 101 || code == 103 || code == 108 || code == 109) {
                            PascOpenPlatform.getInstance()
                                .getOpenPlatformProvider()
                                .onOpenPlatformError(code, msg);
                        }
                        PascLog.e(msg);
                    }
                });
    }

    /**
     * 设置页面数据
     * @param data 请求响应数据
     */
    private void setData(DataSecretaryDetailResp data) {
        if (data.dataSecretaryVO != null) {
            tvInfoName.setText(data.dataSecretaryVO.thirdPartyServicesName);
            tvInfoContent.setText(data.dataSecretaryVO.thirdPartyServicesNameDetail);
            PascHybrid.getInstance()
                    .getHybridInitConfig()
                    .getHybridInitCallback()
                    .loadImage(ivInfoIcon, data.dataSecretaryVO.logo);
        }
        mAdapter = new DataSecretaryInfoAdapter(data.dataSecretaryDetailVOs);
        mRvList.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                DataSecretaryDetailResp.DataDetail dataDetail = (DataSecretaryDetailResp.DataDetail) adapter.getItem(position);
                mAdapter.expandInfo(position,dataDetail);
            }
        });

    }

    /**
     * 取消全部授权
     */
    @SuppressLint("CheckResult")
    private void authCancel(){
        showLoading("",true);
        OpenBiz.dataSecretaryAuthCancel(mAppId,mToken)
                .compose(this.<VoidObject>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<VoidObject>() {
            @Override
            public void accept(VoidObject voidObject) throws Exception {
                dismissLoading();
                EventBus.getDefault().post(new BaseEvent(DataSecretaryListActivity.EVENT_KEY_AUTH_CANCEL));
                ToastUtils.toastMsg("取消授权成功");
                DataSecretaryDetailActivity.this.finish();
            }
        }, new BaseRespThrowableObserver() {
            @Override
            public void onError(int code, String s) {
                dismissLoading();
                if (code == 101 || code == 103 || code == 108 || code == 109) {
                    PascOpenPlatform.getInstance()
                        .getOpenPlatformProvider()
                        .onOpenPlatformError(code, s);
                }
                ToastUtils.toastMsg("取消授权失败");
            }
        });
    }

    /**
     * 显示取消授权弹窗
     */
    private void showAuthCancelDialog(){
        String msg = "取消授权后，您可能无法正常使用相关服务，确认取消授权？";
        new CommonDialog(mContext).setContent(msg).setButton1("确认取消").setButton2("暂不取消",CommonDialog.Blue_22c8d8)
                .setOnButtonClickListener(new CommonDialog.OnButtonClickListener() {
                    @Override
                    public void button1Click() {
                        authCancel();
                    }

                    @Override
                    public void button2Click() {
                    }
                }).show();
    }
}
