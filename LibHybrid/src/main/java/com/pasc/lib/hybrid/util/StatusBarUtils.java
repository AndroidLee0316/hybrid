package com.pasc.lib.hybrid.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class StatusBarUtils {

  public static void setTransparent(Activity activity) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
      return;
    }
    transparentStatusBar(activity);
    setRootView(activity);
  }

  @TargetApi(Build.VERSION_CODES.KITKAT)
  private static void transparentStatusBar(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
      activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
    } else {
      activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
  }

  public static void setRootView(Activity activity) {
    ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
    for (int i = 0, count = parent.getChildCount(); i < count; i++) {
      View childView = parent.getChildAt(i);
      if (childView instanceof ViewGroup) {
        childView.setFitsSystemWindows(true);
        ((ViewGroup) childView).setClipToPadding(true);
      }
    }
  }

  public static void setRootView(Activity activity, boolean fitsSystemWindows) {
    ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
    for (int i = 0, count = parent.getChildCount(); i < count; i++) {
      View childView = parent.getChildAt(i);
      if (childView instanceof ViewGroup) {
        childView.setFitsSystemWindows(fitsSystemWindows);
        ((ViewGroup) childView).setClipToPadding(true);
      }
    }
  }

  public static void transparentStatusBar(Window window){

  }

  public static void setTransparentForWindow(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
      activity.getWindow()
          .getDecorView()
          .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      activity.getWindow()
          .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }
  }

  public static int getStatusBarHeight(Context context) {
    // 获得状态栏高度
    int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
    return context.getResources().getDimensionPixelSize(resourceId);
  }

}
