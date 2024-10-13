package com.pasc.lib.openplatform.resp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 功能：
 * <p>
 * created by zoujianbo345
 * data : 2018/9/17
 */
public class ServiceInfoResp implements Serializable{
  @SerializedName("unionId") public String unionId;
  @SerializedName("sessionId") public String sessionId;
  @SerializedName("token") public String token;
  @SerializedName("remark") public String remark;
  @SerializedName("appId") public String appId;
  @SerializedName("appKey") public String appKey;
  @SerializedName("thirdPartyServicesName") public String thirdPartyServicesName;
  @SerializedName("thirdPartyServicesLogo") public String thirdPartyServicesLogo;
  @SerializedName("createdDate") public String createdDate;
  /**
   * 是否需要实名认证 0不需要1需要 默认0
   */
  @SerializedName("realNameAuthStatus") public String realNameAuthStatus;
  /**
   * 是否需要弹授权框 0不需要1需要 默认1
   */
  @SerializedName("authStatus") public String authStatus;

  /**
   * 获取用户信息方式，1-统一返回，0-按需获取
   */
  @SerializedName("userInfoGetType") public String userInfoGetType;

  @SerializedName("applyUserDataTypeInfo") public List<UserDataTypeInfo> applyUserDataTypeInfo;

  public class UserDataTypeInfo implements Serializable{

    /**
     * 用户数据类型编码
     */
    @SerializedName("userDataTypeCode") public String userDataTypeCode;

    /**
     * 用户数据类型名称
     */
    @SerializedName("userDataTypeName") public String userDataTypeName;
    /**
     * 是否是默认返回的用户数据类型，0是非公开信息，1是公开信息
     */
    @SerializedName("isDefault") public String isDefault;
    /**
     * 所有关联字段名称拼接后的字符串
     */
    @SerializedName("relateNames") public String relateNames;

    /**
     * 关联字段集合
     */
    @SerializedName("relateCodes") public List<RelateCodes> relateCodes;

  }

  private class RelateCodes implements Serializable{

    @SerializedName("relateCode") public String relateCode;

    @SerializedName("relateName") public String relateName;
  }
}
