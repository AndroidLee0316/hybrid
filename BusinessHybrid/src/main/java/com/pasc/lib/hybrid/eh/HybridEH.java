package com.pasc.lib.hybrid.eh;

import android.app.Application;
import com.pasc.lib.base.AppProxy;
import com.pasc.lib.gaode.location.GaoDeLocationFactory;
import com.pasc.lib.lbs.LbsManager;

/**
 * create by wujianning385 on 2019/7/13.
 */
public class HybridEH {


  public static void init(Application application){
    AppProxy.getInstance()
            .init(application,BuildConfig.DEBUG)
            .setIsDebug(BuildConfig.DEBUG)
            .setVersionName(BuildConfig.VERSION_NAME);
    LbsManager.getInstance().initLbs(new GaoDeLocationFactory(application.getApplicationContext()));
  }
}
