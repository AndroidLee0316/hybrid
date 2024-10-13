package com.pasc.lib.hybrid.callback;

/**
 * create by wujianning385 on 2018/9/12.
 */
public interface NetworkStatusCallback {
    void onNetworkStatus(int networkType, boolean isConnected);
}
