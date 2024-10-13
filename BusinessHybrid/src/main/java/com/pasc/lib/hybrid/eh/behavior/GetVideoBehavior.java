package com.pasc.lib.hybrid.eh.behavior;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.iceteck.silicompressorr.SiliCompressor;
import com.luck.video.lib.PictureSelector;
import com.luck.video.lib.config.PictureConfig;
import com.luck.video.lib.config.PictureMimeType;
import com.luck.video.lib.entity.LocalMedia;
import com.pasc.lib.base.permission.PermissionUtils;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.base.widget.LoadingDialog;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.ActivityResultCallback;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.hybrid.eh.R;
import com.pasc.lib.hybrid.eh.utils.GetVideoUtils;
import com.pasc.lib.hybrid.eh.widget.HybridEHChooseOptionDialog;
import com.pasc.lib.hybrid.webview.PascWebView;
import com.pasc.lib.hybrid.widget.CommonDialog;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import com.pasc.lib.widget.dialog.loading.LoadingDialogFragment;


import java.io.File;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;

public class GetVideoBehavior implements BehaviorHandler, Serializable {
    CallBackFunction callBackFunction;
    NativeResponse nativeResponse;
    String mHost = "";
    int maxSize;

    @Override
    public void handler(Context context, String data, CallBackFunction callBackFunction, NativeResponse nativeResponse) {
        try {
            Gson gson = new Gson();
            final VideoBean videoBean = gson.fromJson(data, VideoBean.class);
            this.callBackFunction = callBackFunction;
            this.nativeResponse = nativeResponse;
            if (TextUtils.isEmpty(videoBean.maxSize)){
                videoBean.maxSize = String.valueOf(100*1024*1024);
            }
            maxSize = Integer.valueOf(videoBean.maxSize) / 1024 / 1024;
            getFullHostUrl(context);
            PascHybrid.getInstance().setActivityResultCallback(new ActivityResultCallback() {
                @Override
                public void activityResult(int requestCode, int resultCode, Intent intent) {
                    if (requestCode == PictureConfig.CHOOSE_REQUEST && resultCode == -1) {

                        switch (requestCode) {
                            case PictureConfig.CHOOSE_REQUEST:
                                LocalMedia localMedia = PictureSelector.obtainMultipleResult(intent).get(0);
                                if (localMedia == null || localMedia.getDuration() == 0) {
                                    Toast.makeText(context, "视频文件已损坏，请重新拍摄", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                File file = new File(localMedia.getPath());
                                if (file.length() > Integer.valueOf(videoBean.maxSize)) {
                                    Toast.makeText(context, "视频大小超过限制，不能上传", Toast.LENGTH_SHORT).show();
                                    nativeResponse.code = -1;
                                    callBackFunction.onCallBack(gson.toJson(nativeResponse));
                                    return;
                                }
                                if (videoBean.compressed) {
                                    Log.i("视频-----》", localMedia.getPath());
                                    File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/pasc/videos");
                                    if (f.mkdirs() || f.isDirectory()) {
                                        //compress and output new video specs
                                        new VideoCompressAsyncTask(context, 960, 540).execute(localMedia.getPath(), f.getPath());
                                    }
                                } else {
                                    File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/pasc/videos");
                                    if (f.mkdirs() || f.isDirectory()) {
                                        //compress and output new video specs
                                        new VideoCompressAsyncTask(context, 1920, 1080).execute(localMedia.getPath(), f.getPath());
                                    }
                                }
                                break;
                        }

                    }
                }
            });

            if (null != videoBean.sourceType) {
                if (videoBean.sourceType.size() > 1) {
                    showChooseDialog(context,videoBean.maxDuration, maxSize);
                } else if ("album".equals(videoBean.sourceType.get(0))) {
                    GetVideoUtils.getVideoFromAlbum((Activity) context, videoBean.maxDuration, maxSize);
                } else if ("camera".equals(videoBean.sourceType.get(0))) {
                    GetVideoUtils.getVideoFromCamera((Activity) context, videoBean.maxDuration, maxSize);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendResult(LocalMedia localMedia) {
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(localMedia.getPath());
        File file = new File(localMedia.getPath());

        PascHybrid.getInstance().addAuthorizationPath(mHost + "/" + PascHybrid.PROTOFUL + localMedia.getPath());
        VideoReturnBean videoReturnBean = new VideoReturnBean();
        videoReturnBean.duration = localMedia.getDuration() / 1000;
        videoReturnBean.size = file.length();
        videoReturnBean.tempFilePath = "/" + PascHybrid.PROTOFUL + localMedia.getPath();
        videoReturnBean.height = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        videoReturnBean.width = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        nativeResponse.code = 0;
        nativeResponse.data = videoReturnBean;
        callBackFunction.onCallBack(new Gson().toJson(nativeResponse));

    }

    private void getFullHostUrl(Context context) {
        try {
            PascWebView webView = ((PascWebviewActivity) context).mWebviewFragment.mWebView;
            mHost = webView.getUrl();
            if (TextUtils.isEmpty(mHost)) {
                mHost = webView.getOriginalUrl();
            }
            if (mHost == null) {
                mHost = "";
            } else {
                mHost = Uri.decode(mHost);
            }
            Uri uri = Uri.parse(mHost);
            if (uri.getScheme() != null && uri.getAuthority() != null) {
                mHost = uri.getScheme() + "://" + uri.getAuthority();
            } else {
                mHost = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
            mHost = "";
        }
    }

    private void showChooseDialog(Context context,final String maxDuration, final int maxSize) {
        HybridEHChooseOptionDialog choosePhotoDialog =
                new HybridEHChooseOptionDialog(context, R.layout.hybrideh_choose_option_dialog);
        choosePhotoDialog.setOnSelectedListener(new HybridEHChooseOptionDialog.OnSelectedListener() {
            @Override
            public void onFirst() {
                //拍照选择
                GetVideoUtils.getVideoFromCamera((Activity) context, maxDuration, maxSize);
            }

            @Override
            public void onSecond() {
                //从相册选取
                GetVideoUtils.getVideoFromAlbum((Activity) context, maxDuration, maxSize);
            }

            @Override
            public void onCancel() {

            }
        });
        choosePhotoDialog.show();
    }

    public class VideoCompressAsyncTask extends AsyncTask<String, String, String> implements Serializable {
        LoadingDialog loadingDialog;

        Context mContext;
        int mWidth, mHeight;

        public VideoCompressAsyncTask(Context context) {
            mContext = context;
        }

        public VideoCompressAsyncTask(Context context, int width, int height) {
            mContext = context;
            mWidth = width;
            mHeight = height;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(mContext, "视频正在处理中");
                loadingDialog.setCanceledOnTouchOutside(false);
            }
            try {
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        //execute the task
                        if (!loadingDialog.isShowing()) {
                            loadingDialog.show();
                        }
                    }
                }, 200);

            } catch (Exception e) {

            }
        }

        @Override
        protected String doInBackground(String... paths) {
            String filePath = null;
            try {
                if (mWidth == 0) {
                    filePath = SiliCompressor.with(mContext).compressVideo(paths[0], paths[1]);
                } else {
                    filePath = SiliCompressor.with(mContext).compressVideo(paths[0], paths[1], mWidth, mHeight, 900000);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return filePath;

        }


        @Override
        protected void onPostExecute(String compressedFile) {
            super.onPostExecute(compressedFile);
            File imageFile = new File(compressedFile);
            float length = imageFile.length() / 1024f; // Size in KB
            String value;
            if (length >= 1024)
                value = length / 1024f + " MB";
            else
                value = length + " KB";
            int duration = PictureMimeType.getLocalVideoDuration(compressedFile);
            LocalMedia media = new LocalMedia();
            media.setPath(compressedFile);
            media.setDuration(duration);
            media.setPictureType(PictureMimeType.createVideoType(compressedFile));
            media.setSize(imageFile.length());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingDialog.dismiss();
                }
            },200);
            Log.i("Silicompressor", "Path: " + compressedFile);
            sendResult(media);
        }
    }

    public static class VideoBean {
        @SerializedName("sourceType")
        public List<String> sourceType;
        @SerializedName("compressed")
        public boolean compressed;
        @SerializedName("maxDuration")
        public String maxDuration;
        @SerializedName("camera")
        public String camera;
        @SerializedName("maxSize")
        public String maxSize;
    }

    public static class VideoReturnBean {
        @SerializedName("tempFilePath")
        public String tempFilePath;
        @SerializedName("duration")
        public long duration;
        @SerializedName("size")
        public long size;
        @SerializedName("height")
        public String height;
        @SerializedName("width")
        public String width;
    }
}
