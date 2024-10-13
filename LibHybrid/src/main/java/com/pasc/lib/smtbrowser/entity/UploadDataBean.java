package com.pasc.lib.smtbrowser.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UploadDataBean implements Serializable {
    @SerializedName("path")
    public String path;
    @SerializedName("fileName")
    public String fileName;
    @SerializedName("fileSize")
    public String fileSize;
    @SerializedName("imageIcon")
    public String imageIcon;
    @SerializedName("fileType")
    public String fileType;
    @SerializedName("status")
    public String status;
}
