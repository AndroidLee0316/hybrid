package com.pasc.lib.smtbrowser.entity;

import com.google.gson.annotations.SerializedName;

/**
 * 返回给H5的基本对象类型
 * <p>
 * create by wujianning385 on 2018/6/27.
 */
public class NativeResponse<T> {

    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public T data;


}
