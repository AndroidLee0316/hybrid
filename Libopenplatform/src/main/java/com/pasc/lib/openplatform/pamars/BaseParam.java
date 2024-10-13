package com.pasc.lib.openplatform.pamars;

import com.google.gson.annotations.SerializedName;

/**
 * 功能：
 * <p>
 * created by zoujianbo345
 * data : 2018/9/18
 */
public class BaseParam<T> {
    @SerializedName("userId")
    public String userId;
    @SerializedName("data")
    public T data;

    public BaseParam(T data) {
        this.data = data;
    }
}