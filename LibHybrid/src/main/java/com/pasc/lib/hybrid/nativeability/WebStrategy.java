package com.pasc.lib.hybrid.nativeability;

import com.pasc.lib.hybrid.callback.OverrideCallback;

import java.io.Serializable;

public class WebStrategy implements Serializable{
    //页面url
    public String url;
    //标题
    public String title;
    //状态栏颜色
    public String statusBarColor;
    //toolbar颜色
    public String toolBarColor;
    //标题文字颜色
    public String titleTextColor;
    //返回键颜色（默认蓝色）
    public int backIconColor = WebStrategyType.BACKICONCOLOR_BLUE;
    //状态栏是否强制显示（常熟项目需要）
    public int statusBarVisibility = WebStrategyType.STATUSBAR_FOLLOWING;
    //文件选择能力
    public int fileChoose = WebStrategyType.FILECHOOSE_OFF;
    //文件浏览能力
    public int fileBrowser = WebStrategyType.FILEBROWSER_OFF;
    //标题栏
    public int toolBarVisibility = WebStrategyType.TOOLBAR_VISIBLE;
    //统计
    public int statistics = WebStrategyType.STATISTICS_ON;

    //提供给js的toast接口
    public int nativeToast = WebStrategyType.NATIVE_TOAST_OFF;
    //调用js接口传递gps数据
    public int nativeGps = WebStrategyType.NATIVE_GPS_OFF;
    //关闭整个web页面
    public int closeWeb = WebStrategyType.NATIVE_CLOSE_BROWSER_OFF;
    //打开新浏览器
    public int openBrowser = WebStrategyType.NATIVE_OPEN_BROWSER_OFF;
    //打印日志
    public int nativeLog = WebStrategyType.NATIVE_LOG_OFF;
    //新栈启动activity
    public int activityStartModle = WebStrategyType.FLAG_ACTIVITY_NORMAL;
    //标题栏下划线
    public int toolbarDivider = WebStrategyType.TOOLBAR_DIVIDER_VISIBLE;
    //标题加粗
    public int titleBold = WebStrategyType.TITLT_NORMAL;

    //是否拦截shouldoverrideurl给外界
    public int overrideUrl = WebStrategyType.OVERRIDEURL_OFF;

    public int isHideProgressBar = WebStrategyType.PROGRESS_SHOW;
    //如果在主页无网页和错误页逻辑抛给外部处理,沉浸式交由外部处理
    public int mainPageModule = WebStrategyType.NORMALPAGE;

    //是否显示第三方服务跳转提示，"1"显示，"0"不显示
    public String isNotice = "0";
    //第三方服务跳转提示页描述
    public String description;
    //第三方服务跳转提示页内容
    public String serviceProvider;
    //第三方服务跳转提示页显示时长，单位为毫秒
    public int pageShowDuration = 3000;

    public OverrideCallback overrideCallback;

    public WebStrategy setIsNotice(String isNotice) {
        this.isNotice = isNotice;
        return this;
    }

    public WebStrategy setDescription(String description) {
        this.description = description;
        return this;
    }

