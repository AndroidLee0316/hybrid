package com.pasc.lib.hybrid.eh.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BigPhotosBean {
    @SerializedName("index")
    public int index;

    @SerializedName("urls")
    public List<String> urls;

    @SerializedName("backgroundColor")
    public String backgroundColor;
}
