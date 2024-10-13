package com.pasc.lib.hybrid.callback;

/**
 * create by wujianning385 on 2018/10/25.
 */
public interface ErrorPageback {

  /**
   * 设置错误页按钮文字
   */
  CharSequence RetryLoadText ();

  /**
   * 设置错误页提示文字
   */
  CharSequence EmptyTtipsText ();

  /**
   * 设置错误页图片
   */
  int EmptyIcon ();
}
