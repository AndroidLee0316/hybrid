package com.pasc.lib.openplatform.address;

import android.util.Log;

import com.pasc.lib.base.AppProxy;
import com.pasc.lib.net.ApiGenerator;
import com.pasc.lib.net.resp.BaseRespThrowableObserver;
import com.pasc.lib.openplatform.network.TransformUtil;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.functions.Consumer;


/**
 * 功能：
 *
 * @author lichangbao702
 * @email : lichangbao702@pingan.com.cn
 * @date : 2020-03-26
 */
public class AddressPresenter {

    public void getAddressList(String appid, String token, final AddressCallback callback){

        String url = AppProxy.getInstance().getHost() + "/api/opening/openPlatform/everyTime/getAuthDataSources.do";

        AddressParam addressParam = new AddressParam();
        addressParam.appId = appid;
        addressParam.token = token;
        Disposable disposable = ApiGenerator.createApi (AddressApi.class)
                .getAddressList (url, addressParam)
                .compose (TransformUtil.getTransformer ())
                .subscribeOn (Schedulers.io ())
                .observeOn (AndroidSchedulers.mainThread ())
                .subscribe (new Consumer<List<AddressResp>>() {
                    @Override
                    public void accept(List<AddressResp> addressRespList){
                        if (addressRespList == null || addressRespList.size() == 0){
                            callback.onEmpty();
                        }else {
                            callback.onSuccess(addressRespList);
                        }

                    }
                }, new BaseRespThrowableObserver() {
                    @Override
                    public void onError(int i, String s) {
                        callback.onFailed(String.valueOf(i), s);
                    }
                });
    }


    public static interface AddressCallback{
        void onSuccess(List<AddressResp> addressRespList);
        void onEmpty();
        void onFailed(String code, String msg);
    }

}
