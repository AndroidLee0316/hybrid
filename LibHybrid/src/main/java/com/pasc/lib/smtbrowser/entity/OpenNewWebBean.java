package com.pasc.lib.smtbrowser.entity;

import com.google.gson.annotations.SerializedName;

/**
 * create by wujianning385 on 2018/7/22.
 */
public class OpenNewWebBean {

    @SerializedName("url")
    public String url;

    @SerializedName("hideNavBar")
    public boolean hideNavBar;

    @SerializedName("closeCurWeb")
    public boolean closeCurWeb;
}
