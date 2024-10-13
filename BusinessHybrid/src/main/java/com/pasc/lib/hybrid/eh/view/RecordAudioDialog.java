package com.pasc.lib.hybrid.eh.view;

import android.app.Dialog;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pasc.lib.hybrid.eh.R;

public class RecordAudioDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    //点击录音
    private LinearLayout readyRecordLayout;
    private TextView tvReadyRecordCancel;
    private ImageView ivClickRecord;
    //录音中
    private LinearLayout recordingLayout;
    private ImageView ivRecording;
    private Chronometer chRecordTime;
    //点击播放
    private LinearLayout clickPlayLayout;
    private TextView tvClickPlayBack;
    private TextView tvClickPlayFinish;
    private ImageView ivPlayRecord;
    private TextView tvRecordTotalTime;
    private long recordTime; //记录下来的总时间
    //点击暂停
    private LinearLayout clickPauseLayout;
    private TextView tvClickPauseBack;
    private TextView tvClickPauseFinish;
    private ImageView ivPauseRecord;
    private TextView tvClickPause;
    private Chronometer chRecordRemainTime;
    private CircleProgressBar circleProgressBar;

    private boolean isPausePlay = false;
    private long playedTime;
    private static int progress = 0;
    private int mMaxAudioRecordSeconds = 600; //单位为秒

    public RecordAudioDialog(Context context) {
        super(context, R.style.BottomChoiceDialog);
        mContext = context;
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_record_audio, null);
        setContentView(contentView);
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.gravity = Gravity.BOTTOM;
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(attributes);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        initView();
    }

    public void setMaxAudioRecordSeconds(int maxAudioRecordSeconds) {
        mMaxAudioRecordSeconds = maxAudioRecordSeconds;
    }

    public void initView() {
        readyRecordLayout = findViewById(R.id.ll_ready_record);
        tvReadyRecordCancel = findViewById(R.id.tv_ready_record_cancle);
        tvReadyRecordCancel.setOnClickListener(this);
        ivClickRecord = findViewById(R.id.iv_click_record);
        ivClickRecord.setOnClickListener(this);

        recordingLayout = findViewById(R.id.ll_recording);
        ivRecording = findViewById(R.id.iv_recording);
        ivRecording.setOnClickListener(this);
        chRecordTime = findViewById(R.id.ch_record_time);

        clickPlayLayout = findViewById(R.id.ll_click_play);
        tvClickPlayBack = findViewById(R.id.tv_click_play_back);
        tvClickPlayBack.setOnClickListener(this);
        tvClickPlayFinish = findViewById(R.id.tv_click_play_finish);
        tvClickPlayFinish.setOnClickListener(this);
        ivPlayRecord = findViewById(R.id.iv_click_play);
        ivPlayRecord.setOnClickListener(this);
        tvRecordTotalTime = findViewById(R.id.tv_record_total_time);

        clickPauseLayout = findViewById(R.id.ll_click_pause);
        tvClickPauseBack = findViewById(R.id.tv_click_pause_back);
        tvClickPauseBack.setOnClickListener(this);
        tvClickPauseFinish = findViewById(R.id.tv_click_pause_finish);
        tvClickPauseFinish.setOnClickListener(this);
        ivPauseRecord = findViewById(R.id.iv_click_pause);
        ivPauseRecord.setOnClickListener(this);
        tvClickPause = findViewById(R.id.tv_click_pause);
        chRecordRemainTime = findViewById(R.id.ch_record_remain_time);
        circleProgressBar = findViewById(R.id.circle_progress_bar);
        circleProgressBar.setMaxProgress(100);
    }

    @Override public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_ready_record_cancle) {
            if (mOnSelectedListener != null) {
                mOnSelectedListener.onCancel();
            }
            dismiss();
        } else if (id == R.id.iv_click_record) {
            startRecord();
        } else if (id == R.id.iv_recording) {
            stopRecord();
        } else if (id == R.id.tv_click_play_back) {
            readyRecordLayout.setVisibility(View.VISIBLE);
            recordingLayout.setVisibility(View.GONE);
            clickPlayLayout.setVisibility(View.GONE);
            clickPauseLayout.setVisibility(View.GONE);
        } else if (id == R.id.tv_click_play_finish) {
            if (mOnSelectedListener != null) {
                mOnSelectedListener.onFinish(false, recordTime / 1000);
            }
            dismiss();
        } else if (id == R.id.iv_click_play) {
            startPlay();
        } else if (id == R.id.tv_click_pause_back) {
            readyRecordLayout.setVisibility(View.VISIBLE);
            recordingLayout.setVisibility(View.GONE);
            clickPlayLayout.setVisibility(View.GONE);
            clickPauseLayout.setVisibility(View.GONE);
            if (mOnSelectedListener != null) {
                mOnSelectedListener.onBack();
            }
            stopPlay();
        } else if (id == R.id.tv_click_pause_finish) {
            if (mOnSelectedListener != null) {
                mOnSelectedListener.onFinish(true, recordTime / 1000);
            }
            stopPlay();
            dismiss();
        } else if (id == R.id.iv_click_pause) {
            pauseAndContinuePlay();
        }
    }

    /**
     * 暂停和继续播放
     */
    private void pauseAndContinuePlay() {
        if (isPausePlay) {
            isPausePlay = false;
            if (mOnSelectedListener != null) {
                mOnSelectedListener.onContinuePlay();
            }
            ivPauseRecord.setImageResource(R.drawable.bg_click_pause);
            tvClickPause.setText("点击暂停");
            circleProgressBar.setVisibility(View.VISIBLE);
            //继续播放
            chRecordRemainTime.setBase(SystemClock.elapsedRealtime() - playedTime);
            chRecordRemainTime.start();
            chRecordRemainTime.setOnChronometerTickListener(
                new Chronometer.OnChronometerTickListener() {
                    @Override
                    public void onChronometerTick(Chronometer chronometer) {
                        circleProgressBar.setProgress(++progress);
                        Log.d("RecordAudioDialog", "progress2: " + progress);
                        if (SystemClock.elapsedRealtime() - chRecordRemainTime.getBase()
                            >= recordTime) {
                            //播放结束
                            Log.d("RecordAudioDialog", "progress3: " + 0);
                            finishPlay();
                        }
                    }
                });
        } else {
            isPausePlay = true;
            if (mOnSelectedListener != null) {
                mOnSelectedListener.onPausePlay();
            }
            playedTime = SystemClock.elapsedRealtime() - chRecordRemainTime.getBase();
            chRecordRemainTime.stop();
            chRecordRemainTime.setOnChronometerTickListener(null);
            ivPauseRecord.setImageResource(R.drawable.bg_click_play);
            circleProgressBar.setVisibility(View.GONE);
            tvClickPause.setText("点击播放");
        }
    }

    /**
     * 开始播放
     */
    private void startPlay() {
        readyRecordLayout.setVisibility(View.GONE);
        recordingLayout.setVisibility(View.GONE);
        clickPlayLayout.setVisibility(View.GONE);
        clickPauseLayout.setVisibility(View.VISIBLE);
        if (mOnSelectedListener != null) {
            mOnSelectedListener.onStartPlay();
        }
        //开始播放
        chRecordRemainTime.setBase(SystemClock.elapsedRealtime());
        //chRecordRemainTime.setBase(SystemClock.elapsedRealtime() + recordTime);
        //long remainHour = recordTime / 1000 / 60 / 60;
        //String remainHourStr;
        //if (remainHour > 9) {
        //    remainHourStr = String.valueOf(remainHour);
        //} else {
        //    remainHourStr = "0" + remainHour;
        //}
        //chRecordRemainTime.setFormat(remainHourStr + ":%s");
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        //    chRecordRemainTime.setCountDown(true);
        //}
        chRecordRemainTime.start();
        chRecordRemainTime.setOnChronometerTickListener(
            new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    circleProgressBar.setProgress(++progress);
                    Log.d("RecordAudioDialog", "progress1: " + progress);
                    if (SystemClock.elapsedRealtime() - chRecordRemainTime.getBase()
                        >= recordTime) {
                        //播放结束
                        finishPlay();
                    }
                }
            });
        circleProgressBar.setMaxProgress((int) (recordTime / 1000));
    }

    /**
     * 开始录音
     */
    private void startRecord() {
        readyRecordLayout.setVisibility(View.GONE);
        recordingLayout.setVisibility(View.VISIBLE);
        clickPlayLayout.setVisibility(View.GONE);
        clickPauseLayout.setVisibility(View.GONE);
        if (mOnSelectedListener != null) {
            mOnSelectedListener.onStartRecord();
        }

        chRecordTime.setBase(SystemClock.elapsedRealtime());
        chRecordTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override public void onChronometerTick(Chronometer chronometer) {
                long recordedSeconds = (SystemClock.elapsedRealtime() - chRecordTime.getBase()) / 1000;
                //超过最大录音时间限制
                if (recordedSeconds >= mMaxAudioRecordSeconds) {
                    stopRecord();
                }
            }
        });
        chRecordTime.start();
    }

    /**
     * 停止录音
     */
    private void stopRecord() {
        readyRecordLayout.setVisibility(View.GONE);
        recordingLayout.setVisibility(View.GONE);
        clickPlayLayout.setVisibility(View.VISIBLE);
        clickPauseLayout.setVisibility(View.GONE);
        if (mOnSelectedListener != null) {
            mOnSelectedListener.onStopRecord();
        }
        if (chRecordTime != null) {
            chRecordTime.stop();
            chRecordTime.setOnChronometerTickListener(null);
        }
        recordTime = SystemClock.elapsedRealtime() - chRecordTime.getBase();//保存这次记录的时间
        tvRecordTotalTime.setText(formatMiss(recordTime / 1000));
    }

    /**
     * 停止播放
     */
    private void stopPlay() {
        if (chRecordRemainTime != null) {
            chRecordRemainTime.stop();
            chRecordRemainTime.setOnChronometerTickListener(null);
        }
        progress = 0;
        if (circleProgressBar != null) {
            circleProgressBar.setProgress(progress);
        }
    }

    /**
     * 播放完成
     */
    private void finishPlay() {
        stopPlay();
        readyRecordLayout.setVisibility(View.GONE);
        recordingLayout.setVisibility(View.GONE);
        clickPlayLayout.setVisibility(View.VISIBLE);
        clickPauseLayout.setVisibility(View.GONE);
        if (mOnSelectedListener != null) {
            mOnSelectedListener.onStopPlay();
        }
    }

    // 将秒转化成小时分钟秒
    public String formatMiss(long miss) {
        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;
        return miss / 3600 > 0 ? hh + ":" + mm + ":" + ss : mm + ":" + ss;
    }

    public OnSelectedListener mOnSelectedListener;
    public interface OnSelectedListener {
        void onStartRecord();
        void onStopRecord();
        void onStartPlay();
        void onStopPlay();
        void onPausePlay();
        void onContinuePlay();
        void onCancel();
        void onFinish(boolean isPlaying, long recordTimeSeconds);
        void onBack();
    }

    public RecordAudioDialog setOnSelectedListener(final OnSelectedListener onSelectedListener) {
        mOnSelectedListener = onSelectedListener;
        return this;
    }
}