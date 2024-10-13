package com.pasc.lib.hybrid.nativeability;

import android.support.annotation.IntDef;

public class WebStrategyType {
    //状态栏是否强制显示
    @IntDef({STATUSBAR_VISIBLE, STATUSBAR_GONE,STATUSBAR_FOLLOWING})
    public @interface StatusBarVisibility {
    }
    public static final int STATUSBAR_GONE = 0;      //强制隐藏
    public static final int STATUSBAR_VISIBLE = 1;   //强制显示
    public static final int STATUSBAR_FOLLOWING = 2; //跟随app设置

    //返回键颜色
    @IntDef({BACKICONCOLOR_BLUE,BACKICONCOLOR_WHITE,BACKICONCOLOR_BLACK})
    public @interface BackIconColor {
    }
    public static final int BACKICONCOLOR_BLUE = 0;
    public static final int BACKICONCOLOR_WHITE = 1;
    public static final int BACKICONCOLOR_BLACK = 2;

    //旧交互支持
    @IntDef({OLD_INTERFACE_OFF, OLD_INTERFACE_ON})
    public @interface OldInterface {
    }
    public static final int OLD_INTERFACE_OFF = 0;
    public static final int OLD_INTERFACE_ON = 1;

    //旧拦截支持
    @IntDef({OLD_INTERCEPT_OFF, OLD_INTERCEPT_ON})
    public @interface OldIntercept {
    }
    public static final int OLD_INTERCEPT_OFF = 0;
    public static final int OLD_INTERCEPT_ON = 1;

    //旧收藏支持
    @IntDef({OLD_COLLECTION_OFF, OLD_COLLECTION_ON})
    public @interface OldCollection {
    }
    public static final int OLD_COLLECTION_OFF = 0;
    public static final int OLD_COLLECTION_ON = 1;

    //旧的右上角电话图标
    @IntDef({OLD_PHONEICON_OFF, OLD_PHONEICON_ON})
    public @interface OldPhoneIcon {
    }
    public static final int OLD_PHONEICON_OFF = 0;
    public static final int OLD_PHONEICON_ON = 1;

    //toolbar下拉recycle
    @IntDef({OLD_TOOLBAR_RECYCLE_OFF, OLD_TOOLBAR_RECYCLE_ON})
    public @interface OldToolbarRecycle {
    }
    public static final int OLD_TOOLBAR_RECYCLE_OFF = 0;
    public static final int OLD_TOOLBAR_RECYCLE_ON = 1;

    //文件选择能力
    @IntDef({FILECHOOSE_OFF, FILECHOOSE_ON})
    public @interface FileChoose {
    }
    public static final int FILECHOOSE_OFF = 0;
    public static final int FILECHOOSE_ON = 1;

    //浏览文件能力
    @IntDef({FILEBROWSER_OFF, FILEBROWSER_ON})
    public @interface FileBrowser {
    }
    public static final int FILEBROWSER_OFF = 0;
    public static final int FILEBROWSER_ON = 1;

    //是否显示标题栏
    @IntDef({TOOLBAR_VISIBLE, TOOLBAR_GONE})
    public @interface ToolBarVisibility {
    }
    public static final int TOOLBAR_VISIBLE = 0;
    public static final int TOOLBAR_GONE = 1;

    //是否开启统计
    @IntDef({STATISTICS_ON, STATISTICS_OFF})
    public @interface Statistics {
    }
    public static final int STATISTICS_OFF = 0;
    public static final int STATISTICS_ON = 1;

    //native开放toast给js的调用
    @IntDef({NATIVE_TOAST_ON, NATIVE_TOAST_OFF})
    public @interface NativeToast {
    }
    public static final int NATIVE_TOAST_OFF = 0;
    public static final int NATIVE_TOAST_ON = 1;
    public static final int NATIVE_TOAST_SELF_DEFINED = 2;

