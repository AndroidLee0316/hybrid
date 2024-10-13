package com.pasc.lib.hybrid.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.roundview.RoundTextView;
import com.pasc.lib.hybrid.R;
import com.pasc.lib.hybrid.util.Utils;




public class CommonDialog extends Dialog {
    public static final int Common_Center = 0;          //上面是内容下面一个或两个按钮（比如提示xxx，确认）
    public static final int Common_Bottom = 1;          //底部提示内容，接着多个按钮并列竖排（比如底部：是否确认退出登陆，确认，取消）
    public static final int Common_Top = 2;             //顶部下拉选择框

    public static final String Black_333333 = "#333333";
    public static final String Gray_999999 = "#999999";
    public static final String Red_f14431 = "#f14431";
    public static final String Blue_4d73f4 = "#4d73f4";
    public static final String Blue_22c8d8 = "#22C8D8";
    public static final String Transparent_00000000 = "#00000000";
    private OnButtonClickListener onButtonClickListener;
    private int type;              //布局类型
    private RoundTextView tvTitle;      //diaolog标题
    private RoundTextView tvContent;    //diaolog内容
    private View vDivider;         //中间弹框，并排两个按钮时的分隔线
    private View vDivider1;        //底部弹框内容和第一个按钮分隔线
    private View vDivider2;        //底部弹框第一个和第二个按钮分隔线
    private View vDivider3;        //底部弹框第二个和第三个按钮分隔线
    private boolean divider2show;  //第一个和第二个按钮分隔线是否显示
    private boolean divider3show;  //第二个和第三个按钮分隔线是否显示
    //根据实际情况填入按钮，确认，取消等,排列方式根据不同布局从左到右，从上到下
    private RoundTextView tvButton1;    //按钮1
    private RoundTextView tvButton2;    //按钮2
    private RoundTextView tvButton3;    //按钮3
    //各个位置的文字
    private String title;
    private String content;
    private String button1;
    private String button2;
    private String button3;
    //各个位置文字的颜色
    private String titleColor;
    private String contentColor;
    private String button1Color;
    private String button2Color;
    private String button3Color;
    private int cornerSize;

    //dialog类型
    @IntDef({Common_Center, Common_Bottom})
    public @interface DialogType {
    }

    //把ui要求dialog相关的颜色放在这里
    @StringDef({Black_333333, Gray_999999, Red_f14431, Blue_4d73f4,Blue_22c8d8, Transparent_00000000})
    public @interface DialogColor {
    }


    public CommonDialog(@NonNull Context context, @DialogType int type) {
        super(context,R.style.Theme_AppCompat_Dialog);
        //this.context = context;
        this.type = type;
        initView();

    }

    public CommonDialog(@NonNull Context context) {
        this(context, Common_Center);
    }

