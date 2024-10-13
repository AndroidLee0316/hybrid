package com.pasc.lib.hybrid.eh.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.pasc.lib.log.PascLog;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

public class HybridFileDisplayView extends FrameLayout implements TbsReaderView.ReaderCallback {

    private static String TAG = HybridFileDisplayView.class.getSimpleName();
    private TbsReaderView mTbsReaderView;
    private int saveTime = -1;
    private Context context;

    public HybridFileDisplayView(Context context) {
        this(context, null, 0);
    }

    public HybridFileDisplayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HybridFileDisplayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTbsReaderView = new TbsReaderView(context, this);
        this.addView(mTbsReaderView, new LinearLayout.LayoutParams(-1, -1));
        this.context = context;
    }

    private OnGetFilePathListener mOnGetFilePathListener;

    public void setOnGetFilePathListener(OnGetFilePathListener mOnGetFilePathListener) {
        this.mOnGetFilePathListener = mOnGetFilePathListener;
    }


    private TbsReaderView getTbsReaderView(Context context) {
        return new TbsReaderView(context, this);
    }

    public void displayFile(File mFile) {

        if (mFile != null && !TextUtils.isEmpty(mFile.toString())) {
            //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
            String bsReaderTemp = "/storage/emulated/0/TbsReaderTemp";
            File bsReaderTempFile = new File(bsReaderTemp);

            if (!bsReaderTempFile.exists()) {
                PascLog.v(TAG, "准备创建/storage/emulated/0/TbsReaderTemp！！");
                boolean mkdir = bsReaderTempFile.mkdir();
                if (!mkdir) {
                    PascLog.v(TAG, "创建/storage/emulated/0/TbsReaderTemp失败！！！！！");
                }
            }

            //加载文件
            Bundle localBundle = new Bundle();
            PascLog.d(TAG, mFile.toString());
            //mFile = new File("/storage/emulated/0/nantong/1.docx");
            localBundle.putString("filePath", mFile.toString());
            localBundle.putString("tempPath", Environment.getExternalStorageDirectory() + "/" + "TbsReaderTemp");

            if (this.mTbsReaderView == null)
                this.mTbsReaderView = getTbsReaderView(context);
            boolean bool = this.mTbsReaderView.preOpen(getFileType(mFile.toString()), false);
            if (bool) {
                this.mTbsReaderView.openFile(localBundle);
            }

        } else {
            PascLog.v(TAG, "文件路径无效！");
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
            PascLog.d(TAG, "paramString---->null");
            return str;
        }
        PascLog.d(TAG, "paramString:" + paramString);
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            PascLog.d(TAG, "i <= -1");
            return str;
        }


        str = paramString.substring(i + 1);
        PascLog.d(TAG, "paramString.substring(i + 1)------>" + str);
        return str;
    }

    public void show() {
        if (mOnGetFilePathListener != null) {
            mOnGetFilePathListener.onGetFilePath(this);
        }
    }

    /***
     * 将获取File路径的工作，“外包”出去
     */
    public interface OnGetFilePathListener {
        void onGetFilePath(HybridFileDisplayView mFileDisplayView);
    }


    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {
        PascLog.e(TAG, "****************************************************" + integer);
    }

    public void onStopDisplay() {
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
        }
    }
}

