package com.pasc.lib.hybrid.callback;

import android.content.Context;

import com.pasc.lib.hybrid.widget.WebCommonTitleView;

public interface ToolBarCallback {
    /**
     * 在toolbar上设置下拉recycle
     */
    void toolBarRecycleCallback(WebCommonTitleView webCommonTitleView, Context context, boolean isShow);
}
