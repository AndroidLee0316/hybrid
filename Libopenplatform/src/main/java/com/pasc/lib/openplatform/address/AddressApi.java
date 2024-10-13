package com.pasc.lib.openplatform.address;


import com.pasc.lib.net.resp.BaseResp;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * @date 2019/1/21
 * @des
 * @modify
 **/
public interface AddressApi {
    @POST
    @Headers("Content-Type:application/json")
    Single<BaseResp<List<AddressResp>>>getAddressList(@Url String url, @Body AddressParam addressParam);
}
