package com.pasc.lib.openplatform.resp;

import com.google.gson.annotations.SerializedName;

/**
 * 功能：
 * <p>
 * created by zoujianbo345
 * data : 2018/9/17
 */
public class ServiceAuthResult {
    @SerializedName("openId")
    public String openId;
    @SerializedName("requestCode")
    public String requestCode ;
    @SerializedName("expiresIn")
    public int expiresIn = 300;
}
