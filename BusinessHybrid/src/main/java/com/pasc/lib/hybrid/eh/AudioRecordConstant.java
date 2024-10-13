package com.pasc.lib.hybrid.eh;

/**
 * Created by zhuangjiguang on 2021/2/4.
 */
public class AudioRecordConstant {
  public static final int AUDIO_RECORD_CODE_FINISH = 0;
  public static final int AUDIO_RECORD_CODE_NO_PERMISSION = -1;
  //-2 Unusable API，hybrid统一处理未初始化JsSdk的情况
  public static final int AUDIO_RECORD_CODE_CANCEL = -3;
  public static final int AUDIO_RECORD_CODE_OTHER_ERROR = -4;

  public static final String AUDIO_RECORD_MESSAGE_FINISH = "完成";
  public static final String AUDIO_RECORD_MESSAGE_NO_PERMISSION = "请开启录音权限";
  public static final String AUDIO_RECORD_MESSAGE_CANCEL = "用户取消";
  public static final String AUDIO_RECORD_MESSAGE_OTHER_ERROR = "其他错误";

}
