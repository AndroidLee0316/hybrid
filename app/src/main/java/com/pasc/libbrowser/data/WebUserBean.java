 package com.pasc.libbrowser.data;

import com.google.gson.annotations.SerializedName;

 /**
  * create by wujianning385 on 2018/7/27.
  */
 public class WebUserBean {

     @SerializedName("token")
     public String token;

     @SerializedName("userId")
     public String userId;

     @SerializedName("mobile")
     public String mobile;

     @SerializedName("userName")
     public String userName;

     @SerializedName("isAuth")
     public String isAuth;

     public WebUserBean(String token, String userId, String mobile, String userName, String isAuth) {
         this.token = token;
         this.userId = userId;
         this.mobile = mobile;
         this.userName = userName;
         this.isAuth = isAuth;
     }
 }
