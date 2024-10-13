package com.pasc.lib.hybrid.eh.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.pasc.lib.base.permission.PermissionUtils;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.hybrid.eh.R;
import com.pasc.lib.hybrid.eh.view.FileBrowseView;
import com.pasc.lib.hybrid.widget.WebCommonTitleView;
import com.pasc.lib.net.download.DownLoadManager;
import com.pasc.lib.net.download.DownloadInfo;
import com.pasc.lib.net.download.DownloadObserver;
import io.reactivex.functions.Consumer;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * 附件详情
 * Created by lingchun147 on 2018/3/1.
 */

public class FileBrowseActivity extends AppCompatActivity {

    private Context mContext;
    private static String FILE_URL = "file_url";
    private static String FILE_NAME = "file_name";

    private WebCommonTitleView titleView;
    private FileBrowseView fileBrowseView;
    private ContentLoadingProgressBar fileLoadingView;
    private String filePath;
    private String path;
    private String fileName;

    public static void show(Context context, String url) {
        Intent intent = new Intent(context, FileBrowseActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(FILE_URL, url);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void show(Context context, String url, String fileName) {
        Intent intent = new Intent(context, FileBrowseActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(FILE_URL, url);
        bundle.putSerializable(FILE_NAME, fileName);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_browser);
        mContext = this;
        titleView = findViewById(R.id.view_title);
        fileBrowseView = findViewById(R.id.fileDisplayView);
        fileLoadingView = findViewById(R.id.loading_view);
        titleView.setOnLeftClickListener(new View.OnClickListener() {
                    @Override public void onClick(View view) {
                        finish();
                    }
                });

        initView();
    }

    private void initView() {

        fileBrowseView.setOnGetFilePathListener(new FileBrowseView.OnGetFilePathListener() {
            @Override public void onGetFilePath(FileBrowseView mFileDisplayView) {
                getFilePathAndShowFile();
            }
        });

        Intent intent = this.getIntent();
        if(intent != null) {
            path = (String) intent.getSerializableExtra(FILE_URL);
            fileName = (String) intent.getSerializableExtra(FILE_NAME);
        }
        titleView.setTitleText("附件详情");
        if (!TextUtils.isEmpty(path)) {
            setFilePath(path);
        }
        fileBrowseView.show();
    }

    private void getFilePathAndShowFile() {
        PermissionUtils.request(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>() {
            @Override public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    downLoadFromNet(getFilePath(), fileBrowseView);
                }else {
                    //PermissionSettingUtils.gotoPermissionSetting(FileBrowseActivity.this);
                }
            }
        });
    }

    @Override public void onDestroy() {
        super.onDestroy();
        if (fileBrowseView != null) {
            fileBrowseView.onStopDisplay();
        }
    }

    private String getFilePath() {
        return filePath;
    }

    public void setFilePath(String fileUrl) {
        this.filePath = fileUrl;
    }

    private void downLoadFromNet(final String url, final FileBrowseView mfileBrowseView) {

        try {
            File cacheFile = new File(getCacheFile(url));
            if (cacheFile.exists() && cacheFile.length()>0) {
                mfileBrowseView.displayFile(cacheFile);
                return;
            }else {
                if (cacheFile.exists()){
                    cacheFile.delete();
                }
                DownLoadManager downLoadManager = DownLoadManager.getDownInstance();
                DownloadInfo downloadInfo = new DownloadInfo(url, getFileName(url), getCache(), false);
                downloadInfo.downloadUrl(url);
                fileLoadingView.setVisibility(View.VISIBLE);
                fileLoadingView.getIndeterminateDrawable().setColorFilter(mContext.getResources().getColor(R.color.theme_color), PorterDuff.Mode.MULTIPLY);
                fileLoadingView.show();
                downLoadManager.startDownload(downloadInfo, new DownloadObserver() {
                    @Override
                    public void onDownloadStateProgressed(DownloadInfo downloadInfo) {
                        switch (downloadInfo.downloadState) {
                            case DownloadInfo.STATE_DOWNLOADING:
                                break;
                            case DownloadInfo.STATE_ERROR:
                                ToastUtils.toastMsg("文件下载失败");
                                cacheFile.delete();
                                fileLoadingView.hide();
                                //下载失败
                                break;
                            case DownloadInfo.STATE_PAUSED:
                                //暂停下载
                                break;
                            case DownloadInfo.STATE_FINISH:
                                fileLoadingView.hide();
                                mfileBrowseView.displayFile(cacheFile);
                                break;
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.toastMsg("下载附件失败：" + e.getMessage());
        }


    }

    /***
     * 获取缓存目录
     *
     * @return
     */
    private String getCache() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/smt/";
    }

    /***
     * 绝对路径获取缓存文件
     *
     * @param url
     * @return
     */
    private String getCacheFile(String url) {
        return getCache() + getFileName(url);
    }

    /***
     * 根据链接获取文件名（带类型的），具有唯一性
     *
     * @param url
     * @return
     */
    private String getFileName(String url) {
        String[] urlSplit = url.split("/");
        String fileName = "";
        try {
            if(TextUtils.isEmpty(this.fileName)) {
                fileName = URLDecoder.decode(urlSplit[urlSplit.length - 1], "UTF-8");
            }else {
                return this.fileName;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fileName;
    }
}
