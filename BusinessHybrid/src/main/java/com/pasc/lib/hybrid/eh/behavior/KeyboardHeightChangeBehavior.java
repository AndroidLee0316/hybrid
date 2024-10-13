package com.pasc.lib.hybrid.eh.behavior;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.PascWebviewFragment;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.hybrid.listener.KeyboardListener;
import com.pasc.lib.hybrid.webview.PascWebView;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import java.io.Serializable;

public class KeyboardHeightChangeBehavior implements BehaviorHandler, Serializable {
  @Override public void handler(Context context, String data, CallBackFunction callBackFunction,
      NativeResponse nativeResponse) {
    try {
      KeyboardHeightParams keyboardHeightBean =
          new Gson().fromJson(data, KeyboardHeightParams.class);
      if (context instanceof PascWebviewActivity) {
        PascWebviewActivity activity = (PascWebviewActivity) context;
        final String action = keyboardHeightBean.action;

        activity.addKeyboardListener(action,
            new KeyboardListener.OnKeyboardListener() {
              @Override public void onKeyboardOpened(int keyboardHeight) {
                returnData(activity, action, px2dp(context, keyboardHeight));
              }

              @Override public void onKeyboardChanged(int keyboardHeight) {
                returnData(activity, action, px2dp(context, keyboardHeight));
              }

              @Override public void onKeyboardClose() {
                returnData(activity, action, 0);
              }
            });
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      callBackFunction.onCallBack(new Gson().toJson(nativeResponse));
    }
  }

  private static int px2dp(Context context, final int pxValue) {
    if (context == null) {
      throw new RuntimeException("Context must not be null.");
    }
    float density = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / density + 0.5f);
  }

  private void returnData(PascWebviewActivity activity, String action, int keyboardHeight) {
    PascWebviewFragment webViewFragment = activity.mWebviewFragment;
    if (webViewFragment != null) {
      PascWebView webView = webViewFragment.mWebView;
      if (webView != null) {
        KeyboardHeightResult returnData = new KeyboardHeightResult();
        returnData.height = keyboardHeight;
        String resultJson = new Gson().toJson(returnData);
        webView.callHandler(action, resultJson, null);
      }
    }
  }

  public static class KeyboardHeightResult {
    @SerializedName("height")
    public int height;
  }

  public static class KeyboardHeightParams {
    @SerializedName("action")
    public String action;
  }
}
