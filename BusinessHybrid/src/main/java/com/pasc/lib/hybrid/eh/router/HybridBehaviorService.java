package com.pasc.lib.hybrid.eh.router;

import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.router.BaseJumper;

public class HybridBehaviorService {

    /*** 提供Behavior对象的Service*****/
    public static final String PATH_HYBRID_SERVICE_BEHAVIOR = "/web/service/manager";

    private static HybridBehaviorService instance;

    private static IBehavior iBehavior;

    public static HybridBehaviorService getInstance() {
        if (instance == null) {
            synchronized (HybridBehaviorService.class) {
                if (instance == null) {
                    instance = new HybridBehaviorService();
                }
            }
        }
        return instance;
    }

    private HybridBehaviorService(){
        iBehavior = getIBehavior();
    }


    public <T extends BehaviorHandler> T ShareBehaviorObject() {
        if (iBehavior == null) {
            return null;
        }
        return iBehavior.getShareBehavior();
    }

    public <T extends BehaviorHandler> T GetUserInfoBehaviorObject() {
        if (iBehavior == null) {
            return null;
        }
        return iBehavior.getUserInfoBehavior();
    }

    public <T extends BehaviorHandler> T CallPhoneBehaviorObject() {
        if (iBehavior == null) {
            return null;
        }
        return iBehavior.getCallPhoneBehavior();
    }

    public <T extends BehaviorHandler> T BrowseFileBehaviorObject() {
        if (iBehavior == null) {
            return null;
        }
        return iBehavior.getBrowseFileBehavior();
    }

    public <T extends BehaviorHandler> T PreviewPhotoBehaviorObject() {
        if (iBehavior == null) {
            return null;
        }
        return iBehavior.getPreviewPhotoBehavior();
    }




    /**
     * 浏览器业务提供给其他模块的接口
     */
    static IBehavior getIBehavior() {
        return BaseJumper.getService(PATH_HYBRID_SERVICE_BEHAVIOR);
    }
}
