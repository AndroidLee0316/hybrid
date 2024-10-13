package com.pasc.lib.hybrid.eh.behavior;

import android.content.Context;
import com.google.gson.Gson;
import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.hybrid.eh.bean.PascPayBean;
import com.pasc.lib.smtbrowser.entity.NativeResponse;

/**
 * create by wujianning385 on 2019-08-05.
 */
public class PascPayBehavior implements BehaviorHandler {


  @Override public void handler(Context context, String data, CallBackFunction function,
      NativeResponse response) {
    if (context instanceof PascWebviewActivity) {

      PascPayBean pascPayBean = new Gson().fromJson(data,PascPayBean.class);

      PascWebviewActivity webviewActivity = (PascWebviewActivity) context;
      if (webviewActivity.mWebviewFragment!=null&&webviewActivity.mWebviewFragment.mWebView!=null){

        webviewActivity.mWebviewFragment.mWebView.callHandler(pascPayBean.action,"此处传支付状态",null);
      }
    }

  }
}
