package com.pasc.lib.openplatform.forthird;


public interface RequestIdCallback{
    void getRequestId(String requsetId, int expiresIn);
    void authfail(int code,String msg);
    void initSuccess(int code,String msg);
}
