package com.pasc.lib.openplatform;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.pasc.lib.base.activity.BaseActivity;
import com.pasc.lib.base.event.BaseEvent;
import com.pasc.lib.base.util.StatusBarUtils;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.hybrid.widget.WebCommonTitleView;
import com.pasc.lib.log.PascLog;
import com.pasc.lib.net.resp.BaseRespThrowableObserver;
import com.pasc.lib.openplatform.adapter.DataSecretaryAdapter;
import com.pasc.lib.openplatform.network.OpenBiz;
import com.pasc.lib.openplatform.resp.DataSecretaryList;
import com.pasc.lib.openplatform.resp.DataSecretaryResp;
import com.pasc.libopenplatform.R;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DataSecretaryListActivity extends BaseActivity {

    public static final String EVENT_KEY_AUTH_CANCEL = "event_key_auth_cancel";

    private Context mContext;

    private WebCommonTitleView mTitleView;
    private RecyclerView mRvList;
    private DataSecretaryAdapter mAdapter;
    private String mToken;


    public static void start(Context context){
        Intent intent = new Intent(context, DataSecretaryListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int layoutResId() {
        return R.layout.openplatform_activity_data_secretary;
    }

    @Override
    protected void onInit(@Nullable Bundle bundle) {
        mContext = DataSecretaryListActivity.this;
        mTitleView = findViewById(R.id.view_title);
        mRvList = findViewById(R.id.recycler_view);
        mTitleView.setTitleText("我的授权");
        mTitleView.setUnderLineVisible(true);
        mTitleView.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mTitleView.setBackDrawableLeft(R.drawable.paschybrid_ic_back);
        mRvList.setLayoutManager(new LinearLayoutManager(this));
        EventBus.getDefault().register(this);
        StatusBarUtils.setFlymeStatusBarLightMode(this,true);
        init();
    }


    private void init() {
        PascOpenPlatform.getInstance().getOpenPlatformProvider().getUserToken(new IBizCallback() {
            @Override
            public void onLoginSuccess(String userToken) {
                if (TextUtils.isEmpty(userToken)) {
                    ToastUtils.toastMsg("请先登录");
                    return;
                }
                showLoading("",true);
                mToken = userToken;
                getData(userToken);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void getData(String token) {
        OpenBiz.getDataSecretaryList(token).compose(this.<DataSecretaryList>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<DataSecretaryList>() {
                    @Override
                    public void accept(DataSecretaryList resps) throws Exception {
                        dismissLoading();
                        mAdapter = new DataSecretaryAdapter(resps.dataSecretaryVO);
                        mRvList.setAdapter(mAdapter);
                        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                DataSecretaryResp data = (DataSecretaryResp) adapter.getItem(position);
                                if (data == null) {
                                    return;
                                }
                                DataSecretaryDetailActivity.start(mContext, data.appId);
                            }
                        });
                        if (resps.dataSecretaryVO==null||resps.dataSecretaryVO.size()==0){
                            mAdapter.setEmptyView(R.layout.openplatform_view_data_secretary_empty,mRvList);
                        }
                    }
                }, new BaseRespThrowableObserver() {
                    @Override
                    public void onError(int code, String s) {
                        dismissLoading();
                        if (mAdapter==null){
                            mAdapter  = new DataSecretaryAdapter(new ArrayList<DataSecretaryResp>());
                            mRvList.setAdapter(mAdapter);
                        }
                        mAdapter.setEmptyView(R.layout.openplatform_view_data_secretary_empty,mRvList);
                        if (code == 101 || code == 103 || code == 108 || code == 109) {
                            PascOpenPlatform.getInstance()
                                .getOpenPlatformProvider()
                                .onOpenPlatformError(code, s);
                            //ToastUtils.toastMsg(s);
                        }
                        PascLog.e(s);
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgEvent(BaseEvent event) {
        if (event != null && EVENT_KEY_AUTH_CANCEL.equals(event.getTag())) {
            getData(mToken);
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
