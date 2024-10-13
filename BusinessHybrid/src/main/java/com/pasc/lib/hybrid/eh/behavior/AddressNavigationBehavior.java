package com.pasc.lib.hybrid.eh.behavior;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.hybrid.eh.activity.AddressNavigationActivity;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import java.io.Serializable;

/**
 * create by wujianning385 on 2018/10/18.
 */
public class AddressNavigationBehavior implements BehaviorHandler, Serializable {
  @Override public void handler(Context context, String s, CallBackFunction callBackFunction,
          NativeResponse nativeResponse) {

    try {
      WebLocationBean webLocation = new Gson().fromJson(s, WebLocationBean.class);
      AddressNavigationActivity.start(context, Double.valueOf(webLocation.latitude),
              Double.valueOf(webLocation.longitude), webLocation.name, webLocation.address);
      callBackFunction.onCallBack(new Gson().toJson(nativeResponse));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static class WebLocationBean {
    @SerializedName("latitude") public String latitude;

    @SerializedName("longitude") public String longitude;

    @SerializedName("name") public String name;

    @SerializedName("address") public String address;
  }
}
