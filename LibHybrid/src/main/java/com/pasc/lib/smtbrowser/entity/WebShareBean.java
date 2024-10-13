package com.pasc.lib.smtbrowser.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * create by wujianning385 on 2018/7/17.
 */
public class WebShareBean {

    @SerializedName("image")
    private String image = "";

    @SerializedName("title")
    private String title = "";

    @SerializedName("content")
    private String content = "";

    @SerializedName("shareUrl")
    private String shareUrl = "";



    @SerializedName("shareTypes")
    private List<ExtInfo> shareType;



    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public List<ExtInfo> getShareType() {
        return shareType;
    }

    public static class ExtInfo{

        @SerializedName("platformID")
        private int platformID;

        @SerializedName("content")
        private String content;

        @SerializedName("shareUrl")
        private String shareUrl;

        public int resId;


        public String introduce;

        public int getPlatformID() {
            return platformID;
        }

        public String getContent() {
            return content;
        }

        public String getShareUrl() {
            return shareUrl;
        }

        public void setPlatformID(int platformID) {
            this.platformID = platformID;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setShareUrl(String shareUrl) {
            this.shareUrl = shareUrl;
        }
    }


}
