package com.pasc.lib.hybrid.eh.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.tencent.smtt.sdk.TbsReaderView;
import java.io.File;

/**
 * Created by lingchun147 on 2018/3/1.
 */

public class FileBrowseView extends FrameLayout implements TbsReaderView.ReaderCallback {

    private static String TAG = "FileBrowseView";
    private TbsReaderView mTbsReaderView;
    private int saveTime = -1;
    private Context context;
    private OnGetFilePathListener mOnGetFilePathListener;

    public FileBrowseView(Context context) {
        this(context, null, 0);
    }

    public FileBrowseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FileBrowseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTbsReaderView = new TbsReaderView(context, this);
        this.context = context;
    }

    public void setOnGetFilePathListener(OnGetFilePathListener mOnGetFilePathListener) {
        this.mOnGetFilePathListener = mOnGetFilePathListener;
    }

    private TbsReaderView getTbsReaderView(Context context) {
        return new TbsReaderView(context, this);
    }

    /***
     * 获取缓存目录
     *
     * @return
     */
    private String getCache() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/smt/";
    }


    public void displayFile(File mFile) {

        if (mFile != null && !TextUtils.isEmpty(mFile.toString())) {
            //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
            File bsReaderTempFile = new File(getCache(), "TbsReaderTemp");

            if (!bsReaderTempFile.exists()) {
                Log.v(TAG, "准备创建/storage/emulated/0/TbsReaderTemp！！");
                boolean mkdir = bsReaderTempFile.mkdir();
                if (!mkdir) {
                    Log.v(TAG, "创建/storage/emulated/0/TbsReaderTemp失败！！！！！");
                }
            }

            //加载文件
            Bundle localBundle = new Bundle();
            Log.d(TAG, mFile.toString());
            localBundle.putString("filePath", mFile.toString());

            localBundle.putString("tempPath",bsReaderTempFile.getPath());

            if (this.mTbsReaderView == null) {
                this.mTbsReaderView = getTbsReaderView(context);
            }

            boolean bool = this.mTbsReaderView.preOpen(getFileType(mFile.toString()), false);

            if (bool) {
                this.mTbsReaderView.openFile(localBundle);
                addView(mTbsReaderView,
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                Toast.makeText(context,"文件格式不支持浏览",Toast.LENGTH_LONG).show();
            }
        } else {
            Log.v(TAG, "文件路径无效！");
        }
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            Log.d(TAG, "paramString---->null");
            return str;
        }
        Log.d(TAG, "paramString:" + paramString);
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            Log.d(TAG, "i <= -1");
            return str;
        }

        str = paramString.substring(i + 1);
        Log.d(TAG, "paramString.substring(i + 1)------>" + str);
        return str;
    }

    public void show() {
        if (mOnGetFilePathListener != null) {
            mOnGetFilePathListener.onGetFilePath(this);
        }
    }

    @Override public void onCallBackAction(Integer integer, Object o, Object o1) {
        Log.v(TAG, "****************************************************" + integer);
    }

    public void onStopDisplay() {
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
        }
    }

    /***
     * 将获取File路径的工作，“外包”出去
     */
    public interface OnGetFilePathListener {
        void onGetFilePath(FileBrowseView mFileDisplayView);
    }
}
