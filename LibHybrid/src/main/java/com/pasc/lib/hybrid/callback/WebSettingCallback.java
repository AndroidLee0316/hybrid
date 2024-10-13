package com.pasc.lib.hybrid.callback;

import com.tencent.smtt.sdk.WebSettings;

import java.io.Serializable;

public interface WebSettingCallback extends Serializable {

    /**
     * 外部控制settings，一定要设置usergent
     * @param settings
     */
    void setWebSettings(WebSettings settings);
}