    //native开放gps数据给js的调用
    @IntDef({NATIVE_GPS_ON, NATIVE_GPS_OFF})
    public @interface NativeGps {
    }
    public static final int NATIVE_GPS_OFF = 0;
    public static final int NATIVE_GPS_ON = 1;

    //native关闭浏览器页面
    @IntDef({NATIVE_CLOSE_BROWSER_ON, NATIVE_CLOSE_BROWSER_OFF})
    public @interface NativeCloseBrowser {
    }
    public static final int NATIVE_CLOSE_BROWSER_OFF = 0;
    public static final int NATIVE_CLOSE_BROWSER_ON = 1;

    //native打开浏览器
    @IntDef({NATIVE_OPEN_BROWSER_ON, NATIVE_OPEN_BROWSER_OFF})
    public @interface NativeOpenBrowser {
    }
    public static final int NATIVE_OPEN_BROWSER_OFF = 0;
    public static final int NATIVE_OPEN_BROWSER_ON = 1;

    //native打印日志
    @IntDef({NATIVE_LOG_ON, NATIVE_LOG_OFF})
    public @interface NativeLog {
    }
    public static final int NATIVE_LOG_OFF = 0;
    public static final int NATIVE_LOG_ON = 1;

    //获取设备信息
    @IntDef({DEVICE_INFO_ON, DEVICE_INFO_OFF})
    public @interface DeviceInfo {
    }
    public static final int DEVICE_INFO_OFF = 0;
    public static final int DEVICE_INFO_ON = 1;


    //新栈启动
    @IntDef({FLAG_ACTIVITY_NORMAL,FLAG_ACTIVITY_NEW_TASK})
    public @interface FlagActivity {
    }
    public static final int FLAG_ACTIVITY_NORMAL = 0;
    public static final int FLAG_ACTIVITY_NEW_TASK = 1;

    //标题栏下划线
    @IntDef({TOOLBAR_DIVIDER_VISIBLE,TOOLBAR_DIVIDER_GONE})
    public @interface ToolbarDivider {
    }
    public static final int TOOLBAR_DIVIDER_VISIBLE = 0;
    public static final int TOOLBAR_DIVIDER_GONE = 1;

    //标题加粗
    @IntDef({TITLT_NORMAL,TITLT_BOLD})
    public @interface TitleBold {
    }
    public static final int TITLT_NORMAL = 0;
    public static final int TITLT_BOLD = 1;

    //关闭按钮显示逻辑
    @IntDef({CLOSEBUTTON_ALWAKES_VISIBLE,CLOSEBUTTON_ALWAKES_GONE,CLOSEBUTTON_FRISTPAGE_GONE})
    public @interface CloseButton {
    }
    public static final int CLOSEBUTTON_ALWAKES_VISIBLE = 0;
    public static final int CLOSEBUTTON_ALWAKES_GONE = 1;
    public static final int CLOSEBUTTON_FRISTPAGE_GONE = 2;

    //拦截shouldoverride给外界
    @IntDef({OVERRIDEURL_OFF,OVERRIDEURL_ON})
    public @interface ShouldOverride {
    }
    public static final int OVERRIDEURL_OFF = 0;
    public static final int OVERRIDEURL_ON = 1;

    //关闭按钮样式
    public static final int CLOSEBUTTON_TEXT = 0;
    public static final int CLOSEBUTTON_IMAGE = 1;

    //progressbar是否隐藏
    @IntDef({PROGRESS_HIDE, PROGRESS_SHOW})
    public @interface ProgressHide {
    }
    public static final int PROGRESS_SHOW = 0;   //显示
    public static final int PROGRESS_HIDE = 1;   //隐藏

    @IntDef({NORMALPAGE, MAINPAGE})
    public @interface MainPageModule {
    }
    public static final int NORMALPAGE = 0; //正常网页
    public static final int MAINPAGE = 1;   //在首页加载的网页

}
