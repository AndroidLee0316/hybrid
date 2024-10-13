package com.pasc.lib.openplatform.pamars;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pasc.lib.openplatform.address.AddressResp;

import java.util.List;

/**
 * 功能：开放平台单选（地址）授权获取没吃授权授权码上传参数
 * 这里本来想做成通用的，但是后台设计authData一定要传类型，不能是string，导致无法通用，坑
 */
public class AuthSelectRequestCodeParam {

    @SerializedName("appId")
    public String appId;

    @SerializedName("token")
    public String token;

    @SerializedName("userDataType")
    public String userDataType;

    @SerializedName("authData")
    public AddressResp authData;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
