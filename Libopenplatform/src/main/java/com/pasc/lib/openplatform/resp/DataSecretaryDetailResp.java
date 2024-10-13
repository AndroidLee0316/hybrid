package com.pasc.lib.openplatform.resp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataSecretaryDetailResp {


    @SerializedName("dataSecretaryVO")
    public DataSecretaryResp dataSecretaryVO;

    @SerializedName("dataSecretaryDetailVOs")
    public List<DataDetail> dataSecretaryDetailVOs;

    /**
     * 头部展示的数据
     */
    public class DataDetail{

        @SerializedName("userDataTypeName")
        public String userDataTypeName;

        @SerializedName("relateField")
        public String relateField;

        /**
         * 是否展开了
         */
        @SerializedName("isOpened")
        public boolean isOpened;
    }
}
