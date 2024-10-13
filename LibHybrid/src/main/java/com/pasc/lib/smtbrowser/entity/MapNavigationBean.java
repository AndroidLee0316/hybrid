package com.pasc.lib.smtbrowser.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * create by wujianning385 on 2018/7/25.
 */
public class MapNavigationBean implements Serializable {

    @SerializedName("startAddress")
    public String startAddress = "";

    @SerializedName("startLongitude")
    public String startLongitude;

    @SerializedName("startLatitude")
    public String startLatitude;

    @SerializedName("endAddress")
    public String endAddress = "";

    @SerializedName("endLongitude")
    public String endLongitude;

    @SerializedName("endLatitude")
    public String endLatitude;

    @SerializedName("type")
    public String type = "";
}
