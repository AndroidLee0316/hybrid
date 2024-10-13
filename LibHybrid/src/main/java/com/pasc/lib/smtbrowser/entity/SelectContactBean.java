package com.pasc.lib.smtbrowser.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 选择联系人后返回给web
 * create by wujianning385 on 2018/7/24.
 */
public class SelectContactBean {

    @SerializedName("name")
    public String name;

    @SerializedName("phone")
    public String phone;

    public SelectContactBean(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
}
