 package com.pasc.libbrowser.behavior;

 import android.content.Context;
 import android.text.TextUtils;
 import com.google.gson.Gson;
 import com.pasc.lib.hybrid.behavior.BehaviorHandler;
 import com.pasc.lib.hybrid.callback.CallBackFunction;
 import com.pasc.lib.smtbrowser.entity.GpsInfoBean;
 import com.pasc.lib.smtbrowser.entity.NativeResponse;
 import com.pasc.libbrowser.App;
 import com.pasc.libbrowser.utils.ACache;
 import java.io.Serializable;

 /**
 * create by wujianning385 on 2018/8/1.
 */
public class GetGPSInfoBehavior implements BehaviorHandler,Serializable{
    @Override public void handler(Context context, String data, CallBackFunction function,
            NativeResponse response) {
        ACache aCache = ACache.get(App.getContext());
        String locationCache = aCache.getAsString("locationInfo");
        Gson gson = new Gson();

        if (!TextUtils.isEmpty(locationCache)) {
            response.data = gson.fromJson(locationCache, GpsInfoBean.class);

        }
        function.onCallBack(gson.toJson(response));
    }
}
