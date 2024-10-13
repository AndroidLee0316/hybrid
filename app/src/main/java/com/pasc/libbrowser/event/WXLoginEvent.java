package com.pasc.libbrowser.event;


/**
 * 微信登陆事件
 */
public class WXLoginEvent {

    public static int WX_Login_Event_Success = 0x100;//登录页面
    public static int WX_Bind_Event_Success = 0x101;//快速绑定页面
    public static int WX_Bind_Third_Part = 0x102;//第三方账号绑定页面

    public int status;
    public String code;

    public WXLoginEvent(int status, String code) {
        this.status = status;
        this.code = code;
    }

}
