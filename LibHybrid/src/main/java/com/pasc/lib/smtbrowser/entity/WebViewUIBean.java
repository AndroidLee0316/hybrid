package com.pasc.lib.smtbrowser.entity;

import com.google.gson.annotations.SerializedName;

public class WebViewUIBean {
    @SerializedName("webViewBackgroundColor")
    public String webViewBackgroundColor;
    //禁止滚动
    @SerializedName("banScroll")
    public boolean banScroll;
    //禁止回弹 android没有
    @SerializedName("banBounces")
    public boolean banBounces;
    //禁止网页alert
    @SerializedName("banAlert")
    public boolean banAlert;
    @SerializedName("progressColor")
    public String progressColor;
    //滚动到指定位置
    @SerializedName("verticalOffset")
    public float verticalOffset;
}
