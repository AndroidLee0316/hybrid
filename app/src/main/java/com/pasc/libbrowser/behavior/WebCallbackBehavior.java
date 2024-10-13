package com.pasc.libbrowser.behavior;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.smtbrowser.entity.NativeResponse;

public class WebCallbackBehavior implements BehaviorHandler {


    @Override
    public void handler(Context context, String data, CallBackFunction function, NativeResponse response) {
        try {

            Gson gson = new Gson();
            WebCallbackBean tempData = gson.fromJson(data,WebCallbackBean.class);
            if ("webCallback".equals(tempData.path)) {
                response.code = tempData.code;
                response.message = tempData.message;
                response.data = tempData.data;
                function.onCallBack(gson.toJson(response));
                PascHybrid.getInstance()
                    .triggerCallbackFunction(ConstantBehaviorName.OPEN_NEW_WEBVIEW, tempData.code,
                        tempData.message, tempData.data);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class TestData{

        @SerializedName("akey")
        public String akey;
    }

    private class WebCallbackBean<T>{

        @SerializedName("path")
        public String path;

        @SerializedName("code")
        public int code;

        @SerializedName("message")
        public String message;

        @SerializedName("data")
        public T data;

    }
}
