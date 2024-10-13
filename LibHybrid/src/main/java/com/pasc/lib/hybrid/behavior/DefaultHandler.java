package com.pasc.lib.hybrid.behavior;

import android.content.Context;
import com.google.gson.Gson;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.smtbrowser.entity.NativeResponse;

/**
 * create by wujianning385 on 2018/7/17.
 */
public class DefaultHandler implements BehaviorHandler {


    @Override
    public void handler(Context context, String data, CallBackFunction function, NativeResponse response) {

        if (function != null) {
            function.onCallBack(new Gson().toJson(response));
        }
    }
}
