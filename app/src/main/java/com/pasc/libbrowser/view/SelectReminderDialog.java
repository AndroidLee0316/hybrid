package com.pasc.libbrowser.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.example.chenkun305.libbrowser.R;

public class SelectReminderDialog extends Dialog {

    private final View view;
    public TextView mTitle;
    private TextView mTvContext;
    private TextView mTvConfirm;
    private TextView mTvCancel;

    public SelectReminderDialog(Context context) {
        super(context, R.style.RoundDialog);
        setContentView(R.layout.dialog_select_alarm);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mTvContext = (TextView) findViewById(R.id.tv_context);
        mTvConfirm = (TextView) findViewById(R.id.tv_confirm);
        mTvCancel = (TextView) findViewById(R.id.tv_cancel);
        view  = findViewById(R.id.view);
    }

    public SelectReminderDialog(Context context,int layoutId) {
        super(context, R.style.RoundDialog);
        setContentView(layoutId);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mTvContext = (TextView) findViewById(R.id.tv_context);
        mTvConfirm = (TextView) findViewById(R.id.tv_confirm);
        mTvCancel = (TextView) findViewById(R.id.tv_cancel);
        view  = findViewById(R.id.view);
    }

    public void setOnSelectedListener(final OnSelectedListener onSelectedListener) {

        mTvConfirm.setOnClickListener(new View.OnClickListener() {//确认
            @Override
            public void onClick(View v) {
                onSelectedListener.onSelected();
                dismiss();
            }
        });

        mTvCancel.setOnClickListener(new View.OnClickListener() {//取消
            @Override
            public void onClick(View v) {
                onSelectedListener.onCancel();
                dismiss();
            }
        });
    }


    public void setmTvContext(String context) {//设置对话框内容
        mTvContext.setText(context);
        mTvContext.setVisibility(TextUtils.isEmpty(context)?View.GONE:View.VISIBLE);

    }

    public void setConfirmText(String context) {//设置确认按钮内容
        mTvConfirm.setText(context);
    }

    public void setCancelText(String context) {//设置取消按钮内容
        mTvCancel.setText(context);
    }

    public void setConfirmTextColor(int color) {
        mTvConfirm.setTextColor(getContext().getResources().getColor(color));
    }

    public void setCancelTextColor(int color) {
        mTvCancel.setTextColor(getContext().getResources().getColor(color));
    }

    public TextView getmTvConfirm() {
        return mTvConfirm;
    }

    public TextView getmTvCancel() {
        return mTvCancel;
    }

    public View getView() {
        return view;
    }

    public void setOnPositiveSelectedListener(final OnPositiveSelectedListener onSelectedListener) {

        mTvConfirm.setOnClickListener(new View.OnClickListener() {//确认
            @Override
            public void onClick(View v) {
                onSelectedListener.onSelected();
                dismiss();
            }
        });

    }

    public void setTitleText(String title) {
        mTitle.setText(title);
        mTitle.setVisibility(TextUtils.isEmpty(title)?View.GONE:View.VISIBLE);
    }

    public void setmTvContext(int certification) {
        mTvContext.setText(certification);
        mTvContext.setVisibility(TextUtils.isEmpty(mTvContext.getText().toString().trim())?View.GONE:View.VISIBLE);
    }

    public interface OnSelectedListener {
        void onSelected();

        void onCancel();
    }


    public interface OnPositiveSelectedListener {
        void onSelected();
    }


}