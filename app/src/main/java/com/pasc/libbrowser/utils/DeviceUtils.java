package com.pasc.libbrowser.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangwen881 on 17/3/24.
 */

public class DeviceUtils {

    private static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                int rest = (Integer) method.invoke(context, permission);
                result = rest == PackageManager.PERMISSION_GRANTED;
            } catch (Exception e) {
                result = false;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    private static Map<String, String> sDeviceInfoMap;

    public static Map<String, String> getDeviceInfo(Context context) {
        if (sDeviceInfoMap == null) {
            sDeviceInfoMap = new HashMap<>();
            try {
                android.telephony.TelephonyManager tm =
                        (android.telephony.TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String deviceId = null;
                if (checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
                    deviceId = tm.getDeviceId();
                }
                String mac = null;

                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = interfaces.nextElement();
                    byte[] addr = networkInterface.getHardwareAddress();

                    if (addr == null || addr.length == 0) {
                        continue;
                    }
                    StringBuilder sb = new StringBuilder();
                    for (byte b : addr) {
                        sb.append(String.format("%02x:", b));
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    mac = sb.toString();
                }

                sDeviceInfoMap.put("mac", mac);
                if (TextUtils.isEmpty(deviceId)) {
                    deviceId = mac;
                }
                if (TextUtils.isEmpty(deviceId)) {
                    deviceId = android.provider.Settings.Secure.getString(context.getContentResolver(),
                            android.provider.Settings.Secure.ANDROID_ID);
                }
                sDeviceInfoMap.put("deviceId", deviceId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sDeviceInfoMap;
    }


    public static String getDeviceIp(Context context){
        WifiManager wm=(WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        if(!wm.isWifiEnabled()) {
            return getIpAddressString();
        }
        WifiInfo wi=wm.getConnectionInfo();
        //获取32位整型IP地址
        int ipAdd=wi.getIpAddress();
        //把整型地址转换成“*.*.*.*”地址
        String ip=intToIp(ipAdd);
        return ip;
    }
    private static String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    /*获取设备IPv4地址对应的字符串*/
    public static String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }

    /*获取设备WiFi网络IPv4地址对应的字符串*/
    public static String getWiFiIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                if (netI.getDisplayName().equals("wlan0") || netI.getDisplayName().equals("eth0")) {
                    for (Enumeration<InetAddress> enumIpAddr = netI
                            .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null)
                return networkInfo.isAvailable();
        }
        return false;
    }

    public static boolean isXiaomiMobilePhone(){
        return "Xiaomi".equalsIgnoreCase(Build.MANUFACTURER);
    }

    public static boolean isVivoMobilePhone(){
        return "vivo".equalsIgnoreCase(Build.MANUFACTURER);
    }

    public static boolean isHuaweiMobilePhone(){
        return "huawei".equalsIgnoreCase(Build.MANUFACTURER);
    }

    public static boolean isMeizuMobilePhone(){
        return "meizu".equalsIgnoreCase(Build.MANUFACTURER);
    }

    public static boolean isLocationNeedGPSDevice(){
        return isHuaweiMobilePhone() || isVivoMobilePhone();
    }

    // 检查手机上是否安装了指定的软件
    public static boolean isAvilible(Context context, String packageName){
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if(packageInfos != null){
            for(int i = 0; i < packageInfos.size(); i++){
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @return
     */
    public static int getWindowHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 获取版本名
     */
    public static String getVersionName(Context ctx) {
        String versionName = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 随机生成RSA密钥对
     *
     * @param keyLength
     *            密钥长度，范围：512～2048<br>
     *            一般1024
     * @return
     */
    public static KeyPair generateRSAKeyPair(int keyLength)
    {
        try
        {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(keyLength);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
