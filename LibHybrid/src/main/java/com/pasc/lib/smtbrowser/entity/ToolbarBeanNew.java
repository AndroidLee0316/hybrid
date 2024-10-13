package com.pasc.lib.smtbrowser.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * create by wujianning385 on 2018/7/16.
 */
public class ToolbarBeanNew {

    public static final int STATUS_BAR_STYLE_DARK = 0;
    public static final int STATUS_BAR_STYLE_LIGHT = 1;

    //是否隐藏导航栏
    @SerializedName("isHide")
    private boolean isHide;

    //标题栏背景颜色
    @SerializedName("backgroundColor")
    private String backgroundColor;

    //标题栏子控件统一颜色
    @SerializedName("tintColor")
    private String tintColor;

    //标题（只有第一个网页设置这个标题）
    @SerializedName("title")
    private String title;

    //加载中临时标题，会被网页title覆盖
    @SerializedName("placeholderTitle")
    private String placeholderTitle;

    //标题（这次跳转的所有网页设置这个标题）
    @SerializedName("webViewTitle")
    private String webViewTitle;

    //标题颜色
    @SerializedName("titleTextColor")
    private String titleTextColor;

    //标题字体大小
    @SerializedName("titleTextSize")
    private float titleTextSize;

    //子标题
    @SerializedName("subtitle")
    private String subtitle;

    //子标题颜色
    @SerializedName("subtitleTextColor")
    private String subtitleTextColor;

    //子标题大小
    @SerializedName("subtitleTextSize")
    private float subtitleTextSize;

    //状态栏文字风格 0：黑色，1：白色
    @SerializedName("statusBarStyle")
    private int statusBarStyle = STATUS_BAR_STYLE_DARK; // 默认是黑色

    //状态栏背景色
    @SerializedName("statusBarBackgroundColor")
    private String statusBarBackgroundColor;

    //左边按钮
    @SerializedName("leftBtns")
    private List<BtnOpts> leftBtns;

    //右边按钮集合
    @SerializedName("rightBtns")
    private List<BtnOpts> rightBtns;

    @SerializedName("hideBottomLine")
    private boolean hideBottomLine;

    //沉浸状态栏
    @SerializedName("isWebImmersive")
    private boolean isWebImmersive;

    //标题栏颜色渐变  [#000000, #FFFFFF] 同时传渐变色和背景色，渐变色优先
    @SerializedName("gradientBackgroundColors")
    private List<String> gradientBackgroundColors;

    //渐变方向 0：从上到下 1：从左到右 2：从左上到右下 3：从左下到右上
    @SerializedName("gradientDirection")
    private int gradientDirection;

    //导航栏初始透明，向上滚动时渐变成不透明
    @SerializedName("isProgressiveOpacity")
    private boolean isProgressiveOpacity;

    public boolean isWebImmersive() {
        return isWebImmersive;
    }

    public void setWebImmersive(boolean webImmersive) {
        isWebImmersive = webImmersive;
    }

    public List<String> getGradientBackgroundColors() {
        return gradientBackgroundColors;
    }

    public int getGradientDirection() {
        return gradientDirection;
    }

    public boolean isHide() {
        return isHide;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getTintColor() {
        return tintColor;
    }

    public String getTitle() {
        return title;
    }

    public String getTitleTextColor() {
        return titleTextColor;
    }

    public float getTitleTextSize() {
        return titleTextSize;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getSubtitleTextColor() {
        return subtitleTextColor;
    }

    public float getSubtitleTextSize() {
        return subtitleTextSize;
    }

    public int getStatusBarStyle() {
        return statusBarStyle;
    }

    public String getStatusBarBackgroundColor() {
        return statusBarBackgroundColor;
    }

    public List<BtnOpts> getLeftBtns() {
        return leftBtns;
    }

    public List<BtnOpts> getRightBtns() {
        return rightBtns;
    }

    public boolean isHideBottomLine() {
        return hideBottomLine;
    }

    public String getPlaceholderTitle() {
        return placeholderTitle;
    }

    public String getWebViewTitle() {
        return webViewTitle;
    }

    public boolean isProgressiveOpacity() {
        return isProgressiveOpacity;
    }

    public static class BtnOpts{

        @SerializedName("color")
        private String color;

        //左边：0：返回图标 1：关闭图标；右边：0：分享 1：搜索 2：未收藏 3：已收藏
        @SerializedName("iconType")
        private int iconType = -1;

        //不为空时默认加载URL的icon
        @SerializedName("iconUrl")
        private String iconUrl;

        @SerializedName("title")
        private String title;

        //native响应按钮点击，调用web注册的方法
        @SerializedName("action")
        private String action;


        public String getColor() {
            return color;
        }

        public int getIconType() {
            return iconType;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public String getTitle() {
            return title;
        }

        public String getAction() {
            return action;
        }
    }

}
