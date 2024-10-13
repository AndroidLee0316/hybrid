package com.pasc.libbrowser.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.chenkun305.libbrowser.R;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.behavior.ConstantBehaviorName;
import com.pasc.lib.smtbrowser.entity.RespShareBean;
import com.pasc.lib.smtbrowser.entity.WebShareBean;
import com.pasc.libbrowser.App;
import com.pasc.libbrowser.Constants;
import com.pasc.libbrowser.utils.BitmapUtils;
import com.pasc.libbrowser.utils.DeviceUtils;
import com.pasc.libbrowser.utils.FileDownLoadObserver;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import java.util.ArrayList;
import java.util.List;


/**
 * 针对于需要调用分享的封装
 * Created by ruanwei489 on 2018/6/15.
 */

public class WebShareCommonDialog extends Dialog {

    private Tencent mTencent;
    private IUiListener listener;//qq分享的回调  自己实现
    //private List<ShareInfo> datas;
    private List<WebShareBean.ExtInfo> datas;
    public Context mContext;
    private String defaultPictureUrl =
            "https://iobs.pingan.com.cn/download/szsc-smt-app-dmz-prd/15d0d3ce-9de9-41ab-a7fc-4a6f2f14395e_1533312882783";

    //public ShareBean shareBean;
    public WebShareBean shareBean;

    public OnShareDismissListener onDismissListener;

    public WebShareCommonDialog(Context context, int resId,
            @NonNull List<WebShareBean.ExtInfo> list, WebShareBean shareBean,
            OnShareDismissListener listener) {
        super(context, R.style.style_dialog_select_item);
        checkNotNull(resId, "resId should not be null");
        setContentView(resId);
        this.mContext = context;
        checkNotNull(list, "list should not be null");
        this.datas = list;
        checkNotNull(shareBean, "shareBean should not be null");
        this.shareBean = shareBean;
        setLayoutParams(context, resId);

        this.onDismissListener = listener;

        mTencent = Tencent.createInstance(Constants.QQ_APP_ID, context.getApplicationContext());
    }

