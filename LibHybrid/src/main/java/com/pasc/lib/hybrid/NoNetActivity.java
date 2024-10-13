package com.pasc.lib.hybrid;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pasc.lib.hybrid.nativeability.WebStrategy;
import com.pasc.lib.hybrid.util.NetWorkUtils;
import com.pasc.lib.hybrid.util.StatusBarUtils;
import com.pasc.lib.hybrid.widget.WebCommonTitleView;
import com.pasc.lib.smtbrowser.util.BrowserUtils;

/**
 * Created by ex-guozhe001 on 2019/3/6.
 */
public class NoNetActivity extends FragmentActivity {

    String type;
    String url;
    int mStrategy;

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setTransparentForWindow(this); // 统一都是沉浸式，非沉浸式添加间隔即可。因为两种状态需要互相切换
        setContentView(R.layout.activity_networkerror);
        BrowserUtils.setStatusBarTxColor(this, true);
        WebCommonTitleView mToolbar = findViewById(R.id.common_title);
        TextView tvRetryLoad  = findViewById(R.id.tv_retryload);
        TextView tvEmptyTtips = findViewById(R.id.tv_empty_tips);
        ImageView ivEmptyIcon = findViewById(R.id.iv_empty_icon);

        int statusBarHeight = StatusBarUtils.getStatusBarHeight(this);
        mToolbar.setPadding(0,statusBarHeight, 0,0);

        if (mToolbar != null) {
            mToolbar.getLeftIv().setColorFilter(Color.parseColor("#333333"));
        }

        mToolbar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        url = intent.getStringExtra("url");
        mStrategy = intent.getIntExtra("strategy",0);

        if(PascHybrid.getInstance().mHybridInitConfig != null && PascHybrid.getInstance().mHybridInitConfig.getNoNetPagek() !=  null){
            CharSequence retryload = PascHybrid.getInstance().mHybridInitConfig.getNoNetPagek().RetryLoadText();
            tvRetryLoad.setText(retryload);
        }
        if (PascHybrid.getInstance().mHybridInitConfig != null && PascHybrid.getInstance().mHybridInitConfig.getNoNetPagek() != null){
            CharSequence emptytips = PascHybrid.getInstance().mHybridInitConfig.getNoNetPagek().EmptyTtipsText();
            tvEmptyTtips.setText(emptytips);
        }
        if (PascHybrid.getInstance().mHybridInitConfig != null && PascHybrid.getInstance().mHybridInitConfig.getNoNetPagek() != null){
            int emptyicon = PascHybrid.getInstance().mHybridInitConfig.getNoNetPagek().EmptyIcon();
            ivEmptyIcon.setImageResource(emptyicon);
        }

        tvRetryLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetWorkUtils.isNetworkConnected(NoNetActivity.this)){
                    if (type.equals("1")){
                        PascHybrid.getInstance().start(NoNetActivity.this, url);
                        finish();
                    } else{
                        PascHybrid.getInstance().start(NoNetActivity.this, PascHybrid
                                .getInstance().webStrategyMap.get(mStrategy));
                        finish();
                    }
                }

            }
        });
    }
}
