package com.pasc.lib.hybrid.eh.router;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;


public interface IBehavior extends IProvider {


    <T extends BehaviorHandler> T getShareBehavior();

    <T extends BehaviorHandler> T getCallPhoneBehavior();

    <T extends BehaviorHandler> T getUserInfoBehavior();

    <T extends BehaviorHandler> T getBrowseFileBehavior();

    <T extends BehaviorHandler> T getPreviewPhotoBehavior();


}
