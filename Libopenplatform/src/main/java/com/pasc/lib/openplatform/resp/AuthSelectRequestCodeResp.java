package com.pasc.lib.openplatform.resp;

import com.google.gson.annotations.SerializedName;

/**
 * 功能：获取每次授权授权码返回参数
 * <p>
 * created by zoujianbo345
 * data : 2018/9/17
 */
public class AuthSelectRequestCodeResp {

    @SerializedName("requestCode")
    public String requestCode;
    @SerializedName("expiresIn")
    public int expiresIn;
    @SerializedName("everyTimeRequestCode")
    public String everyTimeRequestCode;
}
