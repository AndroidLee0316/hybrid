package com.pasc.lib.hybrid.callback;

import android.content.Intent;

public interface ActivityResultCallback {
    void activityResult(int requestCode,int resultCode,Intent intent);
}
