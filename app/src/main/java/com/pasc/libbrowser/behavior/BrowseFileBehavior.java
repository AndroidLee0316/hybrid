// package com.pasc.libbrowser.behavior;
//
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.util.Log;
//import android.widget.Toast;
//import com.google.gson.Gson;
//import com.pasc.lib.hybrid.behavior.BehaviorHandler;
//import com.pasc.lib.hybrid.callback.CallBackFunction;
//import com.pasc.lib.smtbrowser.entity.NativeResponse;
//import com.pasc.lib.hybrid.eh.activity.FileBrowseActivity;
//import com.pasc.libbrowser.data.WebFileBean;
//import com.pasc.libbrowser.utils.FileUtil;
//import com.pasc.libbrowser.utils.OnHttpHeaderListener;
//import java.io.Serializable;
//
// /**
// * create by wujianning385 on 2018/8/3.
// */
//public class BrowseFileBehavior implements BehaviorHandler,Serializable {
//
//
//    @Override public void handler(final Context context, String data, CallBackFunction function,
//            NativeResponse response) {
//        try {
//            Gson gson = new Gson();
//            WebFileBean webFileBean = gson.fromJson(data, WebFileBean.class);
//            final String fileUrl = webFileBean.fileUrl;
//            webFileBean.inAppBrowse = true;
//            if (webFileBean.inAppBrowse) {
//                FileUtil.getHttpFileName(fileUrl, new OnHttpHeaderListener() {
//                    @Override public void httpHeader(String fileName) {
//                        Log.v("AAAAA", "fileNAme : " + fileName);
//                        if (fileName.endsWith(".doc")
//                                || fileName.endsWith(".xls")
//                                || fileName.endsWith(".pdf")
//                                || fileName.endsWith(".PDF")
//                                || fileName.endsWith(".ppt")
//                                || fileName.endsWith(".docx")
//                                || fileName.endsWith(".xlsx")
//                                || fileName.endsWith(".pptx")
//                                || fileName.endsWith(".txt")) {
//                            FileBrowseActivity.show(context, fileUrl, fileName);
//                        }
//                    }
//                });
//            }else {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(fileUrl));
//                // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
//                // 官方解释 : Name of the component implementing an activity that can display the intent
//                if (intent.resolveActivity(context.getPackageManager()) != null) {
//                    ComponentName componentName = intent.resolveActivity(context.getPackageManager());
//                    context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
//                } else {
//                    Toast.makeText(context,"请下载浏览器",Toast.LENGTH_LONG).show();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
