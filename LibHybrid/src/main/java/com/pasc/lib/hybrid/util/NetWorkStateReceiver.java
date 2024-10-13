package com.pasc.lib.hybrid.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;

import com.pasc.lib.hybrid.callback.NetworkStatusCallback;

/**
 * create by wujianning385 on 2018/9/12.
 */
public class NetWorkStateReceiver extends BroadcastReceiver {

  private static String TAG = NetWorkStateReceiver.class.getSimpleName();

  private NetworkStatusCallback mNetworkStatusCallback;

  public NetWorkStateReceiver(NetworkStatusCallback networkStatusCallback) {
    this.mNetworkStatusCallback = networkStatusCallback;
  }

  @Override public void onReceive(Context context, Intent intent) {

    //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {

      //获得ConnectivityManager对象
      ConnectivityManager connMgr =
              (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

      //获取ConnectivityManager对象对应的NetworkInfo对象
      //获取WIFI连接的信息
      NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
      //获取移动数据连接的信息
      NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
      if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
        Log.d(TAG, "WIFI已连接,移动数据已连接");
        mNetworkStatusCallback.onNetworkStatus(BridgeUtil.NETWORK_WIFI, true);
      } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
        Log.d(TAG, "WIFI已连接,移动数据已断开");
        mNetworkStatusCallback.onNetworkStatus(BridgeUtil.NETWORK_WIFI, true);
      } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
        Log.d(TAG, "WIFI已断开,移动数据已连接");
        mNetworkStatusCallback.onNetworkStatus(BridgeUtil.NETWORK_DATA, true);
      } else {
        Log.d(TAG, "WIFI已断开,移动数据已断开");
        mNetworkStatusCallback.onNetworkStatus(BridgeUtil.NETWORK_DISCONNECTED, false);
      }
      //API大于23时使用下面的方式进行网络监听
    } else {

      //获得ConnectivityManager对象
      ConnectivityManager connMgr =
              (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

      //获取所有网络连接的信息
      Network[] networks = connMgr.getAllNetworks();
      //用于存放网络连接信息
      StringBuilder sb = new StringBuilder();
      if (networks.length==0){
        mNetworkStatusCallback.onNetworkStatus(BridgeUtil.NETWORK_DISCONNECTED, false);
      }
      //通过循环将网络信息逐个取出来
        boolean haveNetwork = false;
      for (int i = 0; i < networks.length; i++) {
        //获取ConnectivityManager对象对应的NetworkInfo对象
        NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
        if(networkInfo == null){
            return;
        }
        sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI&&networkInfo.isConnected()) {
          mNetworkStatusCallback.onNetworkStatus(BridgeUtil.NETWORK_WIFI,
                  networkInfo.isConnected());
          haveNetwork = true;
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE&&networkInfo.isConnected()) {
          mNetworkStatusCallback.onNetworkStatus(BridgeUtil.NETWORK_DATA,
                  networkInfo.isConnected());
          haveNetwork = true;
        }
//        else if (){
//          mNetworkStatusCallback.onNetworkStatus(BridgeUtil.NETWORK_DISCONNECTED, false);
//        }
      }
      if(!haveNetwork){
          mNetworkStatusCallback.onNetworkStatus(BridgeUtil.NETWORK_DISCONNECTED, false);
      }
      Log.d(TAG, sb.toString());
    }
  }
}


