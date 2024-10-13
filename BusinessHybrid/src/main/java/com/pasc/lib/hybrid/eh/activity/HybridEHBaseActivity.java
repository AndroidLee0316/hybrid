package com.pasc.lib.hybrid.eh.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.pasc.lib.base.activity.BaseActivity;


public class HybridEHBaseActivity extends BaseActivity {

    private boolean isDestroy = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int layoutResId() {
        return 0;
    }

    @Override
    protected void onInit(@Nullable Bundle bundle) {

    }

    public boolean isActivityDestroy() {
        return isDestroy;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isDestroy = true;
    }

}