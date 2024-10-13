package com.pasc.libbrowser.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;
import com.example.chenkun305.libbrowser.R;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.hybrid.behavior.WebPageConfig;
import com.pasc.lib.hybrid.eh.behavior.BrowseFileBehavior;
import com.pasc.lib.hybrid.eh.behavior.KeyboardHeightChangeBehavior;
import com.pasc.lib.hybrid.eh.behavior.OPGetGPSInfoBehavior;
import com.pasc.lib.hybrid.nativeability.WebStrategy;
import com.pasc.lib.hybrid.nativeability.WebStrategyType;
import com.pasc.libbrowser.Constants;
import com.pasc.libbrowser.behavior.BackToHomeBehavior;
import com.pasc.libbrowser.behavior.CallPhoneBehavior;
import com.pasc.libbrowser.behavior.MapNavigationBehavior;
import com.pasc.libbrowser.behavior.OpenPermissionSettingBehavior;
import com.pasc.libbrowser.behavior.PreviewPhotoBehavior;
import com.pasc.libbrowser.behavior.ScanQRBehavior;
import com.pasc.libbrowser.behavior.ShareBehavior;
import com.pasc.libbrowser.behavior.WebLogBehavior;
import com.pasc.libbrowser.behavior.WebStatsEventBehavior;
import com.pasc.libbrowser.behavior.WebStatsPageBehavior;
import com.pasc.libbrowser.utils.RSAUtil;
import java.security.KeyPair;

/**
 * create by wujianning385 on 2018/9/3.
 */
public class Fragment2 extends Fragment {

  private Button button, button2, fileSelectBtn;
  private EditText editText;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_2, container, false);

    View sparseArrayView = view.findViewById(R.id.sparseArrayView);
    sparseArrayView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        SparseArray<String> array = new SparseArray<>(4);
        array.put(1, "one");
        array.put(2, "two");
        array.put(3, "three");
        array.put(4, "four");
        array.put(5, "five");
        array.put(6, null); // 可以放空对象
        System.out.println("six=" + array.get(6));
        array.put(1, "seven"); // 可以覆盖
        System.out.println("seven=" + array.get(1));

        System.out.println("size=" + array.size());
        Toast.makeText(getActivity(), "测试成功", Toast.LENGTH_LONG).show();
      }
    });

    View coverView = view.findViewById(R.id.coverView);
    coverView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //PascWebviewActivity.startWebviewActivity(getActivity(),"https://smt-web-stg4.pingan.com.cn/sz/app/feature/blockchain/?uiparams=%7B%22isHide%22:true,%22isWebImmersive%22:true%7D#/qrcode");
        PascWebviewActivity.startWebviewActivity(getActivity(),
            "https://smt-web-stg4.pingan.com.cn/sz/app/feature/blockchain-authorization/#/scene-auth/home/1");
      }
    });

    button = (Button) view.findViewById(R.id.button_1);
    button2 = (Button) view.findViewById(R.id.button_2);
    editText = (EditText) view.findViewById(R.id.et_url);
    fileSelectBtn = view.findViewById(R.id.button_file_select);


    Button errorPageView = (Button) view.findViewById(R.id.errorPageView);
    errorPageView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        String url = "http://203.91.43.189:7009/iApp/Petition/page/toAdvise/toAdvise-Rule.html";
        openHybrid(url);
      }
    });

    button.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        String url = "http://smt-open.yun.city.pingan.com/demo/base/?area=jixian-stg3#/";
        openHybrid(url);
      }
    });

    button2.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        //QbSdk.forceSysWebView();
        WebPageConfig webPageConfig = new WebPageConfig.Builder()
            //.addCustomerBehavior(Constants.WEB_BEHAVIOR_NAME_NATIVE_ROUTE, new NativeRouteBehavior())
            //.addCustomerBehavior(Constants.WEB_BEHAVIOR_GET_USER_INFO, new GetUserBehavior())
            .addCustomerBehavior(ConstantBehaviorName.CLOSE_WITH_BACK_HOME,
                new BackToHomeBehavior())
            .addCustomerBehavior(ConstantBehaviorName.GET_GPS_INFO, new OPGetGPSInfoBehavior())
            .addCustomerBehavior(ConstantBehaviorName.OPEN_MAP_NAVIGATION,
                new MapNavigationBehavior())
            .addCustomerBehavior(ConstantBehaviorName.QR_CODE_SCAN, new ScanQRBehavior())
            .addCustomerBehavior(Constants.WEB_BEHAVIOR_PREVIEW_IMAGES, new PreviewPhotoBehavior())
            .addCustomerBehavior(Constants.WEB_BEHAVIOR_BROWSE_FILE, new BrowseFileBehavior())
            .addCustomerBehavior(ConstantBehaviorName.STATISTICS_EVENT, new WebStatsEventBehavior())
            .addCustomerBehavior(ConstantBehaviorName.STATISTICS_PAGE, new WebStatsPageBehavior())
            .addCustomerBehavior(ConstantBehaviorName.LOG_EVENT, new WebLogBehavior())
            .addCustomerBehavior(Constants.WEB_BEHAVIOR_OPEN_SETTING,
                new OpenPermissionSettingBehavior())
            .addCustomerBehavior(ConstantBehaviorName.CALL_PHONE, new CallPhoneBehavior())
            .addCustomerBehavior(ConstantBehaviorName.OP_PHONE_CALL, new CallPhoneBehavior())
            .create();


        String url = editText.getText().toString();
        //              String url = "http://www.baidu.com";
        //        String url =
        //                "http://smt-web-stg.pingan.com.cn/sz/app/activity/2018/national-day-absorb-talents/?openweb=paschybrid";
        //              String url = "http://101.89.80.186:8106/";
        PascHybrid.getInstance()
            .with(webPageConfig)
            .start(getActivity(), new WebStrategy().setUrl(url)
                .setStatusBarVisibility(WebStrategyType.STATUSBAR_VISIBLE));
      }
    });

    fileSelectBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
