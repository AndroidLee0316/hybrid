package com.pasc.libbrowser.utils;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.pasc.lib.smtbrowser.entity.GpsInfoBean;
import com.pasc.libbrowser.App;

/**
 * create by wujianning385 on 2018/9/3.
 */
public class Locator {


  public static void doLocation(Context context, boolean b) {

    try {
      //初始化定位
      AMapLocationClient mLocationClient = new AMapLocationClient(context);
      //设置定位回调监听
      mLocationClient.setLocationListener(new AMapLocationListener() {
        @Override public void onLocationChanged(AMapLocation aMapLocation) {
          //缓存定位数据，供web使用
          GpsInfoBean gpsInfo = new GpsInfoBean(aMapLocation.getLongitude(),aMapLocation.getLatitude(),
                  aMapLocation.getCountry(),aMapLocation.getProvince(),aMapLocation.getCity(),aMapLocation.getDistrict(),
                  aMapLocation.getAddress(),aMapLocation.getCityCode(),aMapLocation.getAdCode());
          Gson gson = new Gson();
          String locationStr = gson.toJson(gpsInfo);
          ACache aCache = ACache.get(App.getContext());
          aCache.put("locationInfo",locationStr);
        }
      });
      //启动定位
      mLocationClient.startLocation();
    }catch (Exception e){
      e.printStackTrace();
    }


  }


}
