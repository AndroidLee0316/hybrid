package com.pasc.lib.hybrid.eh.behavior;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.Base64;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pasc.lib.base.permission.PermissionUtils;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.hybrid.eh.AudioRecordConstant;
import com.pasc.lib.hybrid.eh.R;
import com.pasc.lib.hybrid.eh.view.RecordAudioDialog;
import com.pasc.lib.hybrid.webview.PascWebView;
import com.pasc.lib.log.PascLog;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import com.pasc.lib.widget.dialog.DialogFragmentInterface;
import com.pasc.lib.widget.dialog.common.ButtonWrapper;
import com.pasc.lib.widget.dialog.common.PermissionDialogFragment2;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by zhuangjiguang on 2021/2/3.
 */
public class RecordAudioBehavior implements BehaviorHandler, Serializable, RecordAudioDialog.OnSelectedListener {
  private PascWebviewActivity activity;
  private static final String LOG_TAG = "RecordAudioBehavior";
  private static String fileName = null;
  private MediaRecorder recorder = null;
  private MediaPlayer player = null;
  private CallBackFunction mFunction;
  private NativeResponse mNativeResponse;
  private int mMaxAudioRecordSeconds = 600; //单位为秒
  private String mHost = "";

  public RecordAudioBehavior() {

  }

  public RecordAudioBehavior(int maxAudioRecordSeconds) {
    mMaxAudioRecordSeconds = maxAudioRecordSeconds;
  }

