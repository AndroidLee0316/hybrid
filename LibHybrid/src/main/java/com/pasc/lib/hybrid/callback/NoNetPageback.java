package com.pasc.lib.hybrid.callback;

/**
 * create by wujianning385 on 2018/10/25.
 */
public interface NoNetPageback {

  /**
   * 设置无网页按钮文字
   */
  CharSequence RetryLoadText ();

  /**
   * 设置无网页提示文字
   */
  CharSequence EmptyTtipsText ();

  /**
   * 设置无网页图片
   */
  int EmptyIcon ();
}
