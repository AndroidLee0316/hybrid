package com.pasc.lib.openplatform.network;

import android.content.Context;
import android.text.TextUtils;

import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.openplatform.OpenPlatformProvider;
import com.pasc.lib.openplatform.PascOpenPlatform;
import com.pasc.libopenplatform.BuildConfig;

/**
 * 功能：
 * <p>
 * created by zoujianbo345
 * data : 2018/9/17
 */
public class UrlManager {
    public static final String APP_ID = "av8eZ4pK81cTQxsO72ZcRR1vMxwaZxc0";
    public static final String BETA_DOMAIN = "http://ntgsc-smt-stg2.pingan.com.cn:8080";
    public static final String PRODUCT_DOMAIN = "https://ntgsc-smt.pingan.com.cn";
    public static final String PRODUCT_HOST = PRODUCT_DOMAIN + "/nantongsmt";
    public static final String BETA_HOST = BETA_DOMAIN + "/nantongsmt";
    public static String API_HOST = BuildConfig.DEBUG ? PRODUCT_HOST : PRODUCT_HOST; // api的域名

    public static void init() {
        setOpenPlatformBaseUrl();
        GET_SERVICE_INFO = API_HOST + "/openPlatform/services/getServicesInfo.do";
        CHECK_CODE = API_HOST + "/openPlatform/initCode/checkInitCode.do";
        GET_OPENID = API_HOST + "/openPlatform/open/getOpenId.do";
        GET_REQUEST_CODE = API_HOST + "/openPlatform/request/getRequestCode.do";
        GET_SERVICE_STATUS = API_HOST + "/openPlatform/userInfo/getServiceUserInfo.do";

        GET_CORPORATE_AUTH_INFO = API_HOST + "/openPlatform/corporate/getCorporateAuthInfo.do";
        GET_CORPORATE_OPENID = API_HOST + "/openPlatform/corporate/getCorporateOpenId.do";
        GET_CORPORATE_REQUEST_CODE = API_HOST + "/openPlatform/corporate/getCorporateRequestCode.do";
        GET_DATA_SECRETARY_LIST = API_HOST + "/openPlatform/userDataTypeAuth/queryDataSecretary";
        GET_DATA_SECRETARY_DETAIL = API_HOST + "/openPlatform/userDataTypeAuth/queryDataSecretaryDetail";
        DATA_CANCEL_AUTH = API_HOST + "/openPlatform/userDataTypeAuth/modifyAuth";

        GET_AUTH_SELECT_REQUEST_CODE =  API_HOST + "/openPlatform/everyTime/getEveryTimeRequestCode.do";
    }

    /**
     * 设置开放平台baseUrl
     */
    private static void setOpenPlatformBaseUrl() {
        if (null == PascHybrid.getInstance().getHybridInitConfig()) {
            throw new NullPointerException("HybridOptions is null !!!");
        }
        OpenPlatformProvider openPlatformProvider =
                PascOpenPlatform.getInstance().getOpenPlatformProvider();
        if (null != openPlatformProvider && !TextUtils.isEmpty(
                openPlatformProvider.getOpenPlatformBaseUrl())) {
            API_HOST = openPlatformProvider.getOpenPlatformBaseUrl();
        }
    }

    /**
     * 第三方服务信息查询
     */
    public static String GET_SERVICE_INFO;
    /**
     * JSSDK  初始化 校验code
     */
    public static String CHECK_CODE;
    /**
     * 获取openId
     */
    public static String GET_OPENID;

    /**
     * 获取requestcode
     */
    public static String GET_REQUEST_CODE;
    /**
     * 获取第三方授权状态
     */
    public static String GET_SERVICE_STATUS;

    /**
     * 获取法人用户授权信息
     */
    public static String GET_CORPORATE_AUTH_INFO;
    /**
     * 获取法人用户openId
     */
    public static String GET_CORPORATE_OPENID;

    /**
     * 获取法人用户的requestCode
     */
    public static String GET_CORPORATE_REQUEST_CODE;

    public static String GET_DATA_SECRETARY_LIST;

    public static String GET_DATA_SECRETARY_DETAIL;

    public static String DATA_CANCEL_AUTH;

    public static String GET_AUTH_SELECT_REQUEST_CODE;



    public static void initNet(Context context, String baseUrl) {
        //        NetConfig config = new NetConfig.Builder(context)
        //                .baseUrl(baseUrl)
        ////                          .headers(HeaderUtil.getHeaders(BuildConfig.DEBUG))
        //                .gson(NetManager.getConvertGson())
        //                .isDebug(BuildConfig.DEBUG)
        //                .build();
        //        NetManager.init(config);
    }
}
