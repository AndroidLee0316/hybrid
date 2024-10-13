package com.pasc.lib.hybrid.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.pasc.lib.hybrid.R;

/**
 * @date 2021-03-17
 * @des
 * @modify
 **/
public class ServiceLoadDialog extends Dialog {

    public interface ICallback{
        void delayFinish();
        void onBack();
    }

    public static class Builder {
        private String title = "";
        private String description = "您即将前往第三方服务";
        private String content = "";
        private int delay = 3000;
        private ICallback iCallback = new ICallback () {
            @Override
            public void delayFinish() {

            }

            @Override public void onBack() {

            }
        };

        public void setCallback(ICallback iCallback) {
            this.iCallback = iCallback;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }
        public Builder setDelay(int delay) {
            this.delay = delay;
            return this;
        }
        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }
        public Builder setContent(String content) {
            this.content = content;
            return this;
        }
    }
    private int what = 1010;
    private Builder builder;
    private Handler handler = new Handler (Looper.getMainLooper ()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == what){
                if (builder.iCallback!=null){
                    builder.iCallback.delayFinish ();
                }
                dismiss ();
            }
        }
    };

    public ServiceLoadDialog(@NonNull Context context, Builder bb) {
        super (context, R.style.ServicePoolFull);
        View view = LayoutInflater.from (context).inflate (R.layout.service_pool_load_dialog, null);
        setContentView (view);
        builder = bb;
        if (builder == null) {
            builder = new Builder ();
        }
        assignViews (context);

        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.horizontalMargin=0;
        params.verticalMargin=0;
        getWindow ().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(params);
        setCancelable (false);

    }

    @Override
    public void show() {
        handler.removeMessages (what);
        handler.sendEmptyMessageDelayed (what, builder.delay);
        super.show ();
    }

    @Override
    public void dismiss() {
        handler.removeMessages (what);
        super.dismiss ();
    }

    private TextView back;
    private TextView title;
    private ImageView waittingIcon;
    private TextView description;
    private TextView content;

    private void assignViews(Context context) {
        back = (TextView) findViewById (R.id.back);
        title = (TextView) findViewById (R.id.title);
        waittingIcon = findViewById(R.id.iv_waitting_icon);
        Glide.with(context).asGif().load(R.drawable.h5_waitting).into(waittingIcon);
        description = (TextView) findViewById (R.id.tv_description);
        content = (TextView) findViewById (R.id.tv_content);
        back.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                dismiss ();
                if (builder.iCallback != null){
                    builder.iCallback.onBack();
                }
            }
        });
        title.setText (builder.title);
        description.setText (builder.description);
        content.setText (context.getResources().getString(R.string.str_service_provider, builder.content));
    }

}
