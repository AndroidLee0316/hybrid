//package com.pasc.libbrowser.capture;
//
//import android.Manifest;
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.content.pm.PackageManager;
//import android.content.res.Configuration;
//import android.graphics.Bitmap;
//import android.hardware.Camera;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v4.content.ContextCompat;
//import android.util.Log;
//import android.view.Display;
//import android.view.Surface;
//import android.view.Window;
//import android.view.WindowManager;
//import com.elvishew.xlog.XLog;
//import com.google.zxing.ResultMetadataType;
//import com.google.zxing.ResultPoint;
//import com.google.zxing.client.android.BeepManager;
//import com.google.zxing.client.android.InactivityTimer;
//import com.google.zxing.client.android.Intents;
//import com.journeyapps.barcodescanner.BarcodeCallback;
//import com.journeyapps.barcodescanner.BarcodeResult;
//import com.journeyapps.barcodescanner.CameraPreview;
//import com.journeyapps.barcodescanner.CaptureManager;
//import com.journeyapps.barcodescanner.DecoratedBarcodeView;
//import com.pasc.libbrowser.WebQRCapturesActivity;
//import com.pasc.libbrowser.event.CapturePermissionEvent;
//import com.pasc.libbrowser.event.QRResultEvent;
//import com.pasc.libbrowser.utils.DeviceUtils;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.lang.reflect.Field;
//import java.util.List;
//import java.util.Map;
//import org.greenrobot.eventbus.EventBus;
//
///**
// * Created by ex-wuhaiping001 on 2017/6/9.
// */
//
//public class CustomCaptureManager {
//    private static final String TAG = CaptureManager.class.getSimpleName();
//
//    public static int cameraPermissionReqCode = 250;
//
//    private Activity activity;
//    private DecoratedBarcodeView barcodeView;
//    private int orientationLock = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
//    private static final String SAVED_ORIENTATION_LOCK = "SAVED_ORIENTATION_LOCK";
//    private boolean returnBarcodeImagePath = false;
//
//    private boolean destroyed = false;
//
//    // Delay long enough that the beep can be played.
//    // TODO: play beep in background
//    private static final long DELAY_BEEP = 1000;
//
//    private InactivityTimer inactivityTimer;
//    private BeepManager beepManager;
//
//    private Handler handler;
//
//    private BarcodeCallback callback = new BarcodeCallback() {
//        @Override public void barcodeResult(final BarcodeResult result) {
//            XLog.i("barcodeResult");
//            handler.postDelayed(new Runnable() {
//                @Override public void run() {
//                    returnResult(result);
//                }
//            }, DELAY_BEEP);
//        }
//
//        @Override public void possibleResultPoints(List<ResultPoint> resultPoints) {
//
//        }
//    };
//
//    private final CameraPreview.StateListener stateListener = new CameraPreview.StateListener() {
//        @Override public void previewSized() {
//
//        }
//
//        @Override public void previewStarted() {
//
//        }
//
//        @Override public void previewStopped() {
//
//        }
//
//        @Override public void cameraError(Exception error) {
//            displayFrameworkBugMessageAndExit();
//        }
//    };
//
//    public CustomCaptureManager(Activity activity, DecoratedBarcodeView barcodeView) {
//        this.activity = activity;
//        this.barcodeView = barcodeView;
//        barcodeView.getBarcodeView().addStateListener(stateListener);
//
//        handler = new Handler();
//
//        inactivityTimer = new InactivityTimer(activity, new Runnable() {
//            @Override public void run() {
//                Log.d(TAG, "Finishing due to inactivity");
//                finish();
//            }
//        });
//
//        beepManager = new BeepManager(activity);
//    }
//
//    /**
//     * Perform initialization, according to preferences set in the intent.
//     *
//     * @param intent the intent containing the scanning preferences
//     * @param savedInstanceState saved state, containing orientation lock
//     */
//    public void initializeFromIntent(Intent intent, Bundle savedInstanceState) {
//        Window window = activity.getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//        if (savedInstanceState != null) {
//            // If the screen was locked and unlocked again, we may start in a different orientation
//            // (even one not allowed by the manifest). In this case we restore the orientation we were
//            // previously locked to.
//            this.orientationLock = savedInstanceState.getInt(SAVED_ORIENTATION_LOCK,
//                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//        }
//
//        if (intent != null) {
//            if (orientationLock == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
//                // Only lock the orientation if it's not locked to something else yet
//                boolean orientationLocked =
//                        intent.getBooleanExtra(Intents.Scan.ORIENTATION_LOCKED, true);
//
//                if (orientationLocked) {
//                    lockOrientation();
//                }
//            }
//
//            if (Intents.Scan.ACTION.equals(intent.getAction())) {
//                barcodeView.initializeFromIntent(intent);
//            }
//
//            if (!intent.getBooleanExtra(Intents.Scan.BEEP_ENABLED, true)) {
//                beepManager.setBeepEnabled(false);
//                beepManager.updatePrefs();
//            }
//
//            if (intent.hasExtra(Intents.Scan.TIMEOUT)) {
//                Runnable runnable = new Runnable() {
//                    @Override public void run() {
//                        returnResultTimeout();
//                    }
//                };
//                handler.postDelayed(runnable, intent.getLongExtra(Intents.Scan.TIMEOUT, 0L));
//            }
//
//            if (intent.getBooleanExtra(Intents.Scan.BARCODE_IMAGE_ENABLED, false)) {
//                returnBarcodeImagePath = true;
//            }
//        }
//    }
//
//    /**
//     * Lock display to current orientation.
//     */
//    protected void lockOrientation() {
//        // Only get the orientation if it's not locked to one yet.
//        if (this.orientationLock == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
//            // Adapted from http://stackoverflow.com/a/14565436
//            Display display = activity.getWindowManager().getDefaultDisplay();
//            int rotation = display.getRotation();
//            int baseOrientation = activity.getResources().getConfiguration().orientation;
//            int orientation = 0;
//            if (baseOrientation == Configuration.ORIENTATION_LANDSCAPE) {
//                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//                } else {
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
//                }
//            } else if (baseOrientation == Configuration.ORIENTATION_PORTRAIT) {
//                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270) {
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//                } else {
//                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
//                }
//            }
//
//            this.orientationLock = orientation;
//        }
//        //noinspection ResourceType
//        activity.setRequestedOrientation(this.orientationLock);
//    }
//
//    /**
//     * Start decoding.
//     */
//    public void decode() {
//        barcodeView.decodeSingle(callback);
//    }
//
//    public void resScan() {
//        barcodeView.decodeSingle(callback);
//    }
//
//    /**
//     * Call from Activity#onResume().
//     */
//    public void onResume() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            openCameraWithPermission();
//        } else {
//            barcodeView.resume();
//        }
//        beepManager.updatePrefs();
//        inactivityTimer.start();
//    }
//
//    @TargetApi(23) private void openCameraWithPermission() {
//        if (!DeviceUtils.isVivoMobilePhone()) {
//            if (ContextCompat.checkSelfPermission(this.activity, Manifest.permission.CAMERA)
//                    == PackageManager.PERMISSION_GRANTED) {
//
//                EventBus.getDefault().post(new CapturePermissionEvent(true));
//                barcodeView.resume();
//            } else {
//
//                EventBus.getDefault().post(new CapturePermissionEvent(false));
//            }
//        } else {
//            onRequestPermissions_vivo();
//        }
//    }
//
//    private boolean isHasPermission() {
//        Field fieldPassword = null;
//        try {
//            Camera camera = Camera.open();
//            fieldPassword = camera.getClass().getDeclaredField("mHasPermission");
//            fieldPassword.setAccessible(true);
//            return (boolean) fieldPassword.get(camera);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return true;
//        }
//    }
//
//    /**
//     * Call from Activity#onRequestPermissionsResult
//     */
//    public void onRequestPermissionsResult(int requestCode, String permissions[],
//            int[] grantResults) {
//        if (requestCode == cameraPermissionReqCode) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                EventBus.getDefault().post(new CapturePermissionEvent(true));
//                barcodeView.resume();
//            }
//            //            if (!DeviceUtils.isVivoMobilePhone()) {
//            //                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            //                    if(activity instanceof FoodDetectorActivity){
//            //                        ((FoodDetectorActivity)activity).hideNoPermission();
//            //                    }
//            //                    barcodeView.resume();
//            //                }
//            //            }  else {
//            //                if (isHasPermission()) {
//            //                    if(activity instanceof FoodDetectorActivity){
//            //                        ((FoodDetectorActivity)activity).hideNoPermission();
//            //                    }
//            //                    barcodeView.resume();
//            //                }
//            //            }
//        }
//    }
//
//    public void onRequestPermissions_vivo() {
//        if (isHasPermission()) {
//
//            EventBus.getDefault().post(new CapturePermissionEvent(true));
//            barcodeView.resume();
//        } else {
//            EventBus.getDefault().post(new CapturePermissionEvent(false));
//        }
//    }
//
//    /**
//     * Call from Activity#onPause().
//     */
//    public void onPause() {
//        barcodeView.pause();
//
//        inactivityTimer.cancel();
//        beepManager.close();
//    }
//
//    /**
//     * Call from Activity#onDestroy().
//     */
//    public void onDestroy() {
//        destroyed = true;
//        inactivityTimer.cancel();
//    }
//
//    /**
//     * Call from Activity#onSaveInstanceState().
//     */
//    public void onSaveInstanceState(Bundle outState) {
//        outState.putInt(SAVED_ORIENTATION_LOCK, this.orientationLock);
//    }
//
//    /**
//     * Create a intent to return as the Activity result.
//     *
//     * @param rawResult the BarcodeResult, must not be null.
//     * @param barcodeImagePath a path to an exported file of the Barcode Image, can be null.
//     * @return the Intent
//     */
//    public static Intent resultIntent(BarcodeResult rawResult, String barcodeImagePath) {
//        Intent intent = new Intent(Intents.Scan.ACTION);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//        intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
//        intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
//        byte[] rawBytes = rawResult.getRawBytes();
//        if (rawBytes != null && rawBytes.length > 0) {
//            intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
//        }
//        Map<ResultMetadataType, ?> metadata = rawResult.getResultMetadata();
//        if (metadata != null) {
//            if (metadata.containsKey(ResultMetadataType.UPC_EAN_EXTENSION)) {
//                intent.putExtra(Intents.Scan.RESULT_UPC_EAN_EXTENSION,
//                        metadata.get(ResultMetadataType.UPC_EAN_EXTENSION).toString());
//            }
//            Number orientation = (Number) metadata.get(ResultMetadataType.ORIENTATION);
//            if (orientation != null) {
//                intent.putExtra(Intents.Scan.RESULT_ORIENTATION, orientation.intValue());
//            }
//            String ecLevel = (String) metadata.get(ResultMetadataType.ERROR_CORRECTION_LEVEL);
//            if (ecLevel != null) {
//                intent.putExtra(Intents.Scan.RESULT_ERROR_CORRECTION_LEVEL, ecLevel);
//            }
//            @SuppressWarnings("unchecked") Iterable<byte[]> byteSegments =
//                    (Iterable<byte[]>) metadata.get(ResultMetadataType.BYTE_SEGMENTS);
//            if (byteSegments != null) {
//                int i = 0;
//                for (byte[] byteSegment : byteSegments) {
//                    intent.putExtra(Intents.Scan.RESULT_BYTE_SEGMENTS_PREFIX + i, byteSegment);
//                    i++;
//                }
//            }
//        }
//        if (barcodeImagePath != null) {
//            intent.putExtra(Intents.Scan.RESULT_BARCODE_IMAGE_PATH, barcodeImagePath);
//        }
//        return intent;
//    }
//
//    /**
//     * Save the barcode image to a temporary file stored in the application's cache, and return its path.
//     * Only does so if returnBarcodeImagePath is enabled.
//     *
//     * @param rawResult the BarcodeResult, must not be null
//     * @return the path or null
//     */
//    private String getBarcodeImagePath(BarcodeResult rawResult) {
//        String barcodeImagePath = null;
//        if (returnBarcodeImagePath) {
//            Bitmap bmp = rawResult.getBitmap();
//            try {
//                File bitmapFile =
//                        File.createTempFile("barcodeimage", ".jpg", activity.getCacheDir());
//                FileOutputStream outputStream = new FileOutputStream(bitmapFile);
//                bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                outputStream.close();
//                barcodeImagePath = bitmapFile.getAbsolutePath();
//            } catch (IOException e) {
//                Log.w(TAG, "Unable to create temporary file and store bitmap! " + e);
//            }
//        }
//        return barcodeImagePath;
//    }
//
//    private void finish() {
//        //        activity.jump2IndexActivity();
//        activity.finish();
//    }
//
//    protected void returnResultTimeout() {
//        Intent intent = new Intent(Intents.Scan.ACTION);
//        intent.putExtra(Intents.Scan.TIMEOUT, true);
//        activity.setResult(Activity.RESULT_CANCELED, intent);
//        finish();
//    }
//
//    protected void returnResult(BarcodeResult rawResult) {
//        Intent intent = resultIntent(rawResult, getBarcodeImagePath(rawResult));
//        //        activity.setResult(Activity.RESULT_OK, intent);
//        //        finish();
//        String barcode = intent.getStringExtra("SCAN_RESULT");
//        EventBus.getDefault().post(new QRResultEvent(barcode));
//
//
//        if (activity instanceof WebQRCapturesActivity) {
//            if (((WebQRCapturesActivity) activity).ismFinishQuery()) {
//                ((WebQRCapturesActivity) activity).handleBarCodetoServer(barcode);
//            }
//        }
//    }
//
//    protected void displayFrameworkBugMessageAndExit() {
//
//    }
//
//    public static int getCameraPermissionReqCode() {
//        return cameraPermissionReqCode;
//    }
//
//    public static void setCameraPermissionReqCode(int cameraPermissionReqCode) {
//        CustomCaptureManager.cameraPermissionReqCode = cameraPermissionReqCode;
//    }
//}
