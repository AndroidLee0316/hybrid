package com.pasc.lib.smtbrowser.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserAuthBean {
    @SerializedName("appId")
    public String appId;

    @SerializedName("name")
    public String name;

    @SerializedName("userDataTypes")
    public List<String> userDataTypes;

}