    public WebStrategy setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
        return this;
    }

    public WebStrategy setPageShowDuration(int pageShowDuration) {
        this.pageShowDuration = pageShowDuration;
        return this;
    }

    public WebStrategy setOverrideCallback(OverrideCallback overrideCallback) {
        this.overrideCallback = overrideCallback;
        return this;
    }

    public WebStrategy setOverrideUrl(@WebStrategyType.ShouldOverride int overrideUrl) {
        this.overrideUrl = overrideUrl;
        return this;
    }

    public WebStrategy setToolbarDivider(@WebStrategyType.ToolbarDivider int toolbarDivider) {
        this.toolbarDivider = toolbarDivider;
        return this;
    }

    public WebStrategy setTitleBold(@WebStrategyType.TitleBold int titleBold) {
        this.titleBold = titleBold;
        return this;
    }

    /**
     * 兼容旧框架
     */
    //旧的js框架支持
    public int oldJsInterface = WebStrategyType.OLD_INTERFACE_ON;
    //旧的url拦截支持
    public int oldIntercept = WebStrategyType.OLD_INTERCEPT_ON;
    //旧的业务收藏功能
    public int oldCollection = WebStrategyType.OLD_COLLECTION_ON;
    //旧的浏览器右上角电话图标
    public int oldPhoneIcon = WebStrategyType.OLD_PHONEICON_OFF;
    //标题栏下拉recycle支持
    public int oldToolBarRecycle = WebStrategyType.OLD_TOOLBAR_RECYCLE_OFF;


    //强制控制状态栏是否显示
    public WebStrategy setStatusBarVisibility(@WebStrategyType.StatusBarVisibility int statusBarVisibility) {
        this.statusBarVisibility = statusBarVisibility;
        return this;
    }

    //设置加载的url
    public WebStrategy setUrl(String urlStr){
        url = urlStr;
        return this;
    }

    //设置状态栏颜色
    public WebStrategy setActivityStartMode(@WebStrategyType.FlagActivity int activityStartModle){
        this.activityStartModle = activityStartModle;
        return this;
    }

    //设置状态栏颜色
    public WebStrategy setStatusBarColor(String statusBarColor){
        this.statusBarColor = statusBarColor;
        return this;
    }

    //设置返回键颜色
    public WebStrategy setBackIconColor(@WebStrategyType.BackIconColor int backIconColor){
        this.backIconColor = backIconColor;
        return this;
    }

    //设置返回键颜色
    public WebStrategy setTitleTextColor(String titleTextColor){
        this.titleTextColor = titleTextColor;
        return this;
    }

    //设置toolbar颜色
    public WebStrategy setToolBarColor(String toolBarColor){
        this.toolBarColor = toolBarColor;
        return this;
    }

    //设置文件选择能力
    public WebStrategy setFileChoose(@WebStrategyType.FileChoose int strategy){
        fileChoose = strategy;
        return this;
    }

    //设置文件浏览能力
    public WebStrategy setFileBrowser(@WebStrategyType.FileBrowser int strategy){
        fileBrowser = strategy;
        return this;
    }

    //是否显示标题栏
    public WebStrategy setToolBarVisibility(@WebStrategyType.ToolBarVisibility int strategy){
        toolBarVisibility = strategy;
        return this;
    }

    //标题(设置标题则显示传入标题，不设置则获取h5标题)
    public WebStrategy setTitle(String title){
        this.title = title;
        return this;
    }

    //是否需要统计web页面加载时长和成功失败
    public WebStrategy setStatistics(@WebStrategyType.Statistics int strategy){
        statistics = strategy;
        return this;
    }

    //加载下拉标题栏recycle
    public WebStrategy setOldToolBarRecycle(@WebStrategyType.OldToolbarRecycle int oldToolBarRecycle) {
        this.oldToolBarRecycle = oldToolBarRecycle;
        return this;
    }

    //设置南通业务收藏
    public WebStrategy setOldCollction(int oldCollection) {
        this.oldCollection = oldCollection;
        return this;
    }

    //设置右侧电话图标
    public WebStrategy setOldPhoneIcon(int oldPhoneIcon) {
        this.oldPhoneIcon = oldPhoneIcon;
        return this;
    }

    //设置旧的文件拦截
    public WebStrategy setOldIntercept(@WebStrategyType.OldIntercept int oldIntercept) {
        this.oldIntercept = oldIntercept;
        return this;
    }

    //设置旧的js交互
    public WebStrategy setOldJsInterface(@WebStrategyType.OldInterface int oldJsInterface) {
        this.oldJsInterface = oldJsInterface;
        return this;
    }

    //toast接口
    public WebStrategy setNativeToast(@WebStrategyType.NativeToast int strategy){
        nativeToast = strategy;
        return this;
    }

    //关闭页面接口
    public WebStrategy setNativeCloseWeb(@WebStrategyType.NativeCloseBrowser int strategy){
        closeWeb = strategy;
        return this;
    }

    //打开浏览器
    public WebStrategy setNativeOpenBrowser(@WebStrategyType.NativeOpenBrowser int strategy){
        openBrowser = strategy;
        return this;
    }

    //gps接口
    public WebStrategy setNativeGps(@WebStrategyType.NativeGps int strategy){
        nativeGps = strategy;
        return this;
    }

    //打印日志
    public WebStrategy setNativeLog(@WebStrategyType.NativeLog int strategy){
        nativeLog = strategy;
        return this;
    }

    public WebStrategy setIsHideProgressBar(@WebStrategyType.ProgressHide int strategy){
        isHideProgressBar = strategy;
        return this;
    }

    public WebStrategy setMainPageModule(@WebStrategyType.MainPageModule int strategy){
        mainPageModule = strategy;
        return this;
    }
}
