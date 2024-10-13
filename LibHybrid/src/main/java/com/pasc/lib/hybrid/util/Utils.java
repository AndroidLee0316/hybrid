package com.pasc.lib.hybrid.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.pasc.lib.hybrid.network.PictureBiz;
import com.pasc.lib.smtbrowser.util.BrowserUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import okio.ByteString;

import static android.os.Environment.DIRECTORY_DCIM;

/**
 * 适配屏幕工具类
 */
public class Utils {

  public static int getScreenWidth(Context context) {
    WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = manager.getDefaultDisplay();
    return display.getWidth();
  }

  public static int getScreenHeight(Context context) {
    WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = manager.getDefaultDisplay();
    return display.getHeight();
  }

  public static int dp2px(float dpValue) {
    return (int) (0.5F + dpValue * Resources.getSystem().getDisplayMetrics().density);
  }

  public static float getScreenDensity(Context context) {
    try {
      DisplayMetrics dm = new DisplayMetrics();
      WindowManager manager =
          (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
      manager.getDefaultDisplay().getMetrics(dm);
      return dm.density;
    } catch (Exception ex) {

    }
    return 1.0f;
  }

  public static int calcOfProportionWidth(Context context, int realTotalWidth, int realWidth) {
    return realWidth * getScreenWidth(context) / realTotalWidth;
  }

  public static int calcOfProportionHeight(Context context, int realTotalHeight, int realHeight) {
    return realHeight * getScreenHeight(context) / realTotalHeight;
  }

  /**
   * 获取当前应用版本名 [一句话功能简述]<BR>
   * [功能详细描述]
   */
  public static String getLocalVersionName(Context context) {
    String versionName = "";
    try {
      PackageInfo pinfo = context.getPackageManager()
          .getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
      versionName = pinfo.versionName;
    } catch (NameNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return versionName;
  }

  /**
   * 获取屏幕的高度
   *
   * @return 屏幕高度
   */
  public static int getDisplayHeight(Activity activity) {
    return activity.getWindowManager().getDefaultDisplay().getHeight();
  }

  /**
   * 获取顶部状态栏高度
   *
   * @return 状态栏高度
   */
  public static int getStatusHeigth(Activity activity) {
    Rect rectgle = new Rect();
    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectgle);
    int statusBarHeight = rectgle.top;
    return statusBarHeight;
  }

  /**
   * 唯一的设备ID： GSM手机的 IMEI 和 CDMA手机的 MEID. Return null if device ID is not
   * available.
   */
  public static String getImei(Context context) {
    String deviceId = "";
    try {
      android.telephony.TelephonyManager tm =
          (android.telephony.TelephonyManager) context.getSystemService(
              Context.TELEPHONY_SERVICE);

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

      //                sDeviceInfoMap.put("mac", mac);
      if (TextUtils.isEmpty(deviceId)) {
        deviceId = mac;
      }
      if (TextUtils.isEmpty(deviceId)) {
        deviceId = android.provider.Settings.Secure.getString(context.getContentResolver(),
            android.provider.Settings.Secure.ANDROID_ID);
      }
      //                sDeviceInfoMap.put("deviceId", deviceId);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return deviceId;
  }

  public static boolean checkPermission(Context context, String permission) {
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
      if (pm.checkPermission(permission, context.getPackageName())
          == PackageManager.PERMISSION_GRANTED) {
        result = true;
      }
    }
    return result;
  }

  /**
   * 选择了通讯录联系人
   */
  public static String[] getPhoneContacts(Context context, Uri uri) {
    String[] contact = new String[2];
    ////得到ContentResolver对象
    //ContentResolver cr = context.getContentResolver();
    ////取得电话本中开始一项的光标
    //Cursor cursor = cr.query(uri, null, null, null, null);
    //if (cursor != null) {
    //  cursor.moveToFirst();
    //  //取得联系人姓名
    //  int nameFieldColumnIndex =
    //      cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
    //  contact[0] = cursor.getString(nameFieldColumnIndex);
    //  //取得电话号码
    //  String ContactId =
    //      cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
    //  Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
    //      ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null,
    //      null);
    //  if (phone != null) {
    //    phone.moveToFirst();
    //    contact[1] = phone.getString(
    //        phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    //  }
    //  phone.close();
    //  cursor.close();
    //} else {
    //  return null;
    //}

    // 修复高版本手机无法获取通讯录的bug.
    String phoneNum = null;
    String contactName = null;
    ContentResolver contentResolver = context.getContentResolver();
    Cursor cursor = null;
    if (uri != null) {
      cursor = contentResolver.query(uri,
          new String[]{"display_name","data1"}, null, null, null);
    }
    while (cursor.moveToNext()) {
      contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
      phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    }
    cursor.close();
    //  把电话号码中的  -  符号 替换成空格
    if (phoneNum != null) {
      phoneNum = phoneNum.replaceAll("-", " ");
      // 空格去掉  为什么不直接-替换成"" 因为测试的时候发现还是会有空格 只能这么处理
      phoneNum= phoneNum.replaceAll(" ", "");
    }

    contact[0] = contactName;
    contact[1] = phoneNum;

    return contact;
  }

  public static boolean isBackground(Context context) {
    ActivityManager activityManager =
        (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    if (null == activityManager) {
      return false;
    }
    List<ActivityManager.RunningAppProcessInfo> appProcesses =
        activityManager.getRunningAppProcesses();

    if (appProcesses == null) return false;

    for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
      if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
        Log.i(context.getPackageName(), "此appimportace ="
            + appProcess.importance
            + ",context.getClass().getName()="
            + context.getClass().getName());
        if (appProcess.importance
            != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
          Log.i(context.getPackageName(), "处于后台" + appProcess.processName);
          return true;
        } else {
          Log.i(context.getPackageName(), "处于前台" + appProcess.processName);
          return false;
        }
      }
    }
    return false;
  }