    public WebShareCommonDialog(Context context, int resId,
            @NonNull List<WebShareBean.ExtInfo> list, WebShareBean shareBean, IUiListener listener,
            OnShareDismissListener onShareDismissListener) {
        super(context, R.style.style_dialog_select_item);
        checkNotNull(resId, "resId should not be null");
        setContentView(resId);
        this.mContext = context;
        checkNotNull(list, "list should not be null");
        this.datas = list;
        checkNotNull(shareBean, "shareBean should not be null");
        this.shareBean = shareBean;
        setLayoutParams(context, resId);
        checkNotNull(listener, "listener should not be null");
        this.listener = listener;

        this.onDismissListener = onShareDismissListener;

        mTencent = Tencent.createInstance(Constants.QQ_APP_ID, context.getApplicationContext());
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference an object reference
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     *     string using {@link String#valueOf(Object)}
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference, @Nullable Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    private void setLayoutParams(Context context, int resId) {
        int widthPixels = DeviceUtils.getWindowWidth(context);
        View contentView = LayoutInflater.from(context).inflate(resId, null);
        setContentView(contentView);

        setCancel(contentView);
        setRecycleView(contentView);

        ViewGroup.MarginLayoutParams params =
                (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = widthPixels;
        contentView.setLayoutParams(params);
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setGravity(Gravity.BOTTOM);
    }

    private void setCancel(View contentView) {
        TextView cancleView = (TextView) contentView.findViewById(R.id.rtv_cancle);
        cancleView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void setRecycleView(View contentView) {
        RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager =
                new GridLayoutManager(contentView.getContext(), 4);
        recyclerView.setLayoutManager(layoutManager);
        BaseQuickAdapter adapter;
        //WebShareBean.ExtInfo browser = new WebShareBean.ExtInfo();
        //browser.setPlatformID(6);
        //WebShareBean.ExtInfo copyLinkInfo = new WebShareBean.ExtInfo();
        //copyLinkInfo.setPlatformID(7);
        //datas.add(browser);
        //datas.add(copyLinkInfo);
        recyclerView.setAdapter(adapter = new ShareCommonAdapter(datas));
        for (int i = 0; i < datas.size(); i++) {
            WebShareBean.ExtInfo extInfo = datas.get(i);
            switch (extInfo.getPlatformID()) {
                case 0:
                    extInfo.resId = R.mipmap.ic_web_share_sms;
                    extInfo.introduce = getContext().getString(R.string.sms);
                    break;
                case 1:
                    extInfo.resId = R.mipmap.ic_web_share_wechat;
                    extInfo.introduce = getContext().getString(R.string.wechat_friend);
                    break;
                case 2:
                    extInfo.resId = R.mipmap.ic_web_share_friend_circle;
                    extInfo.introduce = getContext().getString(R.string.friends);
                    break;
                case 3:
                    extInfo.resId = R.mipmap.ic_web_share_qq;
                    extInfo.introduce = getContext().getString(R.string.qq);
                    break;
                case 4:
                    extInfo.resId = R.mipmap.ic_web_share_qq_zone;
                    extInfo.introduce = getContext().getString(R.string.qq_zone);
                    break;
                case 5:
                    extInfo.resId = R.mipmap.ic_web_share_weibo;
                    extInfo.introduce = getContext().getString(R.string.weibo);
                    break;
                case 6:
                    extInfo.resId = R.mipmap.ic_web_share_browser;
                    extInfo.introduce = getContext().getString(R.string.browser_open);
                    break;
                case 7:
                    extInfo.resId = R.mipmap.ic_web_share_copy_link;
                    extInfo.introduce = getContext().getString(R.string.copy_link);
                    break;
            }
        }

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TextView textView = (TextView) view.findViewById(R.id.introduce);
                String title = textView.getText().toString().trim();
                WebShareBean.ExtInfo extInfo = (WebShareBean.ExtInfo) adapter.getItem(position);

                if (null != extInfo) {
                    RespShareBean respShareBean = new RespShareBean(extInfo.getPlatformID());
                    PascHybrid.getInstance().triggerCallbackFunction(ConstantBehaviorName.OPEN_SHARE, respShareBean);
                    switch (extInfo.getPlatformID()) {
                        case 1:
                            //微信好友
                            sendMessage2WX(mContext, SendMessageToWX.Req.WXSceneSession,
                                    shareBean.getShareUrl(), shareBean.getTitle(),
                                    shareBean.getContent(), shareBean.getImage());

                            break;
                        case 2:
                            //微信朋友圈
                            sendMessage2WX(mContext, SendMessageToWX.Req.WXSceneTimeline,
                                    shareBean.getShareUrl(), shareBean.getTitle(),
                                    shareBean.getContent(), shareBean.getImage());

                            break;
                        case 3:
                            //qq好友
                            share2QQFriends(listener);
                            break;
                        case 4:
                            //qq空间
                            share2QZone(listener);
                            break;
                        case 5:
                            //微博
                            break;
                        case 6:
                            //浏览器打开
                            Uri uri = Uri.parse(shareBean.getShareUrl());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            getContext().startActivity(intent);
                            break;
                        case 7:
                            //复制链接
                            ClipboardManager cm = (ClipboardManager) getContext().getSystemService(
                                    Context.CLIPBOARD_SERVICE);
                            // 创建普通字符型ClipData
                            ClipData mClipData =
                                    ClipData.newPlainText("share", shareBean.getShareUrl());
                            // 将ClipData内容放到系统剪贴板里。
                            if (cm != null) {
                                cm.setPrimaryClip(mClipData);
                                Toast.makeText(mContext,mContext.getText(R.string.copy_link_success),Toast.LENGTH_LONG).show();
                            }
                            break;
                        case 0:
                            sendSms(mContext, shareBean.getContent());

                            break;
                    }
                }

                if (onDismissListener != null) {
                    onDismissListener.selectMode(title);
                }
                dismiss();
            }
        });
    }

    /**
     * 分享消息到好友
     */
    private void share2QQFriends(final IUiListener listener) {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareBean.getTitle());// 标题
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareBean.getContent());// 摘要
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareBean.getShareUrl());// 内容地址
        String pictureUrl = defaultPictureUrl;
        if (shareBean.getImage() != null && shareBean.getImage().startsWith("http")) {
            pictureUrl = shareBean.getImage();
        }
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
                pictureUrl);// 网络图片地址　　params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "应用名称");// 应用名称
        params.putString(QQShare.SHARE_TO_QQ_EXT_INT, "其它附加功能");
        // 分享操作要在主线程中完成
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                mTencent.shareToQQ((Activity) mContext, params, listener);
            }
        });
    }

    /**
     *
     */
    private void share2QZone(final IUiListener listener) {
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
                QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareBean.getTitle());// 标题
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareBean.getContent());// 摘要
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareBean.getShareUrl());// 内容地址
        ArrayList<String> imgUrlList = new ArrayList<>();
        String pictureUrl = defaultPictureUrl;
        if (shareBean.getImage() != null && shareBean.getImage().startsWith("http")) {
            pictureUrl = shareBean.getImage();
        }
        imgUrlList.add(pictureUrl);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgUrlList);// 图片地址

        // 分享操作要在主线程中完成
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override public void run() {
                mTencent.shareToQzone((Activity) mContext, params, listener);
            }
        });
    }

    public static class ShareCommonAdapter
            extends BaseQuickAdapter<WebShareBean.ExtInfo, BaseViewHolder> {

        public ShareCommonAdapter(@Nullable List<WebShareBean.ExtInfo> data) {
            super(R.layout.item_web_share_common, data);
        }

        @Override protected void convert(BaseViewHolder helper, WebShareBean.ExtInfo item) {

            helper.setImageResource(R.id.icon, item.resId);
            helper.setText(R.id.introduce, item.introduce);
        }
    }

    private static void sendMessage2WX(final Context context, final int type, final String url,
            final String title, final String description, final String imageUrl) {

        if (TextUtils.isEmpty(imageUrl)) {
            shareData(context, type, url, title, description, null);
            return;
        }


        BitmapUtils.downloadFileByBitmap(imageUrl, new FileDownLoadObserver<Bitmap>() {
            @Override public void onDownLoadSuccess(Bitmap bitmap) {

                shareData(context, type, url, title, description, bitmap);
            }

            @Override public void onDownLoadFail(Throwable throwable) {

                shareData(context, type, url, title, description, null);
            }
        });
    }

    public static void shareData(Context context, int type, String url, final String title,
            final String description, Bitmap bitmap) {

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;

        if (bitmap == null) {
            bitmap = BitmapUtils.drawableToBitamp(
                    context.getResources().getDrawable(R.mipmap.ic_about_logo));
        }
        try {
            msg.thumbData = BitmapUtils.Bitmap2Bytes(BitmapUtils.compress(bitmap, 32));
        } catch (Exception e) {
            e.printStackTrace();
            msg.thumbData = null;
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = type;
        App.api.sendReq(req);
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }

    @Override public void dismiss() {
        super.dismiss();
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    /**
     * 短信分享
     */
    public static Boolean sendSms(Context context, String smstext) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        mIntent.putExtra("sms_body", smstext);
        context.startActivity(mIntent);
        return null;
    }
}
