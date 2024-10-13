package com.pasc.lib.smtbrowser.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * create by wujianning385 on 2018/9/4.
 */
public class SendSMSBean {

  /**
   * 收件人列表
   */
  @SerializedName("recipients")
  public List<String> recipients;

  /**
   * 短信内容
   */
  @SerializedName("message")
  public String message;
}