//        String url = "file:////android_asset/test_file_select.html";
//        PascHybrid.getInstance()
//                .start(getActivity(), new WebStrategy().setUrl(url)
//                        .setStatusBarVisibility(WebStrategyType.STATUSBAR_VISIBLE));

        //String url = "https://smt-stg.yun.city.pingan.com/shenzhen/app/jimu/10219/?uiparams=%7B%22isHide%22:true,%22isWebImmersive%22:true%7D&vt=qxj_gzh0521#/";
//        String url = "https://isz-web.sz.gov.cn/sz/app/jimu/10046/?uiparams=%7B%22isHide%22:true,%22isWebImmersive%22:true%7D#/";
//        PascHybrid.getInstance()
//                .start(getActivity(), new WebStrategy().setUrl(url));

        PascHybrid.getInstance().start(getActivity(), new WebStrategy().setUrl("https://smt-stg.yun.city.pingan.com/zaozhuang/stg/app/feature/news/?uiparams=%7B\\\"isHide\\\":true%7D#/article/532069abea234dbbbe1cceecaa208b1a"));
      }
    });

    String str0 = "123";
    KeyPair keyPair = RSAUtil.generateRSAKeyPair();
    //用公钥加密
    try {
      byte[] encrypt =
          RSAUtil.encryptByPublicKey(str0.getBytes(), keyPair.getPublic().getEncoded());
      //用私钥解密
      byte[] decrypt = RSAUtil.decryptByPrivateKey(encrypt, keyPair.getPrivate().getEncoded());
      Log.d("TAG", "解密后的数据：" + new String(decrypt, "utf-8"));
    } catch (Exception e) {
      e.printStackTrace();
    }

    //
    //PublicKey publicKey = RSAUtil.generateRSAKeyPair().getPublic();
    //PrivateKey privateKey = RSAUtil.generateRSAKeyPair().getPrivate();
    //////公钥用base64编码
    //String encodePublic = Base64Util.encode(publicKey.getEncoded());
    ////私钥用base64编码
    //String encodePrivate = Base64Util.encode(privateKey.getEncoded());
    //Log.d("wjn", "base64编码的公钥：" + encodePublic);
    //Log.d("wjn", "base64编码的私钥：" + encodePrivate);
    //String str1 = "123";
    ////InputStream inPublic = null;
    //try {
    ////  inPublic = getResources().getAssets().open("rsa_public_key.pem");
    //  PublicKey publicKeyTemp = RSAUtil.loadPublicKey(encodePublic);
    //  byte[] encryptData = RSAUtil.encryptData(str1.getBytes(),publicKeyTemp);
    //  String afterEncrypt = Base64Util.encode(encryptData);
    //  //InputStream inPrivate = getResources().getAssets().open("rsa_private_key.pem");
    //  PrivateKey privateKeyTemp = RSAUtil.loadPrivateKey(encodePrivate);
    //  byte[] decryptByte = RSAUtil.decryptData(Base64Util.decode(afterEncrypt),privateKeyTemp);
    //  String data = new String(decryptByte, StandardCharsets.UTF_8);
    //  Log.d("wjn", "解密后字符换：" + data.trim());
    //} catch (Exception e) {
    //  e.printStackTrace();
    //}

    //RSAUtil.printPublicKeyInfo(publicKey);

    Button szOpenPlatformView = view.findViewById(R.id.szOpenPlatformView);
    szOpenPlatformView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        String url = "http://smt-open.yun.city.pingan.com/demo/shenzhen/#/";
        openHybrid(url);
      }
    });

    return view;
  }

  private void openHybrid(String url) {
    WebPageConfig webPageConfig = new WebPageConfig.Builder()
        //.addCustomerBehavior(Constants.WEB_BEHAVIOR_NAME_NATIVE_ROUTE, new NativeRouteBehavior())
        //.addCustomerBehavior(Constants.WEB_BEHAVIOR_GET_USER_INFO, new GetUserBehavior())
        .addCustomerBehavior(ConstantBehaviorName.CLOSE_WITH_BACK_HOME,
            new BackToHomeBehavior())
        .addCustomerBehavior(ConstantBehaviorName.GET_GPS_INFO, new OPGetGPSInfoBehavior())
        .addCustomerBehavior(ConstantBehaviorName.OPEN_MAP_NAVIGATION,
            new MapNavigationBehavior())
        .addCustomerBehavior(ConstantBehaviorName.QR_CODE_SCAN, new ScanQRBehavior())
        .addCustomerBehavior(Constants.WEB_BEHAVIOR_PREVIEW_IMAGES, new PreviewPhotoBehavior())
        .addCustomerBehavior(Constants.WEB_BEHAVIOR_BROWSE_FILE, new BrowseFileBehavior())
        .addCustomerBehavior(ConstantBehaviorName.STATISTICS_EVENT, new WebStatsEventBehavior())
        .addCustomerBehavior(ConstantBehaviorName.STATISTICS_PAGE, new WebStatsPageBehavior())
        .addCustomerBehavior(ConstantBehaviorName.LOG_EVENT, new WebLogBehavior())
        .addCustomerBehavior(Constants.WEB_BEHAVIOR_OPEN_SETTING,
            new OpenPermissionSettingBehavior())
        .addCustomerBehavior(ConstantBehaviorName.CALL_PHONE, new CallPhoneBehavior())
        .addCustomerBehavior(ConstantBehaviorName.OP_PHONE_CALL, new CallPhoneBehavior())
        .addCustomerBehavior("PASC.app.onKeyboardHeightChange", new KeyboardHeightChangeBehavior())

        .create();

    //String url =
    //        "http://smt-open.yun.city.pingan.com/demo/shenzhen/?openweb=paschybrid&area=shenzhen#/";
    //String url = "http://smt-web-stg.pingan.com.cn/sz/app/lab/hybrid/test/index.html?openweb=paschybrid";

//    PascHybrid.getInstance()
//        .with(webPageConfig)
//        .start(getActivity(), new WebStrategy().setUrl(url));

    PascHybrid.getInstance()
            .with(webPageConfig).start(getActivity(),url);
  }
}
