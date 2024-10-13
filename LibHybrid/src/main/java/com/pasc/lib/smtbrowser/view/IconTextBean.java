package com.pasc.lib.smtbrowser.view;

/**
 * @date 2021-04-13
 * @des
 * @modify
 **/
public class IconTextBean {

    public String iconUrl;
    public int iconResource=-1;
    public String text;

    public IconTextBean(String iconUrl, String text) {
        this.iconUrl = iconUrl;
        this.text = text;
    }

    public IconTextBean(int iconResource, String text) {
        this.iconResource = iconResource;
        this.text = text;
    }
}
