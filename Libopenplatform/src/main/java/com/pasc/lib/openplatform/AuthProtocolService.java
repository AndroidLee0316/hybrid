package com.pasc.lib.openplatform;

import com.alibaba.android.arouter.facade.template.IProvider;
import org.json.JSONObject;

public interface AuthProtocolService extends IProvider {

  /**
   * 1.0.95版本后发生变更。appId和unionId均放在params里。
   *
   * @param context 上下文。
   * @param params 携带的参数，目前有appId,unionId和serviceName。
   */
  void run(Object context, JSONObject params);
}
