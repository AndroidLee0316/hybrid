package com.pasc.lib.hybrid.eh.utils;

import android.content.Context;

public class SizeUtils {

  public static int px2dp(Context context, final int pxValue) {
    if (context == null) {
      throw new RuntimeException("Context must not be null.");
    }
    float density = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / density + 0.5f);
  }
}
