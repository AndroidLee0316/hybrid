package com.pasc.lib.hybrid.behavior;

// 注意这里只保存 hybird方案的协议名
public class ConstantBehaviorName {
    public static final String DEFAULT_HANDLER = "PASC.app.default";

    public static final String ACTION_BAR_L_BTN = "PASC.app.";
    public static final String WEB_CONTROL_BACK = "PASC.app.webControlBackItem";


    /** -------------------------通用交互-Web发起 register 的方式 start------------------------------*/

    /**
     * 是否支持交互API(多个查询)
     */
    public static final String SUPPORT_APIS = "PASC.app.isExistApis";

    /**
     * 是否支持交互API(单个查询)
     */
    public static final String SUPPORT_API = "PASC.app.isExistApi";

    /**
     * 打开一个新的WebView并加载网页
     */
    public static final String OPEN_NEW_WEBVIEW = "PASC.app.openNewWebView";

    /**
     * 由web来控制返回按钮的点击事件
     */
    public static final String CONTROLL_BACKPRESS = "PASC.app.webControlBackItem";

    /**
     * 直接退出当前WebView
     */
    public static final String CLOSE_WEBVIEW = "PASC.app.close";

    /**
     * 退出所有WebView
     */
    public static final String CLOSE_ALL_WEBVIEW = "PASC.app.closeAllWebview";

    /**
     * 返回上一个网页
     */
    public static final String WEBVIEW_GO_BACK = "PASC.app.goback";

    /**
     * 导航栏定制
     */
    public static final String CONTROLL_TOOLBAR = "PASC.app.setToolBar";

    /**
     * Toast
     */
    public static final String TOAST = "PASC.app.toast";

    /**
     * 跳转回到根页面
     */
    public static final String BACK_TO_ROOT_VIEW = "PASC.app.backToRootView";

    /**
     * 切换到第N个Tab页面,主页面的
     */
    public static final String CLOSE_WITH_BACK_HOME = "PASC.app.closeWithBackHome";

    /**
     * 预览图片
     */
    public static final String PREVIEW_IMAGE = "PASC.app.previewImages";

    /**
     * App地图导航
     */
    public static final String OPEN_MAP_NAVIGATION = "PASC.app.mapNavigation";

    /**
     * 埋点：页面统计
     */
    public static final String STATISTICS_PAGE = "PASC.app.webStatsPage";

    /**
     * 埋点：事件统计
     */
    public static final String STATISTICS_EVENT = "PASC.app.webStatsEvent";

    /**
     * 分享
     */
    public static final String OPEN_SHARE = "PASC.app.share";


    /**
     * 日志打印
     */
    public static final String LOG_EVENT = "PASC.app.log";

    /**
     * 获取设备信息
     */
    public static final String GET_DEVICE_INFO = "PASC.app.getDeviceInfo";

    /**
     * BoxTips（toast，loading弹窗等）
     */
    public static final String BOX_TIPS = "PASC.app.boxTips";

    /**
     * 拍照
     */
    public static final String TAKE_PHOTO = "PASC.app.takePhoto";

    /**
     * 相册
     */
    public static final String OPEN_ALBUM = "PASC.app.photoAlbum";

    /**
     * 选择照片
     */
    public static final String SELECT_PHOTO = "PASC.app.selectPhoto";

    /**
     * 选择联系人
     */
    public static final String OPEN_CONTACT = "PASC.app.selectContact";

    /**
     * 当前位置GPS信息
     */
    public static final String GET_GPS_INFO = "PASC.app.getGpsInfo";

    /**
     * 扫描二维码
     */
    public static final String QR_CODE_SCAN = "PASC.app.openQRCode";

    /**
     * 内存缓存
     */
    public static final String MEMORY_CACHE = "PASC.app.memoryCache";

    /**
     * 磁盘缓存
     */
    public static final String DISK_CACHE = "PASC.app.diskCache";

    /**
     * 已安装的分享平台
     */
    public static final String SUPPROT_SHARE_PLATFORM = "PASC.app.installedSharePlatforms";

    /**
     * 获取网络状态
     */
    public static final String NETWORK_STATUS = "PASC.app.networkStatus";

    /**
     * 打电话功能
     */
    public static final String CALL_PHONE = "PASC.app.callPhone";

    /**
     * Web回调
     * <p>
     * 场景：列表页为原生，详情页为webview，web做了某些动作后需要给列表页一个回调，让列表进行刷新
     */
    public static final String WEB_CALLBACK = "PASC.app.webCallback";

