package com.pasc.lib.hybrid.eh.behavior;

import android.content.Context;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.hybrid.eh.activity.DriveRouteActivity;
import com.pasc.lib.smtbrowser.entity.MapNavigationBean;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import java.io.Serializable;

/**
 * create by wujianning385 on 2018/8/1.
 */
public class MapRouteBehavior implements BehaviorHandler,Serializable {
  @Override public void handler(Context context, String data, CallBackFunction function,
      NativeResponse response) {
    try {
      Gson gson = new Gson();
      MapNavigationBean mapNavigation = gson.fromJson(data, MapNavigationBean.class);
      if (!TextUtils.isEmpty(mapNavigation.endLatitude)) {
        DriveRouteActivity.start(context,mapNavigation.startLatitude,mapNavigation.startLongitude,
                mapNavigation.endLatitude,mapNavigation.endLongitude);
      }
      function.onCallBack(gson.toJson(response));
    }catch (Exception e){
      e.printStackTrace();
    }

  }
}
