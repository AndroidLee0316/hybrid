package com.pasc.libbrowser.behavior;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.ActivityResultCallback;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.smtbrowser.entity.NativeResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Created by buyongyou on 2018/12/27.
 * @Email: buyongyou490@pingan.com.cn
 * @des
 */
public class SelectPicBehavior implements BehaviorHandler,Serializable  {
    private static final int REQUEST_CODE_CHOOSE = 10088;

    @Override
    public void handler(Context context, String s, final CallBackFunction callBackFunction, final NativeResponse nativeResponse) {
//        Log.i(SelectPicBehavior.class.getName(),s);
//        PascHybrid.getInstance().setActivityResultCallback(new ActivityResultCallback() {
//            @Override
//            public void activityResult(Intent intent, int i) {
//                if (i == PascWebviewActivity.REQUEST_CODE_FILE_SELECT) {
//                    List<String> paths = Matisse.obtainPathResult(intent);
//
//                    HashMap map = new HashMap<>();
//                    ArrayList list = new ArrayList();
//                    map.put("tempFiles", list);
//                    for (String path : paths) {
//                        HashMap diu = new HashMap<>();
//                        //path的value值为磁盘路径且必须加上前缀协议PascHybrid.PROTOFUL
//                        diu.put("path", PascHybrid.PROTOFUL + path);
//                        diu.put("size", 0L);
//                        list.add(diu);
//                    }
//                    //必须添加，否者不允许访问
//                    PascHybrid.getInstance().addAuthorizationPath(paths);
//                    nativeResponse.code=0;
//                    nativeResponse.data=map;
//                    callBackFunction.onCallBack(new Gson().toJson(nativeResponse));
//                }
//            }
//        });
//        if (context instanceof Activity) {
//            Matisse.from((Activity) context)
//                    .choose(MimeType.ofAll())
//                    .countable(true)
//                    .maxSelectable(9)
////                    .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
////                    .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
//                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
//                    .thumbnailScale(0.85f)
//                    .imageEngine(new PicassoEngine())
//                    .forResult(PascWebviewActivity.REQUEST_CODE_FILE_SELECT);
//        }

    }
}
