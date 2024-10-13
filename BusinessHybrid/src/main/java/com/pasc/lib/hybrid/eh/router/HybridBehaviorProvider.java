package com.pasc.lib.hybrid.eh.router;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pasc.lib.hybrid.eh.behavior.BrowseFileBehavior;
import com.pasc.lib.hybrid.eh.behavior.CallPhoneBehavior;
import com.pasc.lib.hybrid.eh.behavior.GetUserInfoBehavior;
import com.pasc.lib.hybrid.eh.behavior.PreviewPhotoBehavior;
import com.pasc.lib.hybrid.eh.behavior.ShareBehavior;


@Route(path = HybridBehaviorService.PATH_HYBRID_SERVICE_BEHAVIOR)
public class HybridBehaviorProvider implements IBehavior {

    @Override
    public void init(Context context) {

    }

    @Override
    public ShareBehavior getShareBehavior() {
        return new ShareBehavior();
    }

    @Override
    public GetUserInfoBehavior getUserInfoBehavior() {
        return new GetUserInfoBehavior();
    }

    @Override
    public CallPhoneBehavior getCallPhoneBehavior() {
        return new CallPhoneBehavior();
    }

    @Override
    public BrowseFileBehavior getBrowseFileBehavior() {
        return new BrowseFileBehavior();
    }

    @Override
    public PreviewPhotoBehavior getPreviewPhotoBehavior() {
        return new PreviewPhotoBehavior();
    }


}
