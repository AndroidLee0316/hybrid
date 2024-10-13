package com.pasc.lib.smtbrowser.entity;

import com.google.gson.annotations.SerializedName;

/**
 * create by wujianning385 on 2018/7/18.
 */
public class DeviceBean {

    //设备名称
    @SerializedName("deviceName")
    private String deviceName;

    //系统 如：0:iOS，1:Android，2-N:Android-华为...
    @SerializedName("sysType")
    private int sysType;

    //系统sdk版本
    @SerializedName("sysVersion")
    private String sysVersion;

    @SerializedName("appName")
    private String appName;

    @SerializedName("appVersion")
    private String appVersion;

    //包名
    @SerializedName("appID")
    private String appID;

    @SerializedName("IDFV")
    private String IDFV;

    @SerializedName("IMEI")
    private String IMEI;

    @SerializedName("hybridVersion")
    private String hybridVersion;

    //渠道号
    @SerializedName("channelID")
    private String channelID;


    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getSysType() {
        return sysType;
    }

    public void setSysType(int sysType) {
        this.sysType = sysType;
    }

    public String getSysVersion() {
        return sysVersion;
    }

    public void setSysVersion(String sysVersion) {
        this.sysVersion = sysVersion;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getIDFV() {
        return IDFV;
    }

    public void setIDFV(String IDFV) {
        this.IDFV = IDFV;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getHybridVersion() {
        return hybridVersion;
    }

    public void setHybridVersion(String hybridVersion) {
        this.hybridVersion = hybridVersion;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }
}
