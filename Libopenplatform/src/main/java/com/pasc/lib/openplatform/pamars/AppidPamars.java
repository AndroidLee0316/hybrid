package com.pasc.lib.openplatform.pamars;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 功能：
 * <p>
 * created by zoujianbo345
 * data : 2018/9/17
 */
public class AppidPamars {

    public AppidPamars(){}

    public AppidPamars(String appId){
        this.appId = appId;
    }
    public AppidPamars(String appId,String initCode){
        this.appId = appId;
        this.initCode = initCode;
    }
    public AppidPamars(String appId,String token,int code){
        this.appId = appId;
        this.token = token;
    }

    public AppidPamars(String appId,String token,int code,List<String> dataTypes){
        this.appId = appId;
        this.token = token;
        this.userDataTypes = dataTypes;
    }

    @SerializedName("appId")
    public String appId;
    @SerializedName("initCode")
    public String initCode;
    @SerializedName("token")
    public String token;

    @SerializedName("userDataTypes")
    public List<String> userDataTypes;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
