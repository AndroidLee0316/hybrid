package com.pasc.lib.hybrid.eh.utils;

import android.app.Activity;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.luck.video.lib.PictureSelector;
import com.luck.video.lib.config.PictureConfig;
import com.luck.video.lib.config.PictureMimeType;
import com.pasc.lib.base.permission.PermissionUtils;
import com.pasc.lib.base.permission.RxPermissions;

import io.reactivex.functions.Consumer;


public class GetVideoUtils {
    public static void getVideoFromAlbum(Activity activity,String maxDuration,int maxSize){
        int duration;
        if(maxDuration == null){
            duration = 60;
        }else{
            duration = Integer.valueOf(maxDuration);
        }
        String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE"};
        PermissionUtils.request(activity,permissions).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if(aBoolean){
                    PictureSelector.create(activity)
                            .openGallery(PictureMimeType.ofVideo())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                            .imageSpanCount(4)// 每行显示个数
                            .previewVideo(true)// 是否可预览视频
                            .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                            .videoQuality(1)// 视频录制质量 0压缩 or 1高清
                            .videoMaxSecond(10)//显示多少秒以内的视频or音频也可适用
                            .recordVideoSecond(duration)//录制视频秒数 默认60s
                            .videoMaxSize(maxSize)
                            .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                }
            }
        });

    }

    public static void getVideoFromCamera(Activity activity,String maxDuration,int maxSize){
        String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE"};
        PermissionUtils.request(activity,permissions).subscribe(new Consumer<Boolean>() {

            @Override
            public void accept(Boolean aBoolean) throws Exception {
                int duration;
                if(maxDuration == null){
                    duration = 60;
                }else{
                    duration = Integer.valueOf(maxDuration);
                }
                PictureSelector.create(activity)
                        .openCamera(PictureMimeType.ofVideo())// 单独拍照，也可录像或也可音频 看你传入的类型是图片or视频
                        .previewVideo(true)// 是否可预览视频
                        .videoQuality(1)// 视频录制质量 0 or 1
                        .recordVideoSecond(duration)//录制视频秒数 默认60s
                        .videoMaxSize(maxSize)
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            }
        });


    }
}
