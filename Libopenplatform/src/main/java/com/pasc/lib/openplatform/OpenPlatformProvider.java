package com.pasc.lib.openplatform;

import android.content.Context;
import android.support.annotation.ColorInt;

import com.pasc.lib.hybrid.webview.PascWebView;

import java.util.List;

/**
 * create by wujianning385 on 2018/10/16.
 */
public abstract class OpenPlatformProvider {

    /**
     * 开放平台baseURL
     *
     * @return
     */
    public abstract String getOpenPlatformBaseUrl();

    /**
     * 开放平台需要获取userToken
     */
    public abstract void getUserToken(IBizCallback iBizCallback);

    /**
     * 开放平台法人需要获取corporateuserToken
     *
     * @return
     */
    public void getCorporateToken(IBizCallback iBizCallback){

    }

    /**
     * 开放平台需要实名认证的回调
     *
     * @return
     */
    public abstract void getCertification(Context context, CertificationCallback certificationCallback);

    /**
     * 开放平台动态注册行为回调
     *
     * @return
     */
    public abstract void openPlatformBehavior(PascWebView pascWebView, List<String> nativeApis);

    /**
     * 开放平台内部错误
     * 比如网络请求错误等
     *
     * @param code
     * @param msg
     */
    public abstract void onOpenPlatformError(int code, String msg);

    /**
     * 开放平台法人内部错误
     * 比如网络请求错误等
     *
     * @param code
     * @param msg
     */
    public void onCorporateOpenPlatformError(int code, String msg){

    }

    /**
     * 提供应用图标
     *
     * @return
     */
    public abstract int getAppIcon();

    /**
     * 开放平台主题色
     * @return
     */
    public abstract int getStyleColor();

    /**
     * 开放平台授权页返回键颜色
     *
     * @return
     */
    public abstract @ColorInt int getBackIconColor();

    public void authClick(boolean isAuth,String serviceName,String url){

    }

}
