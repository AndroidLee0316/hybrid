package com.pasc.lib.openplatform.network;

import com.pasc.lib.net.resp.BaseResp;
import com.pasc.lib.net.resp.VoidObject;
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

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * @date 2019/1/18
 * @des 开发平台网络接口
 * @modify
 **/
public interface OpenApi {

    /**
     * 获取第三方服务信息
     */
    @POST
    @Headers("Content-Type:application/json")
    Single<BaseResp<ServiceInfoResp>> getServicesInfo(@Url String url, @Body AppidPamars body);

    /**
     * JSSDK初始化校验code
     */
    @POST
    @Headers("Content-Type:application/json")
    Single<BaseResp<CheckInitCodeResp>> checkInitCode(@Url String url, @Body AppidPamars body);

    /**
     * 获取openID
     */
    @POST
    @Headers("Content-Type:application/json")
    Single<BaseResp<OpenIdResp>> getOpenId(@Url String url, @Body AppidPamars body);

    /**
     * 获取requestCode
     */
    @POST
    @Headers("Content-Type:application/json")
    Single<BaseResp<RequestCodeResp>> getRequestCode(@Url String url, @Body AppidPamars body);

    /**
     * 获取第三方服务状态
     */
    @POST
    @Headers("Content-Type:application/json")
    Single<BaseResp<ServiceStatusResp>> getServiceStatus(@Url String url, @Body AppidPamars body);



    //-------------法人--------------
    /**
     * 获取法人用户授权信息
     */
    @POST
    @Headers("Content-Type:application/json")
    Single<BaseResp<ServiceStatusResp>> getCorporateAuthInfo(@Url String url, @Body AppidPamars body);

    /**
     * 获取openID
     */
    @POST
    @Headers("Content-Type:application/json")
    Single<BaseResp<OpenIdResp>> getCorporateOpenId(@Url String url, @Body AppidPamars body);

    /**
     * 获取requestCode
     */
    @POST
    @Headers("Content-Type:application/json")
    Single<BaseResp<RequestCodeResp>> getCorporateRequestCode(@Url String url, @Body AppidPamars body);


    /**
     * 数据秘书展示列表
     */
    @POST
    @Headers("Content-Type:application/json")
    Single<BaseResp<DataSecretaryList>> getDataSecretaryList(@Url String url, @Body AppidPamars body);

    /**
     * 数据秘书展示详情
     */
    @POST
    @Headers("Content-Type:application/json")
    Single<BaseResp<DataSecretaryDetailResp>> getDataSecretaryDetail(@Url String url, @Body AppidPamars body);

    /**
     * 数据秘书取消权限
     */
    @POST
    @Headers("Content-Type:application/json")
    Single<BaseResp<VoidObject>> dataSecretaryAuthCancel(@Url String url, @Body AppidPamars body);

    /**
     * 获取每次授权授权码
     */
    @POST
    @Headers("Content-Type:application/json")
    Single<BaseResp<AuthSelectRequestCodeResp>> getAuthSelectRequestCode(@Url String url, @Body AuthSelectRequestCodeParam body);

}
