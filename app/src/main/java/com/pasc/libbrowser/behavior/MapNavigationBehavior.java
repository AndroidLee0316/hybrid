package com.pasc.libbrowser.behavior;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import com.google.gson.Gson;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.smtbrowser.entity.MapNavigationBean;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import java.io.Serializable;
import java.util.List;

/**
 * create by wujianning385 on 2018/8/1.
 */
public class MapNavigationBehavior implements BehaviorHandler, Serializable {

  @Override public void handler(Context context, String data, CallBackFunction function,
          NativeResponse response) {
    Gson gson = new Gson();
    MapNavigationBean mapNavigation = gson.fromJson(data, MapNavigationBean.class);

    if (isInstallApk(context, "com.autonavi.minimap")) {// 是否安装了高德
      Intent intents = new Intent();
      intents.setData(Uri.parse("androidamap://navi?sourceApplication=nyx_super&lat="
              + mapNavigation.endLatitude
              + "&lon="
              + mapNavigation.endLongitude
              + "&dev=0&style=2"));
      context.startActivity(intents); // 启动调用
    } else if (!isInstallApk(context, "com.autonavi.minimap") && !isInstallApk(context,
            "com.baidu.BaiduMap")) {
      Intent intent = new Intent();
      intent.setAction("android.intent.action.VIEW");
      // 驾车导航
      intent.setData(Uri.parse("http://uri.amap.com/navigation?from="
              + mapNavigation.startLongitude
              + ","
              + mapNavigation.startLatitude
              + "&to="
              + mapNavigation.endLatitude
              + ","
              + mapNavigation.endLongitude
              + "&mode=car&src=nyx_super"));
      context.startActivity(intent); // 启动调用
    }
  }

  /** 判断手机中是否安装指定包名的软件 */
  private boolean isInstallApk(Context context, String name) {
    List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
    for (int i = 0; i < packages.size(); i++) {
      PackageInfo packageInfo = packages.get(i);
      if (packageInfo.packageName.equals(name)) {
        return true;
      } else {
        continue;
      }
    }
    return false;
  }
}
