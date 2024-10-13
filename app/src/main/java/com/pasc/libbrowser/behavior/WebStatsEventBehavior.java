 package com.pasc.libbrowser.behavior;

 import android.content.Context;
 import com.google.gson.Gson;
 import com.pasc.lib.hybrid.behavior.BehaviorHandler;
 import com.pasc.lib.hybrid.callback.CallBackFunction;
 import com.pasc.lib.smtbrowser.entity.NativeResponse;
 import com.pasc.libbrowser.data.WebEventBean;
 import java.io.Serializable;

 /**
 * create by wujianning385 on 2018/8/3.
 */
public class WebStatsEventBehavior implements BehaviorHandler,Serializable{
    @Override public void handler(Context context, String data, CallBackFunction function,
            NativeResponse response) {

        try {
            Gson gson = new Gson();
            WebEventBean webEvent = gson.fromJson(data,WebEventBean.class);
            //EventUtils.onEvent(webEvent.eventId,webEvent.label,webEvent.map);
            function.onCallBack(gson.toJson(response));
        }catch (RuntimeException e){
            e.printStackTrace();
        }
    }
}
