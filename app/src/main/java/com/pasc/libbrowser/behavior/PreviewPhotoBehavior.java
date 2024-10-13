 package com.pasc.libbrowser.behavior;

import android.content.Context;
import android.view.View;

import com.google.gson.Gson;
import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import com.pasc.lib.smtbrowser.view.PhotoViewPager;
import com.pasc.libbrowser.BigPhotoActivity;
import com.pasc.libbrowser.data.BigPhotosBean;
import java.io.Serializable;
import java.util.ArrayList;

 /**
 * create by wujianning385 on 2018/8/2.
 */
public class PreviewPhotoBehavior implements BehaviorHandler,Serializable {


    @Override public void handler(Context context, String data, CallBackFunction function,
            NativeResponse response) {

        try {
            Gson gson = new Gson();
            BigPhotosBean bigphotos = gson.fromJson(data,BigPhotosBean.class);
            PhotoViewPager photoViewPager = ((PascWebviewActivity) context).mWebviewFragment.photoViewPager;
            if (photoViewPager == null) {
                return;
            }
            photoViewPager.setVisibility(View.VISIBLE);
            photoViewPager.setPicUrl((ArrayList<String>) bigphotos.urls, bigphotos.index,"#000000");
        }catch (RuntimeException e){
            e.printStackTrace();
        }


    }
}
