package com.pasc.lib.hybrid;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import java.lang.ref.WeakReference;

public class WebViewHandler extends Handler{
    private WeakReference<Activity> weakReference;

    public WebViewHandler(Activity activity) {
        super(Looper.getMainLooper());
        this.weakReference = new WeakReference<>(activity);
    }

    public Activity get() {
        return weakReference.get();
    }
}
