package com.pasc.lib.hybrid.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.pasc.lib.net.ApiGenerator;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * 功能：
 * <p>
 * 下载网络图片
 */
public class PictureBiz {


    /**
     * 获取图片流
     *
     * @param dataUrl
     */
    public static Flowable<Bitmap> getDownloadPictures(String dataUrl) {
        if (TextUtils.isEmpty(dataUrl)) {
            dataUrl = "";
        }
        return ApiGenerator.createApi(Api.class)
                .getPicureImage(dataUrl)
                .map(new Function<ResponseBody,Bitmap >(){
                    @Override
                    public Bitmap apply(ResponseBody res) throws Exception {
                        return BitmapFactory.decodeStream(res.byteStream());
                    }
                })
                .toFlowable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
