package com.pasc.libbrowser.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.example.chenkun305.libbrowser.R;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.hybrid.behavior.WebPageConfig;
import com.pasc.lib.hybrid.eh.behavior.BrowseFileBehavior;
import com.pasc.lib.hybrid.eh.zxing.activity.CaptureActivity;
import com.pasc.lib.hybrid.nativeability.WebStrategy;
import com.pasc.libbrowser.Constants;
import com.pasc.libbrowser.behavior.ScanQRBehavior;
import com.pasc.libbrowser.utils.PermissionUtils;
import com.tencent.smtt.sdk.QbSdk;
import io.reactivex.functions.Consumer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;

/**
 * create by wujianning385 on 2018/9/3.
 */
public class Fragment1 extends Fragment {

    public static final String TAG = "HYBRID_TEST";
    private Button button1, button2, bt3;
    TextView qrCodeText;

    //打开扫描界面请求码
    private int REQUEST_CODE = 0x01;
    //扫描成功返回码
    private int RESULT_OK = 0xA1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_1, container, false);
        qrCodeText = view.findViewById(R.id.qrCodeText);

        button1 = (Button) view.findViewById(R.id.button_1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button1Click();
            }
        });

        button2 = (Button) view.findViewById(R.id.button_2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button2Click();
            }
        });

        bt3 = (Button) view.findViewById(R.id.bt3);
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //打开二维码扫描界面
                PermissionUtils.request(getActivity(), Manifest.permission.CAMERA).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            Intent intent = new Intent(getActivity(), CaptureActivity.class);
                            startActivityForResult(intent, REQUEST_CODE);
                        }
                    }
                });

            }
        });

        View base64View = view.findViewById(R.id.base64View);
        base64View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(new File("/sdcard/DCIM/Camera/IMG_20131221_015634.jpg"));

                    StringBuffer buf = new StringBuffer();

                    ArrayList<String> strings = new ArrayList<>();
                    do {
                        Log.d(TAG, "读取图片内容");
                        byte[] bytes = IOUtils.toByteArray(inputStream);
                        byte[] encode = Base64.encode(bytes, Base64.NO_WRAP);
                        String imageBase64 = new String(encode);
                        strings.add(imageBase64);
                        buf.append(imageBase64);
                    } while (inputStream.read() != -1);

                    String result = buf.toString();
                    Log.d(TAG, "图片base64编码后. imageBase64=" + result);
                    Log.d(TAG, "图片base64编码后Encode. imageBase64=" + URLEncoder.encode(result));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        return view;
    }

    public void button1Click() {

        //String url1 =
        //        "https://smt-web-stg.pingan.com.cn/sz/app/feature/jianguanwei-service/?uiparams=%7B%22isHide%22:true,%22isWebImmersive%22:true%7D#/";
//        PascHybrid.getInstance().start(getActivity(), new WebStrategy().setUrl(url1));
//        Intent i = new Intent(getActivity(), DataSecretaryListActivity.class);
//        startActivity(i);
        WebStrategy webStrategy = new WebStrategy ();
        webStrategy.setUrl("http://www.baidu.com");
        webStrategy.isNotice = "1";
        webStrategy.serviceProvider = "深圳市人力资源和社会保障局";
        webStrategy.pageShowDuration = 3000;
        PascHybrid.getInstance ().start (getActivity(), webStrategy);
    }

    public void button2Click() {
        QbSdk.forceSysWebView();
        WebPageConfig webPageConfig = new WebPageConfig.Builder()
//                .addCustomerBehavior(ConstantBehaviorName.OPEN_SHARE, new ShareBehavior())
//                .addCustomerBehavior(ConstantBehaviorName.CLOSE_WITH_BACK_HOME, new BackToHomeBehavior())
//                .addCustomerBehavior(ConstantBehaviorName.GET_GPS_INFO, new OPGetGPSInfoBehavior())
//                .addCustomerBehavior(ConstantBehaviorName.OPEN_MAP_NAVIGATION, new MapRouteBehavior())
                .addCustomerBehavior(ConstantBehaviorName.QR_CODE_SCAN, new ScanQRBehavior())
//                .addCustomerBehavior(Constants.WEB_BEHAVIOR_PREVIEW_IMAGES,new PreviewPhotoBehavior())
                .addCustomerBehavior(Constants.WEB_BEHAVIOR_BROWSE_FILE, new BrowseFileBehavior())
//                .addCustomerBehavior(ConstantBehaviorName.STATISTICS_EVENT,new WebStatsEventBehavior())
//                .addCustomerBehavior(ConstantBehaviorName.STATISTICS_PAGE,new WebStatsPageBehavior())
//                .addCustomerBehavior(ConstantBehaviorName.LOG_EVENT,new WebLogBehavior())
//                .addCustomerBehavior(Constants.WEB_BEHAVIOR_OPEN_SETTING,new OpenPermissionSettingBehavior())
//                .addCustomerBehavior(ConstantBehaviorName.CALL_PHONE,new CallPhoneBehavior())

                .create();
        //String url = "http://www.sd12320.gov.cn/mobileLoginController/toYyghFromOtherPlat.html?p=8d53e9e7b865ce5d09497ccbfa7357360deef0e8ab931dd2e8a243f14d34f097536a2c12a5cf44c5bb2f55c6bdb280f0b1183a7f61e7ba715dec1ebd221b1176";

        String url = "https://smt-stg.yun.city.pingan.com/shenzhen/app/jimu/10219/?uiparams=%7B%22isHide%22:true,%22isWebImmersive%22:true%7D&vt=qxj_gzh0521#/";

        Log.e("aaaaa","============== start url : " + url);
        PascHybrid.getInstance().start(getActivity(), new WebStrategy()
                .setUrl(url));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (resultCode == RESULT_OK) { //RESULT_OK = -1
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("qr_scan_result");
            //将扫描出的信息显示出来
            qrCodeText.setText(scanResult);
        }
    }

}
