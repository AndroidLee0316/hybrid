 package com.pasc.libbrowser.data;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

 /**
  * create by wujianning385 on 2018/8/3.
  */
 public class WebEventBean {

     @SerializedName("eventId")
     public String eventId;

     @SerializedName("label")
     public String label;

     @SerializedName("map")
     public Map<String,String> map;
 }
