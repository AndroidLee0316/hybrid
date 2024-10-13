package com.pasc.libbrowser.net;


import com.pasc.lib.net.resp.BaseResp;

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
public interface AppApi {
    @POST
    @Headers("Content-Type:application/json")
    Single<BaseResp<UserInfoBean>>login(@Url String url, @Body LoginBean loginBean);
}