    private void initView() {
        int widthPixels = Utils.getScreenWidth(getContext());
        View contentView;
        ViewGroup.MarginLayoutParams params;
        switch (type) {
            case Common_Center:
                contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_common_center_hybrid, null);
                setContentView(contentView);
                params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
                params.width = widthPixels - Utils.dp2px(100);
                params.leftMargin = Utils.dp2px(100) / 2;
                params.rightMargin = Utils.dp2px(100) / 2;
                contentView.setLayoutParams(params);
                getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                tvTitle = findViewById(R.id.tv_title);
                tvContent = findViewById(R.id.tv_content);
                tvButton1 =  findViewById(R.id.tv_button1);
                vDivider =  findViewById(R.id.v_divider);
                tvButton2 =  findViewById(R.id.tv_button2);
                break;
            case Common_Bottom:
                contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_common_bottom_hybrid, null);
                setContentView(contentView);
                params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
                params.width = widthPixels - Utils.dp2px(12);
                params.bottomMargin = Utils.dp2px(10);
                contentView.setLayoutParams(params);
                getWindow().getDecorView().setPadding(0, 0, 0, 0);
                getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                getWindow().setGravity(Gravity.BOTTOM);
                tvContent = findViewById(R.id.tv_content);
                tvButton1 = findViewById(R.id.tv_button1);
                tvButton2 = findViewById(R.id.tv_button2);
                tvButton3 = findViewById(R.id.tv_button3);
                vDivider1 = (View) findViewById(R.id.v_divider1);
                vDivider2 = (View) findViewById(R.id.v_divider2);
                vDivider3 = (View) findViewById(R.id.v_divider3);
                break;

        }

        tvButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onButtonClickListener != null)
                    onButtonClickListener.button1Click();
            }
        });

        if (tvButton2 != null) {
            tvButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (onButtonClickListener != null)
                        onButtonClickListener.button2Click();
                }
            });
        }

        if (tvButton3 != null) {
            tvButton3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (onButtonClickListener != null)
                        onButtonClickListener.button3Click();
                }
            });
        }
    }

    //设置好各个参数以后，show方法显示最终的布局
    private void initCommonCenter() {
        //title样式如果没有设置值，就不显示
        if (title == null || TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
            if (titleColor != null) {
                tvTitle.setTextColor(Color.parseColor(contentColor));
            }
        }

        //content样式
        tvContent.setText(content);
        if (contentColor != null) {
            tvContent.setTextColor(Color.parseColor(contentColor));
        }

        //button1样式
        tvButton1.setText(button1);
        if (button1Color != null) {
            tvButton1.setTextColor(Color.parseColor(button1Color));
        }

        //button2样式
        if (button2 == null || TextUtils.isEmpty(button2)) {
            vDivider.setVisibility(View.GONE);
            tvButton2.setVisibility(View.GONE);
        } else {
            vDivider.setVisibility(View.VISIBLE);
            tvButton2.setVisibility(View.VISIBLE);
            tvButton2.setText(button2);
            if (button2Color != null) {
                tvButton2.setTextColor(Color.parseColor(button2Color));
            }
        }
    }

    //设置好各个参数以后，show方法调用显示最终的布局
    private void initCommonBottom() {
        cornerSize = Utils.dp2px(3);
        //设置内容，如果没有设置值，就不显示
        if (content == null || TextUtils.isEmpty(content)) {
            tvContent.setVisibility(View.GONE);
            vDivider1.setVisibility(View.GONE);
            tvButton1.getDelegate().setCornerRadius_TL(cornerSize);
            tvButton1.getDelegate().setCornerRadius_TR(cornerSize);
        } else {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(content);
            vDivider1.setVisibility(View.VISIBLE);
            if (contentColor != null) {
                tvContent.setTextColor(Color.parseColor(contentColor));
            }
        }

        //按钮1样式
        tvButton1.setText(button1);
        if (button1Color != null) {
            tvButton1.setTextColor(Color.parseColor(button1Color));
        }

        //按钮2样式
        if (button2 == null || TextUtils.isEmpty(button2)) {
            tvButton2.setVisibility(View.GONE);
            tvButton1.getDelegate().setCornerRadius_BL(cornerSize);
            tvButton1.getDelegate().setCornerRadius_BR(cornerSize);
        } else {
            tvButton2.setVisibility(View.VISIBLE);
            tvButton2.setText(button2);
            if (divider2show == true) {
                vDivider2.setVisibility(View.VISIBLE);
                tvButton1.getDelegate().setCornerRadius_BL(cornerSize);
                tvButton1.getDelegate().setCornerRadius_BR(cornerSize);
                tvButton2.getDelegate().setCornerRadius(cornerSize);
            } else {
                vDivider2.setVisibility(View.GONE);
                tvButton2.getDelegate().setCornerRadius_BL(cornerSize);
                tvButton2.getDelegate().setCornerRadius_BR(cornerSize);
            }
            if (button2Color != null) {
                tvButton2.setTextColor(Color.parseColor(button2Color));
            }
        }

        //按钮3样式
        if (TextUtils.isEmpty(button3)) {
            tvButton3.setVisibility(View.GONE);
            tvButton2.getDelegate().setCornerRadius_BL(cornerSize);
            tvButton2.getDelegate().setCornerRadius_BR(cornerSize);
        } else {
            tvButton3.setVisibility(View.VISIBLE);
            tvButton3.setText(button3);
            if (divider3show == true) {
                vDivider3.setVisibility(View.VISIBLE);
                tvButton2.getDelegate().setCornerRadius(cornerSize);
                tvButton3.getDelegate().setCornerRadius(cornerSize);
            } else {
                vDivider3.setVisibility(View.GONE);
            }
            if (button3Color != null) {
                tvButton3.setTextColor(Color.parseColor(button3Color));
            }
        }

    }


    public CommonDialog setOnButtonClickListener(final OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
        return this;
    }


    /**
     * 按钮点击事件
     */
    public abstract static class OnButtonClickListener {
        public void button1Click() {
        };

        public void button2Click() {
        };

        public void button3Click() {
        };
    }

    @Override
    public void show() {
        if (getContext() == null) {
            return;
        }
        super.show();
        switch (type) {
            case Common_Center:
                initCommonCenter();
                break;
            case Common_Bottom:
                initCommonBottom();
                break;
        }
    }

    public CommonDialog setTitle(String title) {//设置标题内容
        return setTitle(title, null);
    }

    public CommonDialog setTitle(String title, @DialogColor String titleColor) {//设置标题内容和颜色
        this.title = title;
        this.titleColor = titleColor;
        return this;
    }

    public CommonDialog setContent(String content) {//设置对话框内容
        return setContent(content, null);
    }

    public CommonDialog setContent(String content, @DialogColor String contentColor) {//设置对话框内容和颜色
        this.content = content;
        this.contentColor = contentColor;
        return this;
    }

    public CommonDialog setButton1(String button1) {//设置第一个按钮内容
        return setButton1(button1, null);
    }

    public CommonDialog setButton1(String button1, @DialogColor String button1Color) {//设置第一个按钮内容和颜色
        this.button1 = button1;
        this.button1Color = button1Color;
        return this;
    }

    public CommonDialog setButton2(String button2) {//设置第二个按钮内容
        return setButton2(button2, null);
    }

    public CommonDialog setButton2(String button2, @DialogColor String button2Color) {//设置第二个按钮内容和颜色
        this.button2 = button2;
        this.button2Color = button2Color;
        return this;
    }

    public CommonDialog setButton3(String button3) {//设置第三个按钮内容
        return setButton3(button3, null);
    }

    public CommonDialog setButton3(String button3, @DialogColor String button3Color) {//设置第三个按钮内容和颜色
        this.button3 = button3;
        this.button3Color = button3Color;
        return this;
    }

    public CommonDialog setDevider2Show(boolean divider2show) {//设置底部布局第一个按钮和第二个按钮的分隔线
        this.divider2show = divider2show;
        return this;
    }

    public CommonDialog setDevider3Show(boolean divider3show) {//设置底部布局第二个按钮和第三个按钮的分隔线
        this.divider3show = divider3show;
        return this;
    }

}
