package com.pasc.libbrowser.behavior;

import android.content.Context;
import com.google.gson.Gson;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.smtbrowser.entity.BackToHomeBean;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import com.pasc.libbrowser.MainActivity;
import java.io.Serializable;

/**
 * create by wujianning385 on 2018/8/1.
 */
public class BackToHomeBehavior implements BehaviorHandler,Serializable{


    @Override public void handler(Context context, String data, CallBackFunction function,
            NativeResponse response) {
        BackToHomeBean backToHomeBean = new Gson().fromJson(data,BackToHomeBean.class);
        int index = backToHomeBean.index;
        MainActivity.start(context,index);
    }
}
