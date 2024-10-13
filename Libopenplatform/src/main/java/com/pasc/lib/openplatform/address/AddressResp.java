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
public class AddressResp implements Serializable {

    /**
     *  地址收件人手机号
     */

    @SerializedName("addressMobile")
    public String addressMobile;
    /**
     * 地址
     */

    @SerializedName("addressName")
    public String addressName;
    /**
     * 详细地址
     */

    @SerializedName("detailAddress")
    public String detailAddress;

}
