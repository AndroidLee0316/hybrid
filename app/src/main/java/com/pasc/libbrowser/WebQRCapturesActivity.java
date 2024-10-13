//package com.pasc.libbrowser;
//
//import android.Manifest;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import com.example.chenkun305.libbrowser.R;
//import com.journeyapps.barcodescanner.DecoratedBarcodeView;
//import com.pasc.lib.hybrid.PascHybrid;
//import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
//import com.pasc.lib.smtbrowser.entity.ScanBean;
//import com.pasc.libbrowser.capture.CustomCaptureManager;
//import com.pasc.libbrowser.event.QRResultEvent;
//import com.tbruyelle.rxpermissions2.Permission;
//import com.tbruyelle.rxpermissions2.RxPermissions;
//import io.reactivex.functions.Consumer;
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
///**
// * web的二维码扫描界面
// * Created by wjn .
// */
// public class WebQRCapturesActivity
//        extends AppCompatActivity implements View.OnClickListener {
//
//    private static final int REQUEST_PICK_IMAGE = 12;
//
//    private CustomCaptureManager capture;
//    private DecoratedBarcodeView barcodeScannerView;
//    private volatile boolean mFinishQuery = true;
//    private String mBarcode = "";
//
//    TextView iv_back;
//    TextView tvBack;
//    LinearLayout llPayCode;
//    TextView tvGallery;
//    RelativeLayout rlBottom;
//    View topView;
//
//    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_custom_captures);
//        iv_back = (TextView) findViewById(R.id.iv_capture_back);
//        tvBack = (TextView) this.findViewById(R.id.iv_capture_back);
//        llPayCode = (LinearLayout) this.findViewById(R.id.ll_pay_code);
//        tvGallery = (TextView) this.findViewById(R.id.tv_gallery);
//        rlBottom = (RelativeLayout)this.findViewById(R.id.rl_bottom);
//        topView = (View)this.findViewById(R.id.top_view);
//        tvBack.setOnClickListener(this);
//        llPayCode.setOnClickListener(this);
//        tvGallery.setOnClickListener(this);
//        tvGallery.setVisibility(View.GONE);
//        rlBottom.setVisibility(View.GONE);
//        barcodeScannerView = initializeContent();
//
//        capture = new CustomCaptureManager(this, barcodeScannerView);
//        capture.initializeFromIntent(getIntent(), savedInstanceState);
//        capture.decode();
//        topView.postDelayed(new Runnable() {
//            @Override public void run() {
//                requestPermissions();
//            }
//        },200);
//
//        EventBus.getDefault().register(this);
//    }
//
//    @Override protected void onResume() {
//        super.onResume();
//        capture.onResume();
//        capture.resScan();
//    }
//
//    @Override protected void onPause() {
//        super.onPause();
//        capture.onPause();
//    }
//
//    @Override protected void onDestroy() {
//        super.onDestroy();
//        capture.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }
//
//    @Override protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        capture.onSaveInstanceState(outState);
//    }
//
//
//    //@Override public void onRequestPermissionsResult(int requestCode, String permissions[],
//    //        int[] grantResults) {
//    //
//    //    Map map = new HashMap();
//    //    if (grantResults != null && grantResults.length > 0) {
//    //        if (grantResults[0] == 0) {
//    //            map.put("开启相机", "开启");
//    //        } else {
//    //            map.put("开启相机", "取消");
//    //        }
//    //    } else {
//    //        map.put("开启相机", "取消");
//    //    }
//    //    //埋点
//    //    //EventUtils.onEvent(this, EventUtils.E_COMMUNITY_FOOD_DETECT, "是否开启相机权限", map);
//    //    if (null != capture) {
//    //        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    //    }
//    //}
//
//    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
//    }
//
//    public boolean ismFinishQuery() {
//        return mFinishQuery;
//    }
//
//    public void handleBarCodetoServer(String barcode) {
//        if (mFinishQuery) {
//            mFinishQuery = false;
//            mBarcode = barcode;
//            ScanBean scanBean = new ScanBean(barcode);
//            PascHybrid.getInstance().triggerCallbackFunction(ConstantBehaviorName.QR_CODE_SCAN, scanBean);
//            finish();
//        }
//    }
//
//    /**
//     * Override to use a different layout.
//     *
//     * @return the DecoratedBarcodeView
//     */
//    protected DecoratedBarcodeView initializeContent() {
//
//        //        setContentView(R.layout.activity_custom_capture);
//        DecoratedBarcodeView view = (DecoratedBarcodeView) findViewById(R.id.dbv_custom);
//        return view;
//    }
//
//    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case REQUEST_PICK_IMAGE:
//                if (data != null) {
//                    Uri uri = data.getData();
//                    if (uri != null) {
//                        Log.e(WebQRCapturesActivity.class.getName(),uri.toString());
//                    }
//                }
//                break;
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN) public void onQRResult(QRResultEvent qrResultEvent) {
//        if (ismFinishQuery()) {
//            handleBarCodetoServer(qrResultEvent.result);
//        }
//    }
//
//    @Override public void onClick(View view) {
//        int i = view.getId();
//        if (i == R.id.iv_capture_back) {
//            finish();
//        } else if (i == R.id.ll_pay_code) {
//            finish();
//        } else if (i == R.id.tv_gallery) {
//            Intent intent = new Intent("android.intent.action.PICK");
//            intent.setDataAndType(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "image/*");
//            startActivityForResult(intent, REQUEST_PICK_IMAGE);
//        }
//    }
//
//    private void requestPermissions() {
//        RxPermissions rxPermission = new RxPermissions(this);
//        rxPermission.requestEach(Manifest.permission.CAMERA).subscribe(new Consumer<Permission>() {
//            @Override public void accept(Permission permission) throws Exception {
//                if (permission.granted) {
//                    // 用户已经同意该权限
//                    //capture.onResume();
//                    //capture.resScan();
//                } else if (permission.shouldShowRequestPermissionRationale) {
//                    // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
//                } else {
//                    // 用户拒绝了该权限，并且选中『不再询问』
//                }
//            }
//        });
//    }
//
//}
