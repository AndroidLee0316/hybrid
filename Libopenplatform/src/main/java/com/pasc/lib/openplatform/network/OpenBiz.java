package com.pasc.lib.openplatform.network;

import android.util.Log;

import com.pasc.lib.net.ApiGenerator;
import com.pasc.lib.net.resp.VoidObject;
import com.pasc.lib.openplatform.address.AddressResp;
import com.pasc.lib.openplatform.pamars.AppidPamars;
import com.pasc.lib.openplatform.pamars.AuthSelectRequestCodeParam;
import com.pasc.lib.openplatform.resp.AuthSelectRequestCodeResp;
import com.pasc.lib.openplatform.resp.CheckInitCodeResp;
import com.pasc.lib.openplatform.resp.DataSecretaryDetailResp;
import com.pasc.lib.openplatform.resp.DataSecretaryList;
import com.pasc.lib.openplatform.resp.DataSecretaryResp;
import com.pasc.lib.openplatform.resp.OpenIdResp;
import com.pasc.lib.openplatform.resp.RequestCodeResp;
import com.pasc.lib.openplatform.resp.ServiceInfoResp;
import com.pasc.lib.openplatform.resp.ServiceStatusResp;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

/**
 * 功能：
 * <p>
 * created by zoujianbo345
 * data : 2018/9/18
 */
public class OpenBiz {
    /**
     * 获取第三方服务信息
     */
    public static Single<ServiceInfoResp> getServiceInfo(String appid){
        AppidPamars params = new AppidPamars(appid);
       return ApiGenerator.createApi (OpenApi.class)
                .getServicesInfo (UrlManager.GET_SERVICE_INFO,params)
               .compose (TransformUtil.getTransformer ())
               .subscribeOn (Schedulers.io ())
               .observeOn (AndroidSchedulers.mainThread ());
    }

    /**
     * JSSDK初始化校验code
     */
    public static Single<CheckInitCodeResp> checkCode(String appid, String initCode){
        AppidPamars params = new AppidPamars(appid, initCode);
        return ApiGenerator.createApi (OpenApi.class)
                .checkInitCode (UrlManager.CHECK_CODE,params)
                .compose (TransformUtil.getTransformer ())
                .subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ());
    }

    /**
     * 获取openID
     */
    public static  Single<OpenIdResp> getOpenId(String appid
            , String token){
        AppidPamars params = new AppidPamars(appid, token, 0);
        return ApiGenerator.createApi (OpenApi.class)
                .getOpenId (UrlManager.GET_OPENID,params)
                .compose (TransformUtil.getTransformer ())
                .subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ());
    }

    /**
     * 获取openID
     */
    public static  Single<RequestCodeResp> getResquestCode(String appid
        , String token, List<String> dataTypes){
        AppidPamars params = new AppidPamars(appid, token, 0,dataTypes);
        return ApiGenerator.createApi (OpenApi.class)
            .getRequestCode (UrlManager.GET_REQUEST_CODE,params)
            .compose (TransformUtil.getTransformer ())
            .subscribeOn (Schedulers.io ())
            .observeOn (AndroidSchedulers.mainThread ());
    }

    /**
     * 获取requestCode
     */
    public static Single<RequestCodeResp> getResquestCode(String appid, String token){
        AppidPamars params = new AppidPamars(appid, token, 0);
        return ApiGenerator.createApi (OpenApi.class)
                .getRequestCode (UrlManager.GET_REQUEST_CODE,params)
                .compose (TransformUtil.getTransformer ())
                .subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ());
    }

    /**
     * 获取第三方服务状态
     */
    public static Single<ServiceStatusResp> getServiceStatus(String appid, String token){
        AppidPamars params = new AppidPamars(appid, token, 0);
        return ApiGenerator.createApi (OpenApi.class)
                .getServiceStatus (UrlManager.GET_SERVICE_STATUS,params)
                .compose (TransformUtil.getTransformer ())
                .subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ());
    }

    //------------------法人-------------------
    /**
     * 获取法人用户授权信息
     */
    public static Single<ServiceStatusResp> getCorporateAuthInfo(String appid,String token){
        AppidPamars params = new AppidPamars(appid,token,0);
        return ApiGenerator.createApi (OpenApi.class)
                .getCorporateAuthInfo (UrlManager.GET_CORPORATE_AUTH_INFO,params)
                .compose (TransformUtil.getTransformer ())
                .subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ());
    }

    /**
     * 获取openID
     */
    public static  Single<OpenIdResp> getCorporateOpenId(String appid
            , String token){
        AppidPamars params = new AppidPamars(appid, token, 0);
        return ApiGenerator.createApi (OpenApi.class)
                .getCorporateOpenId (UrlManager.GET_CORPORATE_OPENID,params)
                .compose (TransformUtil.getTransformer ())
                .subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ());
    }

    /**
     * 获取requestCode
     */
    public static Single<RequestCodeResp> getCorporateRequestCode(String appid, String token){
        AppidPamars params = new AppidPamars(appid, token, 0);
        return ApiGenerator.createApi (OpenApi.class)
                .getCorporateRequestCode (UrlManager.GET_CORPORATE_REQUEST_CODE,params)
                .compose (TransformUtil.getTransformer ())
                .subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ());
    }


    /**
     * 数据秘书展示列表
     */
    public static Single<DataSecretaryList> getDataSecretaryList(String token){
        AppidPamars params = new AppidPamars();
        params.token= token;
        return ApiGenerator.createApi (OpenApi.class)
                .getDataSecretaryList (UrlManager.GET_DATA_SECRETARY_LIST,params)
                .compose (TransformUtil.getTransformer ())
                .subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ());
    }

    /**
     * 数据秘书展示详情
     */
    public static Single<DataSecretaryDetailResp> getDataSecretaryDetail(String appid,String token){
        AppidPamars params = new AppidPamars();
        params.appId = appid;
        params.token = token;
        return ApiGenerator.createApi (OpenApi.class)
                .getDataSecretaryDetail (UrlManager.GET_DATA_SECRETARY_DETAIL,params)
                .compose (TransformUtil.getTransformer ())
                .subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ());
    }

    /**
     * 数据秘书取消权限
     */
    public static Single<VoidObject> dataSecretaryAuthCancel(String appid, String token){
        AppidPamars params = new AppidPamars();
        params.appId = appid;
        params.token = token;
        return ApiGenerator.createApi (OpenApi.class)
                .dataSecretaryAuthCancel (UrlManager.DATA_CANCEL_AUTH,params)
                .compose (TransformUtil.getTransformer ())
                .subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ());
    }



    /**
     * 获取openID
     */
    public static  Single<AuthSelectRequestCodeResp> getAuthSelectResquestCode(String appid
            , String token, String userDataType, AddressResp authData){
        AuthSelectRequestCodeParam authSelectRequestCodeParam = new AuthSelectRequestCodeParam();
        authSelectRequestCodeParam.appId = appid;
        authSelectRequestCodeParam.token = token;
        authSelectRequestCodeParam.userDataType = userDataType;
        authSelectRequestCodeParam.authData = authData;
        return ApiGenerator.createApi (OpenApi.class)
                .getAuthSelectRequestCode (UrlManager.GET_AUTH_SELECT_REQUEST_CODE,authSelectRequestCodeParam)
                .compose (TransformUtil.getTransformer ())
                .subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ());
    }


}