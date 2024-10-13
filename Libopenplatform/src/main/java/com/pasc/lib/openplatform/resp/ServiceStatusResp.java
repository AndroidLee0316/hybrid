package com.pasc.lib.openplatform.resp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 功能：
 * <p>
 * created by zoujianbo345
 * data : 2018/9/17
 */
public class ServiceStatusResp {

    // 第三方服务在开放平台申请的appId
    @SerializedName("appId")
    public String appId;

    // 用户对第三方服务的授权状态，0：未授权，1：已授权
    @SerializedName("authorizationStatus")
    public int authorizationStatus;

    @SerializedName("userDataTypeAuthInfos")
    public List<AuthInfos> userDataTypeAuthInfos;


    public class AuthInfos{

        /**
         * 用户数据类型编码
         */
        @SerializedName("userDataTypeCode")
        public String userDataTypeCode;

        /**
         * 用户数据类型授权状态，0-未授权，1-已授权
         */
        @SerializedName("userDataTypeAuthStatus")
        public String userDataTypeAuthStatus;
    }
}
