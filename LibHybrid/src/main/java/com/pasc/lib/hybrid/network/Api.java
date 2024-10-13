package com.pasc.lib.hybrid.network;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 功能：
 * <p>
 */
public interface Api {

    @Streaming
    @GET
    Single<ResponseBody> getPicureImage (@Url String url);


}
