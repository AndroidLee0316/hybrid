package com.pasc.libbrowser.behavior;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import com.pasc.libbrowser.LoginActivity;

import java.io.Serializable;

public class GetUserInfoBehavior implements BehaviorHandler,Serializable {
    @Override
    public void handler(Context context, String data, CallBackFunction callBackFunction, NativeResponse nativeResponse) {
        try {
            Gson gson = new Gson();
            UserInfo userInfo = new UserInfo();

                userInfo.token = LoginActivity.token;
                nativeResponse.data = userInfo;
                nativeResponse.code = 0;
                callBackFunction.onCallBack(gson.toJson(nativeResponse));


//            UserInterfaceManager.getInstance().checkToken(new OnResultListener<TokenStatusResp>() {
//                @Override
//                public void onSuccess(TokenStatusResp tokenStatusResp) {
//                    if (tokenStatusResp.isValid()) {
//                        UserInfo userInfo = new UserInfo();
//                        userInfo.token = UserManager.getInstance().getToken();
//                        userInfo.userId = UserManager.getInstance().getUserId();
//                        userInfo.mobile = UserManager.getInstance().getMobileNo();
//                        userInfo.userName = UserManager.getInstance().getUserName();
//                        userInfo.isAuth = UserManager.getInstance().isCertificationed();
//                        nativeResponse.data = userInfo;
//                        nativeResponse.code = 0;
//                        callBackFunction.onCallBack(gson.toJson(nativeResponse));
//                    } else {
//                        nativeResponse.code = -1;
//                        nativeResponse.message = "user info invalid";
//                        callBackFunction.onCallBack(gson.toJson(nativeResponse));
//                    }
//                }
//                @Override
//                public void onFailed(int code, String msg) {
//                    ToastUtils.toastMsg(msg);
//                }
//            }, context);

        }catch (Exception e){

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
