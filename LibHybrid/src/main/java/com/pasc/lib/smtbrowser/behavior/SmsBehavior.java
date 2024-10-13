package com.pasc.lib.smtbrowser.behavior;

import android.content.Context;

import com.google.gson.Gson;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.behavior.DefaultBehaviorManager;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.smtbrowser.entity.NativeResponse;

import java.io.Serializable;

public class SmsBehavior implements BehaviorHandler,Serializable {
    @Override
    public void handler(Context context, String data, CallBackFunction function, NativeResponse response) {
        android.os.Message message = DefaultBehaviorManager.getInstance().getUIHandler().obtainMessage();
        message.what = DefaultBehaviorManager.ACTION_BEHAVIOR_SEND_SMS;
        message.obj = data;
        DefaultBehaviorManager.getInstance().getUIHandler().sendMessage(message);
        function.onCallBack(new Gson().toJson(response));
    }
}
