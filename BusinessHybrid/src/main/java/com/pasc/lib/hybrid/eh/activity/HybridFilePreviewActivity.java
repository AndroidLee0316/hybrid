package com.pasc.lib.hybrid.eh.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.pasc.lib.base.AppProxy;
import com.pasc.lib.base.permission.PermissionUtils;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.hybrid.eh.R;
import com.pasc.lib.hybrid.eh.utils.HybridFileDisplayView;
import com.pasc.lib.log.PascLog;
import com.pasc.lib.net.download.DownLoadManager;
import com.pasc.lib.net.download.DownloadInfo;
import com.pasc.lib.net.download.DownloadObserver;
import com.pasc.lib.widget.toolbar.PascToolbar;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import io.reactivex.functions.Consumer;


public class HybridFilePreviewActivity extends HybridEHBaseActivity {

    private static String FILE_URL = "file_url";
    private static String FILE_TYPE = "file_type";

    private HybridFileDisplayView fileDisplayView;
    public PascToolbar titleView;
    private String filePath;
    private String fileName;
    public static final int URL_NAME = 0; //文件名包含在url中
    public static final int HTTP_HEADER_NAME = 1; //文件名在http请求头中
    public static final int URL_PARAM = 2; //文件名在参数中
    int fileType;
    DownloadInfo downloadInfo;
    //EmptyView emptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fileDisplayView = findViewById(R.id.fileDisplayView);
        //emptyView = findViewById(R.id.emptyView);
        titleView = findViewById(R.id.view_title);

        titleView.addCloseImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initView();
    }

    @Override
    protected int layoutResId() {
        return R.layout.hybrideh_file_preview_layout;
    }

    @Override
    protected void onInit(@Nullable Bundle bundle) {

    }

    void showErrorView() {
//        emptyView.setVisibility(View.VISIBLE);
//        emptyView.showErrorLayoutWithNetJudge(new ICallBack() {
//            @Override
//            public void callBack() {
//                initView();
//            }
//        });
    }

    void showLoadingView() {
//        emptyView.setVisibility(View.VISIBLE);
//        emptyView.showDefaultLoadingLayout();
    }

    void showSuccessView() {
//        emptyView.setVisibility(View.GONE);

    }

    private void initView() {
        fileDisplayView.setOnGetFilePathListener(new HybridFileDisplayView.OnGetFilePathListener() {
            @Override
            public void onGetFilePath(HybridFileDisplayView mFileDisplayView) {
                getFilePathAndShowFile();
            }
        });

        Intent intent = this.getIntent();
        filePath = (String) intent.getSerializableExtra(FILE_URL);
        try {
            filePath = URLDecoder.decode(filePath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        fileType = intent.getIntExtra(FILE_TYPE, URL_NAME);
        if (fileType == URL_NAME) {
            fileName = getFileName(filePath);
//            titleView.setTitleText(fileName);
            fileDisplayView.show();
        } else if (fileType == HTTP_HEADER_NAME) {
            getHttpHeader(filePath);
        } else if (fileType == URL_PARAM) {
            fileName = getFileName(filePath);
//            titleView.setTitleText(fileName);
            filePath = filePath.substring(filePath.indexOf("url=") + 4);
            fileDisplayView.show();

        }
    }

    private void getFilePathAndShowFile() {

        PermissionUtils.request(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    //X5Init.getInstance().initX5(FileBrowseActivity.this);
                    downLoadFromNet(filePath, fileDisplayView);
                }else {
                    showErrorView();
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        if (downloadInfo != null)
            DownLoadManager.getDownInstance().stopDownload(downloadInfo);
        super.onDestroy();
        PascLog.v(TAG, "FileDisplayActivity-->onDestroy");
        if (fileDisplayView != null) {
            fileDisplayView.onStopDisplay();
        }
    }

    public static void show(Context context, String url, int type) {
        Intent intent = new Intent(context, HybridFilePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(FILE_URL, url);
        bundle.putInt(FILE_TYPE, type);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    private void downLoadFromNet(final String url, final HybridFileDisplayView mFileDisplayView) {
        showLoadingView();
        String fileNameStr = "filename";
        if (fileType == HTTP_HEADER_NAME) {
            fileNameStr = fileName;
        } else {
            fileNameStr = getFileName(url);//new File(getCacheDir(url), getFileName(url))
        }
        downloadInfo = new DownloadInfo(url, fileNameStr, "", false);
        DownLoadManager.getDownInstance().startDownload(downloadInfo, new DownloadObserver() {
            @Override
            public void onDownloadStateProgressed(DownloadInfo updateInfo) {
                PascLog.i(TAG, updateInfo.progress + "--" + updateInfo.totalLength + " - " + updateInfo.downloadState);

                switch (updateInfo.downloadState) {
                    case DownLoadManager.STATE_PAUSED:
                    case DownLoadManager.STATE_ERROR:
                        if (!isActivityDestroy()) {
                            showErrorView();
                            ToastUtils.toastMsg("文件下载失败");
                        }
                        break;
                    case DownLoadManager.STATE_FINISH:
                        if (!isActivityDestroy()) {
                            showSuccessView();
                            mFileDisplayView.displayFile(new File(updateInfo.getFilePath(AppProxy.getInstance().getContext())));
                        }
                        break;
                }
            }
        });
    }


    /***
     * 根据链接获取文件名（带类型的），具有唯一性
     */
    private String getFileName(String url) {
        String fileName = "";
        try {
            url = URLDecoder.decode(url, "UTF-8");
            String[] urlSplit = url.split("/");
            fileName = urlSplit[urlSplit.length - 1];

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return fileName;
    }

    public void getHttpHeader(final String urlStr) {
        showLoadingView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection connection = null;
                    URL url = new URL(urlStr);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    int code = connection.getResponseCode();
                    if (code == HttpURLConnection.HTTP_OK) {
                        fileName = connection.getHeaderField("Content-Disposition");
                        // 通过Content-Disposition获取文件名
                        if (fileName == null || fileName.length() < 1) {
                            // 通过截取URL来获取文件名
                            URL downloadUrl = connection.getURL();
                            // 获得实际下载文件的URL
                            fileName = downloadUrl.getFile();
                            fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                        } else {
                            fileName = fileName.substring(fileName.indexOf("filename=") + 9);
                            //fileName = URLDecoder.decode(fileName.substring(fileName.indexOf("filename=") + 9), "UTF-8");
                            // 存在文件名会被包含在""里面，所以要去掉，否则读取异常
                            fileName = fileName.replaceAll("\"", "");

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    //更新UI
                                    if (!isActivityDestroy() && fileDisplayView != null) {
                                        fileDisplayView.show();
                                    }
                                }
                            });
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isActivityDestroy())
                                    showErrorView();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isActivityDestroy())
                                showErrorView();
                        }
                    });
                }
            }
        }).start();
    }
}