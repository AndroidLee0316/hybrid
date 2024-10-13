package com.pasc.lib.hybrid.eh.behavior;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.hybrid.eh.bean.BigPhotosBean;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import com.pasc.lib.smtbrowser.view.PhotoViewPager;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 功能：
 * <p>
 * created by zoujianbo345
 * data : 2018/11/23
 */
public class PreviewPhotoBehavior implements BehaviorHandler,Serializable {


    @Override
    public void handler(Context context, String data, CallBackFunction function,
                        NativeResponse response) {
        try {
            Gson gson = new Gson();
            BigPhotosBean bigphotos = gson.fromJson(data,BigPhotosBean.class);
            PhotoViewPager photoViewPager = ((PascWebviewActivity) context).mWebviewFragment.photoViewPager;
            if (photoViewPager == null) {
                return;
            }
            photoViewPager.init(bigphotos.backgroundColor);
            photoViewPager.setVisibility(View.VISIBLE);
            photoViewPager.setPicUrl((ArrayList<String>) bigphotos.urls, bigphotos.index,bigphotos.backgroundColor);
        }catch (RuntimeException e){
            e.printStackTrace();
        }


    }
}