    /**
     * web广播
     */
    public static final String BROADCAST = "PASC.app.broadcast";
    /**
     * 发短信
     */
    public static final String SEND_SMS = "PASC.app.sendSMS";
    /**
     * 客户端路由
     */
    public static final String NATIVI_ROUTE = "PASC.app.nativeRoute";
    /**
     * 获取用户信息
     */
    public static final String GET_USERINFO = "PASC.app.getUserInfo";
    /**
     * h5 token失效
     */
    public static final String H5_TOKEN_INVALID = "PASC.app.h5TokenInvalid";
    /**
     * 弹起原生键盘
     */
    public static final String OPEN_KEYBOARD = "PASC.app.openKeyboard";
    /**
     * 退出登陆
     */
    public static final String LOGOUT = "PASC.app.logout";
    /**
     * 办事流程结束
     */
    public static final String BROAD_CAST = "PASC.app.broadcast";
    /**
     * 设置webviewui
     */
    public static final String WEBVIEW_UI = "PASC.app.setWebView";
    /**
     * 弹框
     */
    public static final String ALERT = "PASC.app.alert";

    public static final String WEB_BEHAVIOR_BROWSE_FILE = "PASC.app.browseFile";

    public static final String WEB_BEHAVIOR_OPEN_SETTING = "PASC.app.openSetting";

    public static final String WEB_BEHAVIOR_PREVIEW_IMAGES = "PASC.app.previewImages";

    public static final String INIT_JSSDK = "PASC.app.initJSSDK";

    public static final String USER_AUTH = "PASC.app.userAuth";

    /**
     * 用户地址列表授权
     */
    public static final String USER_ADDRESS_AUTH = "PASC.app.userAddressAuth";

    public static final String SYSTEM_SHARE = "PASC.app.systemShare";

    public static final String OPEN_LOCATION = "PASC.app.openLocation";

    public static final String CHOOSEIMAGE = "PASC.app.chooseImage";


    /**
     * 选择视频
     */
    public static final String CHOOSE_VIDEO = "PASC.app.chooseVideo";

    /**
     * 播放视频
     */
    public static final String PLAY_VIDEO = "PASC.app.playVideo";

    /**
     * 法人授权
     */
    public static final String ENTERPRISE_USER_AUTH = "PASC.app.enterpriseUserAuth";


    /** -------------------------通用交互-Web发起 register 的方式 end------------------------------*/


    /** -------------------------通用交互-客户端发起 callHandler 的方式 start------------------------*/

    /**
     * 进入WebView
     */
    public static final String CALL_ENTER_WEBVIEW = "PASC.web.startPage";

    /**
     * 退出WebView
     */
    public static final String CALL_CLOSE_WEBVIEW = "PASC.web.finishPage";

    public static final String CALL_EXIT_APP = "PASC.web.exitApp";

    public static final String CALL_ENTER_APP = "PASC.web.enterApp";


    /** -------------------------通用交互-客户端发起 callHandler 的方式 end------------------------*/

    ///** -------------------------业务交互-Web发起 register 的方式 start------------------------*/
    //
    ///**
    // * 获取当前用户信息
    // */
    //public static final String GET_USER_INFO = "PASC.app.getUserInfo";
    //
    ///**
    // * 登录
    // */
    //public static final String OPEN_LOGIN = "PASC.app.login";
    //
    ///**
    // * 获取用户验证信息
    // */
    //public static final String GET_AUTH_INFO = "PASC.app.getAuthInfo";
    //
    ///**
    // * 实名认证
    // */
    //public static final String REAL_NAME_AUTH = "PASC.app.auth";
    //
    ///** -------------------------业务交互-Web发起 register 的方式 end------------------------*/

    /** -------------------------开放平台动态注册的交互------------------------------*/

    /**
     * 开放平台动态注册电话
     */
    public static final String OP_PHONE_CALL = "PASC.app.PhoneCall";

    /**
     * 开放平台动态注册通知
     */
    public static final String OP_NOTIFICATION = "PASC.app.Notification";

    /**
     * 开放平台动态注册发短信
     */
    public static final String OP_SMS = "PASC.app.SMS";

    /**
     * 开放平台动态注册联系人
     */
    public static final String OP_CONTACT = "PASC.app.Contact";

    /**
     * 开放平台动态注册GPS
     */
    public static final String OP_GPS = "PASC.app.GPS";

    /**
     * 开放平台动态注册导航
     */
    public static final String OP_NAVIGATION = "PASC.app.Navigation";

    /**
     * 开放平台动态注册扫描二维码
     */
    public static final String OP_QRCODE = "PASC.app.QRCode";

    /**
     * 开放平台动态注册路由
     */
    public static final String OP_ROUTER = "PASC.app.Router";

    /**
     * 开放平台动态注册获取用户信息
     */
    public static final String OP_USERINFO = "PASC.app.UserInfo";

    /**
     * 开放平台动态注册获取用户信息
     */
    public static final String OP_LOCATION = "PASC.app.OpenLocation";
    /**
     * 开放平台选择图片
     */
    public static final String OP_CHOOSEIMAGE = "PASC.app.ChooseImage";
    /**
     * 开放平台选择视频
     */
    public static final String OP_CHOOSEVIDEO = "PASC.app.ChooseVideo";

    /**
     * 录音能力
     */
    public static final String AUDIO_RECORD = "PASC.app.audioRecord";
}
