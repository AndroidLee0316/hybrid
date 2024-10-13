package com.pasc.lib.hybrid.behavior;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.pasc.lib.hybrid.PascWebviewActivity;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.hybrid.widget.CommonDialog;
import com.pasc.lib.smtbrowser.entity.NativeResponse;

import java.io.Serializable;
import java.util.List;

/**
 * 功能：
 * <p>
 * created by zoujianbo345
 * data : 2018/9/20
 */
public class OpenDialogBehavior implements BehaviorHandler, Serializable {
    @Override
    public void handler(final Context context, String data, CallBackFunction callBackFunction, final NativeResponse nativeResponse) {
        try {
            Gson gson = new Gson();
            final DialogParam dialogParam = gson.fromJson(data, DialogParam.class);
            if (dialogParam.buttons.size() == 1) {
                new CommonDialog(context)
                        .setTitle(dialogParam.title)
                        .setContent(dialogParam.message)
                        .setButton1(dialogParam.buttons.get(0).title)
                        .setOnButtonClickListener(new CommonDialog.OnButtonClickListener() {
                            @Override
                            public void button1Click() {
                                super.button1Click();
                                nativeResponse.data = dialogParam.message;
                                ((PascWebviewActivity) context).mWebviewFragment
                                        .callHandler(dialogParam.buttons.get(0).action, "", new CallBackFunction() {
                                            @Override
                                            public void onCallBack(String s) {

                                            }
                                        });
                            }

                        })
                        .show();
            } else if (dialogParam.buttons.size() == 2) {
                new CommonDialog(context)
                        .setTitle(dialogParam.title)
                        .setContent(dialogParam.message)
                        .setButton1(dialogParam.buttons.get(0).title)
                        .setButton2(dialogParam.buttons.get(1).title)
                        .setOnButtonClickListener(new CommonDialog.OnButtonClickListener() {
                            @Override
                            public void button1Click() {
                                super.button1Click();
                                nativeResponse.data = dialogParam.message;
                                ((PascWebviewActivity) context).mWebviewFragment
                                        .callHandler(dialogParam.buttons.get(0).action, "", new CallBackFunction() {
                                            @Override
                                            public void onCallBack(String s) {

                                            }
                                        });

                            }

                            @Override
                            public void button2Click() {
                                super.button2Click();
                                nativeResponse.data = dialogParam.message;
                                ((PascWebviewActivity) context).mWebviewFragment
                                        .callHandler(dialogParam.buttons.get(1).action, "", new CallBackFunction() {
                                            @Override
                                            public void onCallBack(String s) {

                                            }
                                        });
                            }

                        })
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class DialogParam {
        @SerializedName("title")
        public String title;
        @SerializedName("message")
        public String message;
        @SerializedName("buttons")
        public List<DialogButton> buttons;

        public static class DialogButton {
            @SerializedName("title")
            public String title;
            @SerializedName("action")
            public String action;
        }
    }
}
