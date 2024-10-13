package com.pasc.lib.smtbrowser.entity;

import com.google.gson.annotations.SerializedName;

/**
 * create by wujianning385 on 2018/7/18.
 */
public class ToastBean {

    //默认：text，其他：loding，hide，successText，errorText
    @SerializedName("type")
    private String type;

    @SerializedName("text")
    private String text;

    @SerializedName("detailText")
    private String detailText;

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getDetailText() {
        return detailText;
    }
}
