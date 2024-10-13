 package com.pasc.libbrowser.behavior;

 import android.content.Context;
 import android.util.Log;
 import com.google.gson.Gson;
 import com.pasc.lib.hybrid.behavior.BehaviorHandler;
 import com.pasc.lib.hybrid.callback.CallBackFunction;
 import com.pasc.lib.smtbrowser.entity.NativeResponse;
 import com.pasc.libbrowser.data.WebLogBean;
 import java.io.Serializable;

 /**
 * create by wujianning385 on 2018/8/3.
 */
public class WebLogBehavior implements BehaviorHandler,Serializable{

    public static final String TAG = WebLogBehavior.class.getSimpleName();

    /*
     context:当前webView的context
     data：H5端传给我们的数据
     function：通过CallBackFunction对象返回给H5数据
     response: 智慧城hybrid协商的返回数据格式对象，调用方可自行修改对象参数，具体文档：
     http://iqsz-d6889:8090/pages/viewpage.action?pageId=4852675
     */
    @Override public void handler(Context context, String data, CallBackFunction function,
            NativeResponse response) {
        Gson gson = new Gson();
        WebLogBean webLog = gson.fromJson(data,WebLogBean.class);
        switch (webLog.level){
            case 0:
                Log.i(TAG,webLog.info);
                break;
            case 1:
                Log.i(TAG,webLog.info);
                break;
            case 2:
                Log.w(TAG,webLog.info);
                break;
            case 3:
                Log.e(TAG,webLog.info);
                break;
        }
      function.onCallBack(gson.toJson(response));
    }
}
