package com.pasc.lib.openplatform.address;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 功能：开发平台地址管理服务器返回的地址类
 *
 * @author lichangbao702
 * @email : lichangbao702@pingan.com.cn
 * @date : 2020-03-26
 */
public class AddressParam implements Serializable {

    /**
     *  数据类型，地址的话写地址，即值为 address
     */
    @SerializedName("userDataType")
    public String userDataType = "address";
    /**
     * 地址
     */
    @SerializedName("token")
    public String token;
    /**
     * 详细地址
     */
    @SerializedName("appId")
    public String appId;

}
