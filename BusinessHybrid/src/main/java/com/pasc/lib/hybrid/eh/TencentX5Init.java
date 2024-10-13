package com.pasc.lib.hybrid.eh;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.pasc.lib.base.AppProxy;
import com.pasc.lib.base.permission.PermissionUtils;
import com.tencent.smtt.sdk.QbSdk;

import java.lang.ref.WeakReference;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author yangzijian
 * @date 2018/10/18
 * @des
 * @modify
 **/
public class TencentX5Init {
    private static boolean isInit=false;
    private TencentX5Init(){}
    private static class SingletonHolder {
        private static final TencentX5Init INSTANCE = new TencentX5Init();
    }
    /**是否已经初始化***/
    public static boolean isInit(){
        return isInit;
    }
    public static TencentX5Init getInstance() {
        return SingletonHolder.INSTANCE;
    }
    /***是否在初始化中***/
    private boolean isIniting = false;
    /*** X5 失败的话重试三次**/
    private int retryTimes = 3;
    private String[] tbsPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};
    WeakReference<Activity> mActivty;
    /**
     * 检查手机权限 sdcard的逻辑，如果允许，则初始化QbSdk。
     */
    public void initX5(Activity activity) {
        QbSdk.setNeedInitX5FirstTime(true);
        if (isInit){
            return;
        }

        //DataCleanManager.deleteDir(new File("/data/data/com.pingan.nt/shared_prefs/tbs_download_config.xml"));
        SharedPreferences sp = AppProxy.getInstance().getApplication().getSharedPreferences
                ("tbs_download_config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();

        if (activity==null){
            return;
        }
        mActivty=new WeakReference<>(activity);
        try {
            // 权限框 有时候崩溃了
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init(){
        Activity activity=mActivty.get();
        Context applicationCtx =activity;
        PermissionUtils.request(activity, tbsPermissions)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) {
                        if (aBoolean) {
                            QbSdk.initX5Environment(applicationCtx, new QbSdk.PreInitCallback() {
                                @Override
                                public void onCoreInitFinished() {
                                }

                                @Override
                                public void onViewInitFinished(boolean b) {
                                    isInit=b;
                                    Log.d("tbsInit", "x5 内核加载 " + (b ? "加载成功" : "加载失败"));
                                    if (!b) {
                                        retryTimes--;
                                        if (retryTimes >= 0) {
                                            initX5(activity);
                                        }
                                    }

                                }
                            });
                        }
                    }
                });

    }

}
