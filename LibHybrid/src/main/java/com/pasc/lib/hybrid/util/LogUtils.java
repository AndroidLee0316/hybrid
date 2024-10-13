package com.pasc.lib.hybrid.util;

import android.util.Log;

import com.pasc.lib.hybrid.BuildConfig;

/**
 * @author Created by buyongyou on 2018/12/27.
 * @Email: buyongyou490@pingan.com.cn
 * @des
 */
public class LogUtils {
    public static void i(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.i(tag,  message);
        }
    }

    public static void w(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.w(tag,  message);
        }
    }
    public static void e(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.e(tag,  message);
        }
    }
    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag,  message);
        }
    }

}
