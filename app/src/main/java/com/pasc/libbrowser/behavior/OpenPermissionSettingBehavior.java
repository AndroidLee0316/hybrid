package com.pasc.libbrowser.behavior;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import java.io.Serializable;

/**
 * 打开应用权限设置页
 * create by wujianning385 on 2018/8/20.
 */
public class OpenPermissionSettingBehavior implements BehaviorHandler,Serializable{
    @Override public void handler(Context context, String data, CallBackFunction function,
            NativeResponse response) {
        //PermissionSettingUtils.gotoApplicationDetails(context);

        Intent localIntent = new Intent();
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }
}
