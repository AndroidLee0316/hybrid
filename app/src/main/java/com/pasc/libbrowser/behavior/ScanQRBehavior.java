package com.pasc.libbrowser.behavior;

import android.content.Context;
import android.content.Intent;

import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import java.io.Serializable;

/**
 * create by wujianning385 on 2018/8/1.
 */
public class ScanQRBehavior implements BehaviorHandler,Serializable {


    @Override public void handler(Context context, String data, CallBackFunction function,
            NativeResponse response) {
        PascHybrid.getInstance().saveCallBackFunction(context.hashCode(),ConstantBehaviorName.QR_CODE_SCAN,function);
        //Intent intent = new Intent(context, WebQRCapturesActivity.class);
        //context.startActivity(intent);

    }
}
