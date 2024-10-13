package com.pasc.lib.openplatform.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.pasc.lib.net.ApiError;
import com.pasc.lib.net.ApiV2Error;
import com.pasc.lib.net.ErrorCode;
import com.pasc.lib.net.NetManager;
import com.pasc.lib.net.resp.BaseResp;
import com.pasc.lib.net.resp.BaseV2Resp;
import com.pasc.lib.net.transform.NetV2ObserverManager;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author yangzijian
 * @date 2019/1/18
 * @des
 * @modify
 **/
public class TransformUtil {
    public static RespTransformer getTransformer(){
        return RespTransformer.newInstance ();
    }

    private static class RespTransformer<T> implements SingleTransformer<BaseResp<T>, T> {
        @Override
        public SingleSource<T> apply(Single<BaseResp<T>> upstream) {
            return upstream.doOnSubscribe(new Consumer<Disposable> () {
                @Override
                public void accept(@NonNull Disposable disposable) throws Exception {
                    NetManager netManager = NetManager.getInstance();
                    if (!isNetworkAvailable(netManager.globalConfig.context)) {
                        throw new ApiError (ErrorCode.ERROR, "当前网络不佳，请稍后重试");
                    }
                }
            })
                    .subscribeOn(Schedulers.io())
                    .flatMap(new Function<BaseResp<T>, SingleSource<? extends T>> () {
                        @Override
                        public SingleSource<? extends T> apply(@NonNull BaseResp<T> baseResp)
                                throws Exception {
                            int code = baseResp.code;
                            if (code == ErrorCode.SUCCESS) {
                                T t = baseResp.data;
                                return Single.just(t);
                            } else {
//                                BaseV2Resp<T> baseV2Resp = new BaseV2Resp<>();
//                                baseV2Resp.code = String.valueOf(code);
//                                baseV2Resp.msg = baseResp.msg;
//                                baseV2Resp.data = baseResp.data;
//                                NetV2ObserverManager.getInstance().notifyObserver(baseV2Resp);
//                                if (baseV2Resp.data != null) {
//                                    throw new ApiV2Error(baseV2Resp.code, new Gson().toJson(baseV2Resp));
//                                } else {
//                                    throw new ApiV2Error(baseV2Resp.code, baseV2Resp.msg);
//                                }
                                // 由用户模块去判断token 的东西
//                                NetObserverManager.getInstance().notifyObserver(baseResp);
                                throw new ApiError(code, baseResp.msg);
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread());
        }


        private RespTransformer() {
        }

        public static <R> RespTransformer<R> newInstance() {
            return new RespTransformer<> ();
        }

        private static boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }
}
