package com.pasc.lib.openplatform;

import com.pasc.lib.hybrid.HybridInitConfig;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.openplatform.network.UrlManager;

/**
 * 开放平台初始化类，外部传入必要的参数
 * create by wujianning385 on 2018/10/22.
 */
public class PascOpenPlatform {

    private OpenPlatformProvider mOpenPlatformProvider;
    private IBizCallback mIBizCallback;

    private static class Singleton {
        private static PascOpenPlatform instance = new PascOpenPlatform();
    }

    private PascOpenPlatform() {
    }

    public static PascOpenPlatform getInstance() {
        return Singleton.instance;
    }

    public void init(OpenPlatformProvider openPlatformProvider) {
        this.mOpenPlatformProvider = openPlatformProvider;
        UrlManager.init();
    }

    /**
     * 开放平台和hybrid一起初始化
     * @param openPlatformProvider
     * @param hybridInitConfig
     */
    public void init(OpenPlatformProvider openPlatformProvider,HybridInitConfig hybridInitConfig){
        this.mOpenPlatformProvider = openPlatformProvider;
        PascHybrid.getInstance().init(hybridInitConfig);
        UrlManager.init();
    }

    public OpenPlatformProvider getOpenPlatformProvider() {
        return mOpenPlatformProvider;
    }

    public void setIBizCallback(IBizCallback iBizCallback){
        this.mIBizCallback = iBizCallback;
    }

    public IBizCallback getIBizCallback() {
        return mIBizCallback;
    }
}
