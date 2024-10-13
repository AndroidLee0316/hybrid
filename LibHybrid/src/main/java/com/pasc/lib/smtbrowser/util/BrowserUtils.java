package com.pasc.lib.smtbrowser.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.util.DeviceUtils;

/**
 * 设置浏览器功能工具类
 */
public class BrowserUtils {
  /**
   * 发送短信
   */
  public static void sendSMS(Context context, String phoneNumbers, String content) {
    Uri smsToUri = Uri.parse("smsto:" + phoneNumbers);
    Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
    mIntent.putExtra("sms_body", content);
    context.startActivity(mIntent);
  }

  /**
   * 打开联系人
   */
  public static void openContact(Activity context) {
    Uri uri = Uri.parse("content://contacts/people");
    Intent intent = new Intent(Intent.ACTION_PICK, uri);
    context.startActivityForResult(intent, PascWebviewActivity.REQUEST_CODE_CONTACT);
  }

  /**
   * 是否设置状态栏字体颜色
   */
  public static void setStatusBarTxColor(Activity activity, boolean isDark) {
    if (isDark) {
      activity.getWindow()
          .getDecorView()
          .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    } else {
      activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
  }
}
