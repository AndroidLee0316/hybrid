package com.pasc.lib.hybrid.eh.bean;

import com.google.gson.annotations.SerializedName;

/**
 * create by wujianning385 on 2019-08-05.
 */
public class PascPayBean {

  /**
   * 会员号，从开放平台个人信息接口获取
   */
  @SerializedName("token")
  public String token;

  /**
   * 订单号
   */
  @SerializedName("mchOrderNo")
  public String mchOrderNo;

  /**
   * 商务号
   */
  @SerializedName("merchantNo")
  public String merchantNo;

  /**
   * JSSDK内部写死一个字符串即可
   * 由于支付SDK存在多次回调，所以JSSDK内部需要注册一个方法给原生调用
   */
  @SerializedName("action")
  public String action;

}