  @Override public void handler(Context context, String data, CallBackFunction function,
      NativeResponse response) {
    getFullHostUrl(context);
    if (context instanceof PascWebviewActivity) {
      activity = (PascWebviewActivity) context;
    }
    if (activity == null) {
      return;
    }
    mFunction = function;
    mNativeResponse = response;
    Disposable subscribe =
        PermissionUtils.request(activity, Manifest.permission.RECORD_AUDIO).subscribe(
            new Consumer<Boolean>() {
              @Override public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                  //有录音权限
                  RecordAudioDialog recordAudioDialog = new RecordAudioDialog(activity);
                  recordAudioDialog.setOnSelectedListener(RecordAudioBehavior.this);
                  if (mMaxAudioRecordSeconds > 0) {
                    recordAudioDialog.setMaxAudioRecordSeconds(mMaxAudioRecordSeconds);
                  }
                  recordAudioDialog.show();
                } else {
                  //开启录音权限
                  ButtonWrapper
                      buttonWrapper = ButtonWrapper.wapButton("去授权", R.color.white, R.drawable.selector_primary_button);
                  showPermissionDialog("录音权限", "语音输入或上传等功能需要录音权限才可以使用", R.drawable.ic_record_permission, buttonWrapper, true);
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

  /**
   * 权限开启型弹窗
   *
   * @param title         弹窗标题
   * @param desc          弹窗描述
   * @param iconResId     弹窗icon
   * @param buttonWrapper 弹窗button包装类
   */
  private void showPermissionDialog(String title, String desc, @DrawableRes int iconResId, ButtonWrapper buttonWrapper, boolean closeImgVisible) {
    final PermissionDialogFragment2 permissionDialogFragment = new PermissionDialogFragment2.Builder()
        .setTitle(title)
        .setCloseImgVisible(closeImgVisible)
        .setDesc(desc)
        .setIconResId(iconResId)
        .setButton(buttonWrapper, new DialogFragmentInterface.OnClickListener<PermissionDialogFragment2>() {
          @Override
          public void onClick(PermissionDialogFragment2 dialogFragment, int which) {
            dialogFragment.dismiss();
            Intent localIntent = new Intent();
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
            activity.startActivity(localIntent);
          }
        })
        .setOnCancelListener(new DialogFragmentInterface.OnCancelListener<PermissionDialogFragment2>() {
          @Override
          public void onCancel(PermissionDialogFragment2 dialogFragment) {
            dialogFragment.dismiss();
            if (mNativeResponse != null && mFunction != null) {
              mNativeResponse.code = AudioRecordConstant.AUDIO_RECORD_CODE_NO_PERMISSION;
              mNativeResponse.message = AudioRecordConstant.AUDIO_RECORD_MESSAGE_NO_PERMISSION;
              PascLog.d(LOG_TAG, "permission: " + new Gson().toJson(mNativeResponse));
              mFunction.onCallBack(new Gson().toJson(mNativeResponse));
            }
          }
        })
        .build();
    permissionDialogFragment.show(activity);
  }

  private void startPlaying() {
    player = new MediaPlayer();
    try {
      player.setDataSource(fileName);
      player.prepare();
      player.start();
    } catch (IOException e) {
      PascLog.e(LOG_TAG, "player prepare() failed");
    }
  }

  private void stopPlaying() {
    if (player != null) {
      player.release();
      player = null;
    }
  }

  private void pausePlaying() {
    if (player != null) {
      player.pause();
    }
  }

  private void continuePlaying() {
    if (player != null) {
      player.start();
    }
  }

  private void startRecording() {
    fileName = activity.getCacheDir().getAbsolutePath();
    fileName += "/audiorecord.aac";
    recorder = new MediaRecorder();
    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
    recorder.setOutputFile(fileName);
    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

    try {
      recorder.prepare();
    } catch (IOException e) {
      PascLog.e(LOG_TAG, "recorder prepare() failed");
    }

    recorder.start();
  }

  private void stopRecording() {
    if (recorder != null) {
      recorder.stop();
      recorder.release();
      recorder = null;
    }
  }

  @Override public void onStartRecord() {
    startRecording();
  }

  @Override public void onStopRecord() {
    stopRecording();
  }

  @Override public void onStartPlay() {
    startPlaying();
  }

  @Override public void onStopPlay() {
    stopPlaying();
  }

  @Override public void onPausePlay() {
    pausePlaying();
  }

  @Override public void onContinuePlay() {
    continuePlaying();
  }

  @Override public void onCancel() {
    if (mNativeResponse != null && mFunction != null) {
      mNativeResponse.code = AudioRecordConstant.AUDIO_RECORD_CODE_CANCEL;
      mNativeResponse.message = AudioRecordConstant.AUDIO_RECORD_MESSAGE_CANCEL;
      PascLog.d(LOG_TAG, "onCancel: " + new Gson().toJson(mNativeResponse));
      mFunction.onCallBack(new Gson().toJson(mNativeResponse));
    }
  }

  @Override public void onFinish(boolean isPlaying, long recordTimeSeconds) {
    File file = new File(fileName);
    if (file.exists()) {
      if (isPlaying) {
        stopPlaying();
      }
      if (mNativeResponse != null && mFunction != null) {
        mNativeResponse.code = AudioRecordConstant.AUDIO_RECORD_CODE_FINISH;
        mNativeResponse.message = AudioRecordConstant.AUDIO_RECORD_MESSAGE_FINISH;
        PascHybrid.getInstance().addAuthorizationPath(mHost + "/" + PascHybrid.PROTOFUL + fileName);
        AudioResult audioResult = new AudioResult();
        audioResult.tempFilePath = "/" + PascHybrid.PROTOFUL + fileName;
        audioResult.duration = recordTimeSeconds;
        mNativeResponse.data = audioResult;
        String responseStr = new Gson().toJson(mNativeResponse);
        PascLog.d(LOG_TAG, "onFinish: " + responseStr);
        mFunction.onCallBack(responseStr);
      }
    } else {
      if (isPlaying) {
        stopPlaying();
      }
      if (mNativeResponse != null && mFunction != null) {
        mNativeResponse.code = AudioRecordConstant.AUDIO_RECORD_CODE_OTHER_ERROR;
        mNativeResponse.message = AudioRecordConstant.AUDIO_RECORD_MESSAGE_OTHER_ERROR;
        String responseStr = new Gson().toJson(mNativeResponse);
        PascLog.d(LOG_TAG, "onFinish: " + responseStr);
        mFunction.onCallBack(responseStr);
      }
    }
  }

  public static class AudioResult {
    @SerializedName("tempFilePath")
    public String tempFilePath;
    @SerializedName("duration")
    public long duration;
  }

  @Override public void onBack() {
    stopPlaying();
  }

  /**
   * 将文件转成base64 字符串
   * @param path 文件路径
   * @return
   */
  public static String encodeBase64File(String path) {
    File file = new File(path);
    String base64 = null;
    InputStream in = null;
    try {
      in = new FileInputStream(file);
      byte[] bytes = new byte[(int)file.length()];
      int length = in.read(bytes);
      base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return base64;
  }
}