package com.pasc.lib.smtbrowser.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * create by wujianning385 on 2018/7/25.
 */
public class RespShareBean implements Serializable{

    @SerializedName("platform")
    public int platform;

    public RespShareBean(int platform) {
        this.platform = platform;
    }
}
