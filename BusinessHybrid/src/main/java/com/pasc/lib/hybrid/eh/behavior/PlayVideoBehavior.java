package com.pasc.lib.hybrid.eh.behavior;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.luck.video.lib.PictureVideoPlayActivity;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.smtbrowser.entity.NativeResponse;

import java.io.Serializable;

public class PlayVideoBehavior implements BehaviorHandler, Serializable {
    @Override
    public void handler(Context context, String data, CallBackFunction function, NativeResponse response) {
        try{
            Gson gson = new Gson();
            PlayVideoBean playVideoBean = gson.fromJson(data,PlayVideoBean.class);
            if(playVideoBean.virtualSrc != null){
                String path[] = playVideoBean.virtualSrc.split(PascHybrid.PROTOFUL);
                Intent intent = new Intent(context, PictureVideoPlayActivity.class);
                intent.putExtra("video_path", path[path.length - 1]);
                intent.putExtra(PictureVideoPlayActivity.VIDEO_PLAY_MODE,PictureVideoPlayActivity.AUTO_PLAY);
                intent.putExtra(PictureVideoPlayActivity.VIDEO_USE_MODE,PictureVideoPlayActivity.PREVIEW_MODE);
                context.startActivity(intent);
            }else if(playVideoBean.src != null){
                Intent intent = new Intent(context, PictureVideoPlayActivity.class);
                intent.putExtra("video_path", playVideoBean.src);
                intent.putExtra(PictureVideoPlayActivity.VIDEO_PLAY_MODE,PictureVideoPlayActivity.AUTO_PLAY);
                intent.putExtra(PictureVideoPlayActivity.VIDEO_USE_MODE,PictureVideoPlayActivity.PREVIEW_MODE);
                context.startActivity(intent);
            }
        }catch (Exception e){

        }
    }

    public static class PlayVideoBean{
        @SerializedName("src")
        public String src;

        @SerializedName("virtualSrc")
        public String virtualSrc;
    }
}
