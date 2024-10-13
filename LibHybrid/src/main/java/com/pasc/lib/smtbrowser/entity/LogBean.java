package com.pasc.lib.smtbrowser.entity;

import com.google.gson.annotations.SerializedName;

/**
 * create by wujianning385 on 2018/7/22.
 */
public class LogBean {

    //0：debug(只在开发环境的控制台打印) 1：info(正常信息)，2：warning(警告信息)，3：error(错误信息)。后面3种会上传后台。
    @SerializedName("level")
    public int level;

    @SerializedName("info")
    public String info;
}
