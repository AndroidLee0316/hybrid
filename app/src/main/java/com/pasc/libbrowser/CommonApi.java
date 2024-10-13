package com.pasc.libbrowser;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface CommonApi {
  /**
   * 文件下载
   */
  @Streaming @GET Observable<ResponseBody> loadFile(@Url String fileUrl);
}
