package com.pasc.lib.hybrid.eh.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pasc.lib.base.util.ScreenUtils;
import com.pasc.lib.hybrid.eh.R;

import java.io.Serializable;

public class HybridEHChooseOptionDialog extends Dialog implements Serializable{
    public static final String TAG = "ChooseOptionDialog";
    private TextView tvFirst;
    private TextView tvSecond;
    private TextView tvCancel;

    public HybridEHChooseOptionDialog(Context context, int resId) {
        super(context, R.style.choose_option_dialog);
        int widthPixels = ScreenUtils.getScreenWidth();
        View contentView = LayoutInflater.from(context).inflate(resId, (ViewGroup)null);
        this.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)contentView.getLayoutParams();
        params.width = widthPixels ;
        contentView.setLayoutParams(params);
        this.getWindow().getDecorView().setPadding(0, 0, 0, 0);
        this.getWindow().setGravity(Gravity.BOTTOM);
        this.tvFirst = (TextView)contentView.findViewById(R.id.tv_first);
        this.tvSecond = (TextView)contentView.findViewById(R.id.tv_second);
        this.tvCancel = (TextView)contentView.findViewById(R.id.tv_cancel);
        //this.setLayoutParams(context, resId);
    }

    public HybridEHChooseOptionDialog(Context context, int resId,String firstStr,String secondStr) {
        super(context, R.style.choose_option_dialog);
        int widthPixels = ScreenUtils.getScreenWidth();
        View contentView = LayoutInflater.from(context).inflate(resId, (ViewGroup)null);
        this.setContentView(contentView);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)contentView.getLayoutParams();
        params.width = widthPixels ;
        contentView.setLayoutParams(params);
        this.getWindow().getDecorView().setPadding(0, 0, 0, 0);
        this.getWindow().setGravity(Gravity.BOTTOM);
        this.tvFirst = (TextView)contentView.findViewById(R.id.tv_first);
        this.tvSecond = (TextView)contentView.findViewById(R.id.tv_second);
        this.tvCancel = (TextView)contentView.findViewById(R.id.tv_cancel);
        this.tvFirst.setText(firstStr);
        this.tvSecond.setText(secondStr);

    }

    //private void setLayoutParams(Context context, int resId) {
    //    int widthPixels = ScreenUtils.getScreenWidth();
    //    View contentView = LayoutInflater.from(context).inflate(resId, (ViewGroup)null);
    //    this.setContentView(contentView);
    //    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)contentView.getLayoutParams();
    //    params.width = widthPixels ;
    //    contentView.setLayoutParams(params);
    //    this.getWindow().getDecorView().setPadding(0, 0, 0, 0);
    //    this.getWindow().setGravity(Gravity.BOTTOM);
    //}

    public void setOnSelectedListener(final OnSelectedListener onSelectedListener) {

        tvFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSelectedListener != null) {
                    onSelectedListener.onFirst();
                }

                HybridEHChooseOptionDialog.this.dismiss();
            }
        });

        tvSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSelectedListener != null) {
                    onSelectedListener.onSecond();
                }

                HybridEHChooseOptionDialog.this.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HybridEHChooseOptionDialog.this.dismiss();
            }
        });
    }

    public interface OnSelectedListener {
        void onFirst();

        void onSecond();

        void onCancel();
    }
}
