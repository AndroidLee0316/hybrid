package com.pasc.libbrowser.behavior;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.chenkun305.libbrowser.R;
import com.google.gson.Gson;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.share.ShareManager;
import com.pasc.lib.share.config.ShareContent;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import com.pasc.lib.smtbrowser.entity.WebShareBean;
import java.io.Serializable;

/**
 * create by wujianning385 on 2018/8/1.
 */
public class ShareBehavior implements BehaviorHandler,Serializable {


    @Override public void handler(Context context, String data, CallBackFunction function,
            NativeResponse response) {
        Log.e("aaaaa", "======== ShareBehavior");

    }

}
