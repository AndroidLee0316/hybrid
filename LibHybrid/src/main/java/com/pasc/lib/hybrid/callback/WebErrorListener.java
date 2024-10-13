package com.pasc.lib.hybrid.callback;

/**
 * create by wujianning385 on 2018/9/17.
 */
public interface WebErrorListener {

  void onWebError(int errorCode, String description,String failingUrl);
}
