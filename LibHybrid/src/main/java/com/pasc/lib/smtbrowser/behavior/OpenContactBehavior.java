package com.pasc.lib.smtbrowser.behavior;

import android.content.Context;

import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.hybrid.behavior.DefaultBehaviorManager;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.smtbrowser.entity.NativeResponse;

import java.io.Serializable;


public class OpenContactBehavior implements BehaviorHandler,Serializable {
    @Override
    public void handler(Context context, String data, CallBackFunction function, NativeResponse response) {
        android.os.Message message = DefaultBehaviorManager.getInstance().getUIHandler().obtainMessage();
        message.what = DefaultBehaviorManager.ACTION_BEHAVIOR_SELECT_CONTACT;
        DefaultBehaviorManager.getInstance().getUIHandler().sendMessage(message);

      PascHybrid.getInstance()
              .saveCallBackFunction(context.hashCode(), ConstantBehaviorName.OPEN_CONTACT,
                      function);
    }
}
