package com.pasc.libbrowser.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * create by wujianning385 on 2018/8/2.
 */
public class BigPhotosBean {

    @SerializedName("index")
    public int index;

    @SerializedName("urls")
    public List<String> urls;
}
