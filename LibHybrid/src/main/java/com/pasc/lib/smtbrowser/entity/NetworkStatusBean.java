package com.pasc.lib.smtbrowser.entity;

import com.google.gson.annotations.SerializedName;

/**
 * create by wujianning385 on 2018/8/23.
 */
public class NetworkStatusBean {

    //0：无网络 1：2G,3G,4G 2:WiFi
    @SerializedName("status")
    public int status;

    public NetworkStatusBean(int status) {
        this.status = status;
    }
}
