package com.pasc.lib.smtbrowser.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OpenplatformBean {
    //第三方服务在开放平台申请的appId
    @SerializedName("appId")
    public String appId;

    //调用开放平台接口生成的初始化校验码
    @SerializedName("initCode")
    public String initCode;

    //交互Api数组
    @SerializedName("nativeApis")
    public List<String> nativeApis;
}
