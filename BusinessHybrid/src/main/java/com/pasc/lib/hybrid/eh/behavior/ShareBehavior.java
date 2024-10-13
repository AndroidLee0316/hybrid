package com.pasc.lib.hybrid.eh.behavior;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.share.ShareManager;
import com.pasc.lib.share.callback.ShareActionListener;
import com.pasc.lib.share.config.ShareContent;
import com.pasc.lib.share.config.SharePlatformConfig;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import com.pasc.lib.smtbrowser.entity.RespShareBean;
import com.pasc.lib.smtbrowser.entity.WebShareBean;

import java.io.Serializable;
import java.util.List;


/**
 * create by wujianning385 on 2018/8/1.
 */
public class ShareBehavior implements BehaviorHandler,Serializable {


    @Override
    public void handler(Context context, String data, CallBackFunction function,
                        NativeResponse response) {
        PascHybrid.getInstance().saveCallBackFunction(context.hashCode(),ConstantBehaviorName.OPEN_SHARE,function);
        WebShareBean share = new Gson().fromJson(data,WebShareBean.class);
        ShareContent.Builder contentBuilder = new ShareContent.Builder();
        contentBuilder.setContent(share.getContent())
                .setTitle(share.getTitle())
                .setShareUrl(share.getShareUrl())
                .setImageUrl(share.getImage());

        SharePlatformConfig.Builder platformBuilder = new SharePlatformConfig.Builder();
        List<WebShareBean.ExtInfo> sharebeanExtInfos =  share.getShareType();
        for(int i = 0;i<sharebeanExtInfos.size();i++){
            if (sharebeanExtInfos.get(i) == null){
                continue;
            }

            switch (sharebeanExtInfos.get(i).getPlatformID()){
                case 0 :
                    platformBuilder.setSMS();
                    contentBuilder.setSmsContent(sharebeanExtInfos.get(i).getContent());

                case 1 :
                    platformBuilder.setWX();

                case 2 :
                    platformBuilder.setWxCircle();

                case 3 :
                    platformBuilder.setQQ();

                case 4 :
                    platformBuilder.setQZONE();

                case 5 :

                case 6 :

                case 7 :
                    platformBuilder.setCopyLink();

                case 8 :
                    platformBuilder.setEmail();

                case 999 :
                    platformBuilder.setMore();
                    contentBuilder.setMoreContent(sharebeanExtInfos.get(i).getContent());

            }
        }

        ShareManager.getInstance()
                .setPlatformConfig(platformBuilder.build())
                .shareContent((Activity) context, contentBuilder.build(),  new ShareActionListener() {
                    @Override
                    public void onComplete(int i) {

                    }

                    @Override
                    public void onCancel(int i) {

                    }

                    @Override
                    public void onError(int i, Throwable throwable) {

                    }

                    @Override
                    public void onPlatformClick(int i) {

                        int platformIdForH5 = -1;
                        String platformName = ""; // 埋点用

                        switch (i) {
                            case ShareManager.PLATFORM_WX:
                                platformName = "微信";
                                platformIdForH5 = 1;
                                break;
                            case ShareManager.PLATFORM_QQ:
                                platformName = "QQ";
                                platformIdForH5 = 3;
                                break;
                            case ShareManager.PLATFORM_WX_CIRCLE:
                                platformName = "微信朋友圈";
                                platformIdForH5 = 2;
                                break;
                            case ShareManager.PLATFORM_QZONE:
                                platformName = "QQ空间";
                                platformIdForH5 = 4;
                                break;
                            case ShareManager.PLATFORM_SMS:
                                platformName = "短信";
                                platformIdForH5 = 0;
                                break;
                            case ShareManager.PLATFORM_EMAIL:
                                platformName = "邮箱";
                                platformIdForH5 = 8;
                                break;
                            case ShareManager.PLATFORM_COPY_LINK:
                                platformName = "复制链接";
                                platformIdForH5 = 7;
                                break;
                            case ShareManager.PLATFORM_MORE:
                                platformName = "更多";
                                platformIdForH5 = 999;
                                break;
                        }

                        RespShareBean respShareBean = new RespShareBean(platformIdForH5);
                        PascHybrid.getInstance().triggerCallbackFunction(ConstantBehaviorName.OPEN_SHARE, respShareBean);
                    }
                });


    }
}
