package com.pasc.libbrowser.behavior;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.example.chenkun305.libbrowser.R;
import com.google.gson.Gson;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import com.pasc.libbrowser.data.WebCallPhoneBean;
import com.pasc.libbrowser.view.SelectReminderDialog;
import java.io.Serializable;

/**
 * create by wujianning385 on 2018/8/9.
 */
public class CallPhoneBehavior implements BehaviorHandler,Serializable {


    @Override public void handler(Context context, String data, CallBackFunction function,
            NativeResponse response) {
        try {
            Gson gson = new Gson();
            WebCallPhoneBean webCallPhone = gson.fromJson(data,WebCallPhoneBean.class);
            showCallDialog(context, webCallPhone.phoneNum);
            function.onCallBack(gson.toJson(response));
        }catch (RuntimeException e){
            e.printStackTrace();
        }

    }

    /**
     Util
     * 拨号弹窗
     * @param context
     * @param phone
     */
    public static void showCallDialog(final Context context, final String phone){
        if(context instanceof Activity &&((Activity)context).isFinishing()){
            return;
        }
        SelectReminderDialog dialog =
                new SelectReminderDialog(context);
        dialog.setCancelText("取消");
        dialog.setCancelTextColor(R.color.gray_8d8d8d);
        dialog.setConfirmText("呼叫");
        dialog.setConfirmTextColor(R.color.blue_469cf1);
        dialog.setmTvContext(phone);
        dialog.setOnSelectedListener(new SelectReminderDialog.OnSelectedListener() {
            @Override public void onSelected() {
                call(context, phone);
            }

            @Override public void onCancel() {
            }
        });
        dialog.show();

    }

    /**
     * 调用拨号界面
     *
     * @param context
     * @param phone
     */
    public static void call(Context context, String phone) {

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
