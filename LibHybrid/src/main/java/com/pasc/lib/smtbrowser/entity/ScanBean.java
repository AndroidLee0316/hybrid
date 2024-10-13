package com.pasc.lib.smtbrowser.entity;

import com.google.gson.annotations.SerializedName;

/**
 * create by wujianning385 on 2018/7/25.
 */
public class ScanBean {

    @SerializedName("scanString")
    public String scanString;

    public ScanBean(String scanString) {
        this.scanString = scanString;
    }
}
