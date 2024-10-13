package com.pasc.lib.hybrid.eh.behavior;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pasc.lib.base.AppProxy;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.smtbrowser.entity.NativeResponse;

import java.io.Serializable;

public class GetUserInfoBehavior implements BehaviorHandler,Serializable {
    @Override
    public void handler(Context context, String data, CallBackFunction callBackFunction, NativeResponse nativeResponse) {
        try {
            Gson gson = new Gson();
            UserInfo userInfo = new UserInfo();
//            if(TextUtils.isEmpty( AppProxy.getInstance().getUserManager().getToken())){
//                nativeResponse.code = -1;
//                nativeResponse.message = "user info invalid";
//                callBackFunction.onCallBack(gson.toJson(nativeResponse));
//            }else{
                userInfo.token = "88C814C18DC1428196469CC7046D22491661410179940";
//                userInfo.userId = AppProxy.getInstance().getUserManager().getUserId();
//                userInfo.mobile = AppProxy.getInstance().getUserManager().getMobile();
//                userInfo.userName = AppProxy.getInstance().getUserManager().getUserName();
//                userInfo.isAuth = AppProxy.getInstance().getUserManager().isCertified();
                nativeResponse.data = userInfo;
                nativeResponse.code = 0;
                callBackFunction.onCallBack(gson.toJson(nativeResponse));
//            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static class UserInfo{
        @SerializedName("token")
        public String token;

        @SerializedName("userId")
        public String userId;

        @SerializedName("mobile")
        public String mobile;

        @SerializedName("userName")
        public String userName;

        @SerializedName("isAuth")
        public boolean isAuth;
    }
}