  /**
   * 是否开启沉浸式状态栏
   */
  public static void openImmersiveStatusBar(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      Window window = activity.getWindow();
      window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        // 因为EMUI3.1系统与这种沉浸式方案API有点冲突，会没有沉浸式效果。
        // 所以这里加了判断，EMUI3.1系统不清除FLAG_TRANSLUCENT_STATUS
        //if (!isEMUI3_1()) {
        //    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //}
        window.getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
      }
    }
  }

  /**
   * 获取状态栏的高度
   */
  public static int getStatusBarHeight(Context context) {
    return getSystemComponentDimen(context, "status_bar_height");
  }

  /**
   * 获取系统组件高度
   *
   * @param context 上下文
   * @param dimenName 组件id ，如status_bar_height
   * @return 组件高度
   */
  private static int getSystemComponentDimen(Context context, String dimenName) {
    int statusHight = 0;
    try {
      //反射拿到android.R.internal.R$dimen内部类
      Class<?> clazz = Class.forName("com.android.internal.R$dimen");
      //创建dimen实例
      Object object = clazz.newInstance();
      //拿到dimenName对应的变量名，在拿到其对应的值并转为int值
      String heightStr = clazz.getField(dimenName).get(object).toString();
      int height = Integer.parseInt(heightStr);
      //dp--->dx
      statusHight = context.getResources().getDimensionPixelSize(height);
      return statusHight;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return statusHight;
  }

  /**
   * 是否设置状态栏字体为黑色和是否是沉浸式
   */
  public static void setStatusBarLightMode(Activity activity, boolean isImmersive,
      boolean isDark) {
    if (isImmersive) {
      openImmersiveStatusBar(activity);
    }
        /*if (false&&DeviceUtils.isMiui()) {
            MIUISetStatusBarLightMode(activity, isDark);
        } else */
    //        if (DeviceUtils.isFlyme()) {
    //            FlymeSetStatusBarLightMode(activity, isDark);
    //        } else
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      {
        if (isDark) {
          activity.getWindow()
              .getDecorView()
              .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        } else {
          activity.getWindow()
              .getDecorView()
              .setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE
                  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
      }
    }
  }

  /**
   * 获取当前的网络状态 ：没有网络--0,WIFI网络--2, 4G网络/3G网络/2G网络--1
   * 自定义
   */
  public static int getAPNType(Context context) {
    //结果返回值
    int netType = 0;
    //获取手机所有连接管理对象
    ConnectivityManager manager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (manager == null) {
      return netType;
    }
    //获取NetworkInfo对象
    NetworkInfo networkInfo = manager.getActiveNetworkInfo();
    //NetworkInfo对象为空 则代表没有网络
    if (networkInfo == null) {
      return netType;
    }
    //否则 NetworkInfo对象不为空 则获取该networkInfo的类型
    int nType = networkInfo.getType();
    if (nType == ConnectivityManager.TYPE_WIFI) {
      //WIFI
      netType = 2;
    } else if (nType == ConnectivityManager.TYPE_MOBILE) {
      int nSubType = networkInfo.getSubtype();
      TelephonyManager telephonyManager =
          (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      //3G   联通的3G为UMTS或HSDPA 电信的3G为EVDO
      if (nSubType == TelephonyManager.NETWORK_TYPE_LTE
          && !telephonyManager.isNetworkRoaming()) {
        netType = 1;
      } else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
          || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
          || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0
          && !telephonyManager.isNetworkRoaming()) {
        netType = 1;
        //2G 移动和联通的2G为GPRS或EGDE，电信的2G为CDMA
      } else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS
          || nSubType == TelephonyManager.NETWORK_TYPE_EDGE
          || nSubType == TelephonyManager.NETWORK_TYPE_CDMA
          && !telephonyManager.isNetworkRoaming()) {
        netType = 1;
      } else {
        netType = 1;
      }
    }
    return netType;
  }

  public static void showSslErrorDialog(Context context, final SslErrorHandler handler) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle("提示")
        .setMessage("您即将访问的外部网站涉及不安全证书，确认信任当前网站并访问吗？")
        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            handler.cancel();
            dialogInterface.dismiss();
            ((Activity) context).finish();
          }
        })
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            handler.proceed();
            dialogInterface.dismiss();
          }
        })
        .setCancelable(false)
        .create()
        .show();
  }

  public static String appendUri(String uri, String appendQuery) throws URISyntaxException {
    URI oldUri = new URI(uri);
    String newQuery = oldUri.getQuery();
    if (newQuery == null) {
      newQuery = appendQuery;
    } else {
      newQuery += "&" + appendQuery;
    }

    URI newUri = new URI(oldUri.getScheme(), oldUri.getAuthority(),
        oldUri.getPath(), newQuery, oldUri.getFragment());

    return newUri.toString();
  }

  /**
   * 获取去掉uiparams参数后的字符串
   */
  public static String getDeleteParamUri(String uri) throws URISyntaxException {

    //Uri tempUri = Uri.parse(uri);
    //tempUri.getQueryParameter()


    URI oldUri = new URI(uri);
    String newQuery = oldUri.getRawQuery();
    if (TextUtils.isEmpty(newQuery)) {
      return uri;
    } else if (!newQuery.contains("&") && newQuery.contains("uiparams")) {
      return uri.replace(newQuery, "");
    } else {
      String[] strings = newQuery.split("&");
      for (int i = 0; i < strings.length; i++) {
        if (strings[i].contains("uiparams")) {
          return uri.replace(strings[i], "");
        }
      }
      return uri;
    }
  }

  /**
   * 获取uiparam的值
   */
  public static String getUiparam(String uri) throws URISyntaxException {
    URI oldUri = new URI(uri);
    String newQuery = oldUri.getQuery();
    if (TextUtils.isEmpty(newQuery)) {
      return null;
    } else if (!newQuery.contains("&") && newQuery.contains("uiparams")) {
      return newQuery.replace("uiparams=", "");
    } else {
      String[] strings = newQuery.split("&");
      for (int i = 0; i < strings.length; i++) {
        if (strings[i].contains("uiparams")) {
          return strings[i].replace("uiparams=", "");
        }
      }

      return null;
    }
  }

  public static String getApplicationName(Context context) {
    PackageManager packageManager = null;
    ApplicationInfo applicationInfo = null;
    try {
      packageManager = context.getPackageManager();
      applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      applicationInfo = null;
    }
    String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);

    return applicationName;
  }

  public static void setColors(ProgressBar progressBar, int backgroundColor, int progressColor) {
    //Background
    ClipDrawable bgClipDrawable =
        new ClipDrawable(new ColorDrawable(backgroundColor), Gravity.LEFT, ClipDrawable.HORIZONTAL);
    bgClipDrawable.setLevel(10000);
    //Progress
    ClipDrawable progressClip =
        new ClipDrawable(new ColorDrawable(progressColor), Gravity.LEFT, ClipDrawable.HORIZONTAL);
    //Setup LayerDrawable and assign to progressBar
    Drawable[] progressDrawables = { bgClipDrawable, progressClip/*second*/, progressClip };
    LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);
    progressLayerDrawable.setId(0, android.R.id.background);
    progressLayerDrawable.setId(1, android.R.id.secondaryProgress);
    progressLayerDrawable.setId(2, android.R.id.progress);

    progressBar.setProgressDrawable(progressLayerDrawable);
  }

  public static boolean hasHoneycomb() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
  }

  public static boolean hasJellyBean() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
  }

  public static boolean hasJellyBeanMR1() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
  }

  public static boolean hasKitkat() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
  }

  public static boolean hasLollipop() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
  }

  public static String hash(String source) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("MD5");
      byte[] md5Bytes = messageDigest.digest(source.getBytes("UTF-8"));
      return ByteString.of(md5Bytes).hex();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return source;
  }

  //base64字符串转化成图片
  public static boolean GenerateImage(Context context, String imgStr) {   //对字节数组字符串进行Base64解码并生成图片
    if (imgStr == null) //图像数据为空
    {
      return false;
    }
    try {
      //Base64解码
      byte[] b = Base64.decode(imgStr, Base64.DEFAULT);
      for (int i = 0; i < b.length; ++i) {
        if (b[i] < 0) {//调整异常数据
          b[i] += 256;
        }
      }
      //生成jpeg图片
      String fileName =
          new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()) + ".png";
      String imgFilePath =
          Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM) + "/" + fileName;
      OutputStream out = new FileOutputStream(imgFilePath);
      out.write(b);
      out.flush();
      out.close();
      notifyMedia(context, imgFilePath, fileName);

      return true;
    } catch (Exception e) {
      Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
      return false;
    }
  }

  public static void saveImage(Context context, String data) {
    if (TextUtils.isEmpty(data)) {
      return;
    }

    if (data.contains(";base64,")) {
      String[] str = data.split(";base64,");
      Utils.GenerateImage(context, str[str.length - 1]);
    } else {
      CompositeDisposable mDisposables = new CompositeDisposable();
      mDisposables.add(PictureBiz.getDownloadPictures(data)
          .subscribe(new Consumer<Bitmap>() {
            @Override
            public void accept(Bitmap bm) throws Exception {
              //成功
              LogUtils.e("downloadImage", "   ----  bitmap  " + bm);
              if (bm != null) {
                save2Album(context, bm, new SimpleDateFormat("yyyyMMddHHmmss"
                    , Locale.getDefault()).format(new Date()) + ".jpg");
              } else {
                Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
              }
            }
          }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
              Log.e("downloadImage", "   失败   " + throwable.getMessage());
              Toast.makeText(context, "网页禁止下载图片", Toast.LENGTH_SHORT).show();
              //失败
            }
          }));
    }
  }

  private static void save2Album(Context context, Bitmap bitmap, String fileName) {
    File file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM), fileName);
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
      fos.flush();
      fos.close();
      notifyMedia(context, file.getAbsolutePath(), fileName);
    } catch (Exception e) {
      Toast.makeText(context, "保存失败", Toast.LENGTH_SHORT).show();
      e.printStackTrace();
    } finally {
      try {
        fos.close();
      } catch (Exception ignored) {
      }
    }
  }

  public static void notifyMedia(Context context, String filePath, String fileName) {
    //把文件插入到系统图库
    try {
      MediaStore.Images.Media.insertImage(context.getContentResolver(),
          filePath, fileName, null);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    //通知图库更新
    context.sendBroadcast(
        new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
    Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
  }

    public static void setStatusBarBgColor(Activity activity,int statusColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            //取消状态栏透明
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //添加Flag把状态栏设为可绘制模式
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(statusColor);
            //设置系统状态栏处于可见状态
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            //让view不根据系统窗口来调整自己的布局
            ViewGroup mContentView = window.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                ViewCompat.setFitsSystemWindows(mChildView, false);
                ViewCompat.requestApplyInsets(mChildView);
            }
        }
    }

    public static void checkContactPermission(Activity activity) {
        new RxPermissions(activity)
                .request("android.permission.READ_CONTACTS")
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            BrowserUtils.openContact(activity);
                        }
                    }
                });
    }
}