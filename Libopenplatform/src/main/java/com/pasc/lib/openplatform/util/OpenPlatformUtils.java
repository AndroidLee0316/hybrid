package com.pasc.lib.openplatform.util;

import android.app.Activity;
import com.alibaba.android.arouter.launcher.ARouter;
import com.pasc.lib.openplatform.AuthProtocolService;
import org.json.JSONException;
import org.json.JSONObject;

public class OpenPlatformUtils {

  public static void toAuthProtocol(Activity activity, String appId, String unionId,
      String serviceName) {
    if (activity == null) {
      return;
    }
    AuthProtocolService authProtocolService =
        ARouter.getInstance().navigation(AuthProtocolService.class);
    if (authProtocolService != null) {
      JSONObject params = new JSONObject();
      try {
        params.put("appId", appId);
        params.put("unionId", unionId);
        params.put("serviceName", serviceName);
      } catch (JSONException e) {
        e.printStackTrace();
      }
      authProtocolService.run(activity, params);
    }
  }
}
