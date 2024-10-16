package com.pasc.lib.openplatform;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * create by wujianning385 on 2018/11/20.
 */
public class NotchScreenUtils {

  private static String TAG = "NotchScreenUtils";

  /**
   * 判断是否是刘海屏
   */
  public static boolean hasNotchScreen(Context context) {
    if (getInt("ro.miui.notch", context) == 1
            || hasNotchAtHuawei(context)
            || hasNotchAtOPPO(context)
            || hasNotchAtVivo(context)) { //TODO 各种品牌
      return true;
    }

    return false;
  }

  ///**
  // * Android P 刘海屏判断
  // * @param activity
  // * @return
  // */
  //public static DisplayContext isAndroidP(Activity activity){
  //  View decorView = activity.getWindow().getDecorView();
  //  if (decorView != null && android.os.Build.VERSION.SDK_INT >= 28){
  //    WindowInsets windowInsets = decorView.getRootWindowInsets();
  //    if (windowInsets != null)
  //      return windowInsets.getDis();
  //  }
  //  return null;
  //}

  /**
   * 小米刘海屏判断.
   *
   * @return 0 if it is not notch ; return 1 means notch
   * @throws IllegalArgumentException if the key exceeds 32 characters
   */
  public static int getInt(String key, Context context) {
    int result = 0;
    if (isXiaomi()) {
      try {
        ClassLoader classLoader = context.getClassLoader();
        @SuppressWarnings("rawtypes") Class SystemProperties =
                classLoader.loadClass("android.os.SystemProperties");
        //参数类型
        @SuppressWarnings("rawtypes") Class[] paramTypes = new Class[2];
        paramTypes[0] = String.class;
        paramTypes[1] = int.class;
        Method getInt = SystemProperties.getMethod("getInt", paramTypes);
        //参数
        Object[] params = new Object[2];
        params[0] = new String(key);
        params[1] = new Integer(0);
        result = (Integer) getInt.invoke(SystemProperties, params);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  /**
   * 华为刘海屏判断
   */
  public static boolean hasNotchAtHuawei(Context context) {
    boolean ret = false;
    try {
      ClassLoader classLoader = context.getClassLoader();
      Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
      Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
      ret = (boolean) get.invoke(HwNotchSizeUtil);
    } catch (ClassNotFoundException e) {
      Log.e(TAG, "hasNotchAtHuawei ClassNotFoundException");
    } catch (NoSuchMethodException e) {
      Log.e(TAG, "hasNotchAtHuawei NoSuchMethodException");
    } catch (Exception e) {
      Log.e(TAG, "hasNotchAtHuawei Exception");
    } finally {
      return ret;
    }
  }

  public static final int VIVO_NOTCH = 0x00000020;//是否有刘海
  public static final int VIVO_FILLET = 0x00000008;//是否有圆角

  /**
   * VIVO刘海屏判断
   */
  public static boolean hasNotchAtVivo(Context context) {
    boolean ret = false;
    try {
      ClassLoader classLoader = context.getClassLoader();
      Class FtFeature = classLoader.loadClass("android.util.FtFeature");
      Method method = FtFeature.getMethod("isFeatureSupport", int.class);
      ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
    } catch (ClassNotFoundException e) {
      Log.e(TAG, "hasNotchAtVivo ClassNotFoundException");
    } catch (NoSuchMethodException e) {
      Log.e(TAG, "hasNotchAtVivo NoSuchMethodException");
    } catch (Exception e) {
      Log.e(TAG, "hasNotchAtVivo Exception");
    } finally {
      return ret;
    }
  }

  /**
   * OPPO刘海屏判断
   */
  public static boolean hasNotchAtOPPO(Context context) {
    return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
  }

  // 是否是小米手机
  public static boolean isXiaomi() {
    return "Xiaomi".equals(Build.MANUFACTURER);
  }
}
