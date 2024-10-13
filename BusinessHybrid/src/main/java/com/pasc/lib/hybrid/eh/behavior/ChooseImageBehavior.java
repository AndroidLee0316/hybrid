package com.pasc.lib.hybrid.eh.behavior;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pasc.lib.base.permission.PermissionUtils;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.ActivityResultCallback;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.hybrid.eh.R;
import com.pasc.lib.hybrid.eh.widget.HybridEHChooseOptionDialog;
import com.pasc.lib.hybrid.webview.PascWebView;
import com.pasc.lib.log.PascLog;
import com.pasc.lib.picture.pictureSelect.ImagePicker;
import com.pasc.lib.picture.takephoto.app.TakePhoto;
import com.pasc.lib.picture.takephoto.app.TakePhotoImpl;
import com.pasc.lib.picture.takephoto.compress.CompressConfig;
import com.pasc.lib.picture.takephoto.model.InvokeParam;
import com.pasc.lib.picture.takephoto.model.TContextWrap;
import com.pasc.lib.picture.takephoto.model.TResult;
import com.pasc.lib.picture.takephoto.permission.InvokeListener;
import com.pasc.lib.picture.takephoto.permission.PermissionManager;
import com.pasc.lib.picture.takephoto.permission.TakePhotoInvocationHandler;
import com.pasc.lib.smtbrowser.entity.NativeResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class ChooseImageBehavior implements BehaviorHandler, TakePhoto.TakeResultListener {
    private String mHost = "";
    private CallBackFunction function;
    private NativeResponse response;
    private TakePhoto takePhoto;
    private ChooseImageBean chooseImageBean;

    @Override
    public void handler(Context context, String data, CallBackFunction function, NativeResponse response) {
        Gson gson = new Gson();
        ChooseImageBean chooseImageBean = gson.fromJson(data, ChooseImageBean.class);
        List<String> sourceTypes = chooseImageBean.sourceType;
        if (sourceTypes == null || sourceTypes.size() == 0) {
            return;
        }
        getFullHostUrl(context);
        this.chooseImageBean = chooseImageBean;
        this.function = function;
        this.response = response;

        PascHybrid.getInstance().setActivityResultCallback(new ActivityResultCallback() {

            @Override
            public void activityResult(int requestCode, int resultCode, Intent intent) {
                if (requestCode == 100 && resultCode == -1) {
                    ArrayList<String> pictures = intent.getStringArrayListExtra("images");
                    Log.i("pictures", pictures.toString());
                    sendResult(pictures, function, response);
                } else {
                    if (takePhoto != null) {
                        takePhoto.onActivityResult(requestCode, resultCode, intent);
                    }
                }
            }
        });

        if (sourceTypes.size() > 1) {
            showChooseDialog(context, chooseImageBean);
        } else if ("album".equals(sourceTypes.get(0))) {
            choosePic(context, chooseImageBean.count);
        } else if ("camera".equals(sourceTypes.get(0))) {
            chooseFromCamera(context, chooseImageBean);

        }
    }

    public TakePhoto getTakePhoto(final Context context) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(new InvokeListener() {
                @Override
                public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
                    return PermissionManager.checkPermission(TContextWrap.of((Activity) context)
                            , invokeParam.getMethod());
                }

            }).bind(new TakePhotoImpl((Activity) context, this));

        return takePhoto;
    }

    private void sendResult(ArrayList<String> pictures, CallBackFunction function, NativeResponse response) {
        List<ImageResult> imageResults = new ArrayList<ImageResult>();
        for (String picture : pictures) {
            picture = mHost + "/" + PascHybrid.PROTOFUL + picture;
            ImageResult imageResult = new ImageResult();
            imageResult.path = picture;
            imageResults.add(imageResult);
            //图片添加进去等待拦截
            PascHybrid.getInstance().addAuthorizationPath(picture);
        }
        ImageResults imageResultList = new ImageResults();
        imageResultList.imageResults = imageResults;

        response.code = 0;
        response.data = imageResultList;
        function.onCallBack(new Gson().toJson(response));
    }

    private void showChooseDialog(Context context, ChooseImageBean chooseImageBean) {
        HybridEHChooseOptionDialog choosePhotoDialog =
                new HybridEHChooseOptionDialog(context, R.layout.hybrideh_choose_option_dialog);
        choosePhotoDialog.setOnSelectedListener(new HybridEHChooseOptionDialog.OnSelectedListener() {
            @Override
            public void onFirst() {
                //拍照选择
                chooseFromCamera(context, chooseImageBean);
            }

            @Override
            public void onSecond() {
                //从相册选取
                choosePic(context, chooseImageBean.count);
            }

            @Override
            public void onCancel() {

            }
        });
        choosePhotoDialog.show();
    }

    private void chooseFromCamera(Context context, ChooseImageBean chooseImageBean) {
        //拍照
        String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"
                , "android.permission.CAMERA"};

        PermissionUtils.request((Activity) context, permissions)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            TakePhoto takePhoto = getTakePhoto(context);
                            File file = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DCIM)
                                .getAbsolutePath(), System.currentTimeMillis() + ".jpg");
                            if (!file.getParentFile().exists()) {
                                file.getParentFile().mkdirs();
                            }
                            Uri imageUri1 = Uri.fromFile(file);
                            if (chooseImageBean.sizeType.contains("compressed")) {
                                configCompress(takePhoto);
                            }
                            takePhoto.onPickFromCapture(imageUri1);
                        } else {
                            response.code = -1;
                            response.message = "permission deny by user";
                            function.onCallBack(new Gson().toJson(response));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override public void accept(Throwable throwable) throws Exception {
                        PascLog.e(throwable.getMessage());
                    }
                });
    }

    private void choosePic(Context context, int count) {
        String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

        PermissionUtils.request((Activity) context, permissions).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    ImagePicker.getInstance().setEnableCompress(true).
                            setSelectLimit(count).pickMutlPhoto((Activity) context, 100);
                }
            }
        });
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

    @Override
    public void takeSuccess(TResult result) {
        if (result == null || result.getImages() == null) {
            return;
        }
        ArrayList<String> paths = new ArrayList<String>();
        if (chooseImageBean.sizeType != null && chooseImageBean.sizeType.size() > 0 && chooseImageBean.sizeType.get(0)
                .contains("compressed")) {
            paths.add(result.getImage().getCompressPath());
        } else {
            paths.add(result.getImage().getOriginalPath());
        }
        sendResult(paths, function, response);
    }

    @Override
    public void takeFail(TResult tResult, String msg) {
        response.code = -1;
        response.message = "" + msg;
        function.onCallBack(new Gson().toJson(response));
    }

    @Override
    public void takeCancel() {
        response.code = -999;
        response.message = "from user cancel";
        function.onCallBack(new Gson().toJson(response));
    }

    //配置压缩
    private void configCompress(TakePhoto takePhoto) {
        CompressConfig config = new CompressConfig.Builder().setMaxSize(152400)//大小不超过150k
                .setMaxPixel(1028)//最大像素1028
                .enableReserveRaw(true)//是否压缩
                .create();
        takePhoto.onEnableCompress(config, false); //这个trued代表显示压缩进度条
    }

    private static class ChooseImageBean {
        @SerializedName("count")
        public int count;

        @SerializedName("sizeType")
        public List<String> sizeType;

        @SerializedName("sourceType")
        public List<String> sourceType;

        @SerializedName("imageType")
        public List<String> imageType;

        @SerializedName("fileType")
        public List<String> fileType;

    }

    private static class ImageResult {
        @SerializedName("path")
        public String path;

        @SerializedName("size")
        public long size = 0L;
    }

    public static class ImageResults {
        @SerializedName("tempFiles")
        public List<ImageResult> imageResults;
    }
}
