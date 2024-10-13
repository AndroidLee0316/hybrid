package com.pasc.libbrowser.behavior;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import com.pasc.lib.base.AppProxy;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.hybrid.eh.behavior.GetUserInfoBehavior;
import com.pasc.lib.smtbrowser.entity.NativeResponse;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * create by wujianning385 on 2018/7/27.
 */
public class NativeRouteBehavior implements BehaviorHandler, Serializable {

    @Override
    public void handler(Context context, String data, CallBackFunction function,
                        NativeResponse response) {

        try {
            PascHybrid.getInstance()
                    .saveCallBackFunction(context.hashCode(), Constants.WEB_BEHAVIOR_NAME_NATIVE_ROUTE,
                            function);
            PascHybrid.getInstance()
                    .saveCallBackFunction(context.hashCode(), ConstantBehaviorName.OP_ROUTER, function);
            postHandler(context, data, function, response, false);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void postHandler(Context context, String data, CallBackFunction function,
                            NativeResponse response, boolean finishContext) {
        Log.e("postHandler", data + "==");
        Gson gson = new Gson();
        RawNativeRouter nativeRoute = gson.fromJson(data, RawNativeRouter.class);

        if (Constants.WEB_ROUTE_LOGIN_PATH.equals(nativeRoute.path)) {
            if (AppProxy.getInstance().getUserManager().isLogin()) {
                AppProxy.getInstance().getUserManager().exitUser(context);
            }
            //BaseJumper.jumpARouter(RouterTable.Login.PATH_LOGIN_ACTIVITY);

                    GetUserInfoBehavior.UserInfo userInfo = new GetUserInfoBehavior.UserInfo();
                    userInfo.token = "88C814C18DC1428196469CC7046D22491661410179940";
//                    userInfo.userId = AppProxy.getInstance().getUserManager().getUserId();
//                    userInfo.mobile = AppProxy.getInstance().getUserManager().getMobile();
//                    userInfo.userName = AppProxy.getInstance().getUserManager().getUserName();
//                    userInfo.isAuth = AppProxy.getInstance().getUserManager().isCertified();
                    response.data = userInfo;
                    response.code = 0;
                    response.message = "你已经登录成功";
                    function.onCallBack(gson.toJson(response));


        } else if (Constants.WEB_ROUTE_LOGIN_STATUS.equals(nativeRoute.path)) {
            response.code = 0;
            response.data =
                    new NativeTokenStatus( true );
            function.onCallBack(gson.toJson(response));
        } else if (Constants.WEB_ROUTER_FACE_CHECK_PATH.equals(nativeRoute.path)) {
            FaceCheckData faceCheckData = gson.fromJson(data, FaceCheckData.class);
            if (faceCheckData == null || faceCheckData.params == null || TextUtils.isEmpty(faceCheckData.params.appId)) {
                response.code = -2;//-2代表人脸核验传入的appid为空
                response.message = "error data from js, please check first";
                function.onCallBack(gson.toJson(response));
                return;
            }



        } else if (Constants.WEB_ROUTER_UPDATE_APP.equals(nativeRoute.path)) {

        } else if (Constants.ROUTER_TO_AUTH_FINGER.equals(nativeRoute.path)) {

        } else {


        }

        //BaseJumper.jumpARouter(nativeRoute.path);
        if (nativeRoute.closeCurWeb) {
            finishContext(context, finishContext);
        }
    }

    public class RawNativeRouter {
        @SerializedName("businessType")
        public String businessType;

        //原生页面的uri，如登录页：/account/loginActivity/main
        @SerializedName("path")
        public String path;

        //弹起原生页面的方式，弹起登录页必须传：1
        @SerializedName("openType")
        private int openType;

        //是否关闭当前webview
        @SerializedName("closeCurWeb")
        private boolean closeCurWeb;

        @SerializedName("params")
        public SearchData searchData;
    }

    class SearchData {
        @SerializedName("entranceLocation")
        public String entranceLocation;
        @SerializedName("entranceId")
        public String entranceId;
        @SerializedName("themeConfigId")
        public String themeConfigId;
        @SerializedName("sectionType")
        public String sectionType;

    }


    class FaceCheckData {
        @SerializedName("path")
        public String path;
        @SerializedName("params")
        public FaceCheckParam params;
    }

    class FaceCheckParam {
        @SerializedName("appId")
        public String appId;
    }

    class NativeRouteFaceResult {
        @SerializedName("credential")
        public String credential;

        NativeRouteFaceResult(String credential) {
            this.credential = credential;
        }
    }

    public static void finishContext(Context activity, boolean finishContext) {
        if (finishContext
                && activity != null
                && activity instanceof Activity
                && !((Activity) activity).isFinishing()) {
            ((Activity) activity).finish();
        }
    }

    public class NativeTokenStatus {

        @SerializedName("status")
        public boolean status;

        public NativeTokenStatus(boolean status) {
            this.status = status;
        }
    }





    interface CallBack {
        void onSuccess();

        void onFailed(String msg);
    }

}
