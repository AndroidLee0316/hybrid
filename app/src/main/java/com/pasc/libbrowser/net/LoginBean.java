package com.pasc.libbrowser.net;

import com.google.gson.annotations.SerializedName;

public class LoginBean {
    @SerializedName("mobile")
    public String mobile;
    @SerializedName("verificationCode")
    public String verificationCode;
    @SerializedName("verificationType")
    public String verificationType = "SMS_MOBILE_LOGIN";
    @SerializedName("env")
    public Env env;

    public static class Env{
        @SerializedName("osType")
        public String osType = "2";
        @SerializedName("deviceId")
        public String deviceId = "testDeviceId3";
    }
}
