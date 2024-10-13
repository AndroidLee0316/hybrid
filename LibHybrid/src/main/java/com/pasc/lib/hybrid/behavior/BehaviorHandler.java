package com.pasc.lib.hybrid.behavior;

import android.content.Context;
import android.support.annotation.UiThread;

import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.smtbrowser.entity.NativeResponse;

/*
 * Copyright (C) 2018 pasc Licensed under the Apache License, Version 1.0 (the "License");
 * @author chenshangyong872
 * @date 2018-07-15
 * @des行为处理器的抽象接口
 * @version 1.0
 */
public interface BehaviorHandler {
    /**
     * 交互行为处理方法
     * @param context webview 的context，供UI使用
     * @param data 前端H5返回的数据
     * @param function  调用该接口，给前端返回数据
     * @param response 给前端的response，该数据转成json字符串通过function返回,记得给data赋值
     */
    @UiThread
    void handler(Context context, String data, CallBackFunction function, NativeResponse response);
}
