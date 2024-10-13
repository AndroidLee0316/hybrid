package com.pasc.lib.hybrid.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.R;


import java.util.List;


/**
 * 通用Title View
 * Created by duyuan797 on 17/3/16.
 */

public class WebCommonTitleView extends LinearLayout {

    private static final String TAG = WebCommonTitleView.class.getSimpleName();

    protected View view;

    protected ImageView mLeftIv;
    // 返回
    protected TextView mRightTV;
    // 标题
    protected TextView mTitleTV;
    // 子标题
    private TextView mSubTitleTv;
    // 下一个
    protected TextView mLeftTV;
    //右图标
    protected ImageView mRightIv;
    protected ImageView mRightLeftIv;
    private View underLine;


    private View topView;

    private LinearLayout llTitleBar;

    private ProgressBar mProgressBar;

    private RelativeLayout rlFirst;

    private TranslateAnimation mShowAction, mHiddenAction;
    //toolbar颜色
    private String toolbarColor = "#ffffff";

    public WebCommonTitleView(Context context) {
        super(context);
        initView(context, null);
    }

    public WebCommonTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    protected void initView(Context context, AttributeSet attrs) {
        // 设置背景
        setBackgroundColor(getResources().getColor(R.color.white_ffffff));
        view = LayoutInflater.from(context).inflate(R.layout.common_title_view_web, null);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(view, params);

        mRightTV = (TextView) view.findViewById(R.id.common_title_right);
        mTitleTV = (TextView) view.findViewById(R.id.common_title_name);
        mLeftTV = (TextView) view.findViewById(R.id.common_title_left);
        mLeftIv = (ImageView) view.findViewById(R.id.iv_title_left);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mRightIv = (ImageView) view.findViewById(R.id.iv_title_Right);
        mRightLeftIv = (ImageView) view.findViewById(R.id.iv_title_right_left);
        rlFirst = (RelativeLayout) view.findViewById(R.id.rl_first);
        mSubTitleTv = (TextView)view.findViewById(R.id.common_tv_sub_title);
        underLine = (View) view.findViewById(R.id.view_under_line);
        topView  = (View) view.findViewById(R.id.top_view);
        llTitleBar = (LinearLayout) view.findViewById(R.id.ll_title_bar);
        if (attrs != null) {

            //获得这个控件对应的属性。
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WebCommonTitleView);

            try {
                //获得属性值
                //getColor(R.styleable.commonTitle_RxBackground, getResources().getColor(R.color.transparent))
                String title = a.getString(R.styleable.WebCommonTitleView_hybrid_toolbar_title);//标题
                int titleColor = a.getColor(R.styleable.WebCommonTitleView_hybrid_toolbar_titleColor, getResources().getColor(R.color.black_333333));//标题颜色
                int bgColor = a.getColor(R.styleable.WebCommonTitleView_hybrid_toolbar_backgroundColor,getResources().getColor( R.color.white_ffffff));//标题颜色
                setBackgroundColor(bgColor);
                int titleSize = a.getDimensionPixelSize(R.styleable.WebCommonTitleView_hybrid_toolbar_titleSize, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics()));
                boolean titleVisibility = a.getBoolean(R.styleable.WebCommonTitleView_hybrid_toolbar_titleVisibility, true);
                mTitleTV.setText(title);
                mTitleTV.setTextColor(titleColor);
                mTitleTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
                mTitleTV.setVisibility(titleVisibility ? VISIBLE : GONE);

                int leftIcon = a.getResourceId(R.styleable.WebCommonTitleView_hybrid_toolbar_leftIcon, R.drawable.paschybrid_ic_back_blue);//左边图标
//                if (null!=DefaultBehaviorManager.getInstance().getWebPageConfig()){
//                    int customBackIcon = DefaultBehaviorManager.getInstance().getWebPageConfig().getIconBackRes();
//                    if (customBackIcon != -1){
//                        leftIcon = a.getResourceId(R.styleable.WebCommonTitleView_leftIcon, customBackIcon);
//                    }
//                }
                boolean leftIconVisibility = a.getBoolean(R.styleable.WebCommonTitleView_hybrid_toolbar_leftIconVisibility, true);//左边图标是否显示
                mLeftIv.setImageResource(leftIcon);
                mLeftIv.setVisibility(leftIconVisibility ? VISIBLE : GONE);

                int rightIcon = a.getResourceId(R.styleable.WebCommonTitleView_hybrid_toolbar_rightIcon, R.drawable.paschybrid_ic_more_black);//右边图标
                boolean rightIconVisibility = a.getBoolean(R.styleable.WebCommonTitleView_hybrid_toolbar_rightIconVisibility, false);//右边图标是否显示
                mRightIv.setImageResource(rightIcon);
                mRightIv.setVisibility(rightIconVisibility ? VISIBLE : GONE);


                String leftText = a.getString(R.styleable.WebCommonTitleView_hybrid_toolbar_leftText);
                int leftTextColor = a.getColor(R.styleable.WebCommonTitleView_hybrid_toolbar_leftTextColor, getResources().getColor(R.color.blue_4d73f4));//左边字体颜色
                int leftTextSize = a.getDimensionPixelSize(R.styleable.WebCommonTitleView_hybrid_toolbar_leftTextSize, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP,16, getResources().getDisplayMetrics()));//标题字体大小
                boolean leftTextVisibility = a.getBoolean(R.styleable.WebCommonTitleView_hybrid_toolbar_leftTextVisibility, false);
                if(leftText==null){
                    leftText = "关闭";
                }
                mLeftTV.setText(leftText);
                mLeftTV.setTextColor(leftTextColor);
                mLeftTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTextSize);
                mLeftTV.setVisibility(leftTextVisibility ? VISIBLE : GONE);

                String rightText = a.getString(R.styleable.WebCommonTitleView_hybrid_toolbar_rightText);
                int rightTextColor = a.getColor(R.styleable.WebCommonTitleView_hybrid_toolbar_rightTextColor, getResources().getColor(R.color.black_333333));//右边字体颜色
                int rightTextSize = a.getDimensionPixelSize(R.styleable.WebCommonTitleView_hybrid_toolbar_rightTextSize, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP,16, getResources().getDisplayMetrics()));//标题字体大小
                boolean rightTextVisibility = a.getBoolean(R.styleable.WebCommonTitleView_hybrid_toolbar_rightTextVisibility, false);
                mRightTV.setText(rightText);
                mRightTV.setTextColor(rightTextColor);
                mRightTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize);
                mRightTV.setVisibility(rightTextVisibility ? VISIBLE : GONE);

            } finally {
                //回收这个对象
                a.recycle();
            }
        }

        mShowAction =
                new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                        0.0f, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);
        mHiddenAction =
                new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                        0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        mHiddenAction.setDuration(500);
    }

    public WebCommonTitleView setTopRelBackGround(int resId) {
        rlFirst.setBackgroundResource(resId);
        return this;
    }

    public WebCommonTitleView setOnLeftClickListener(OnClickListener clickListener) {
        mLeftIv.setOnClickListener(clickListener);
        return this;
    }

    public WebCommonTitleView setOnLeftTextClickListener(OnClickListener clickListener) {
        mLeftTV.setOnClickListener(clickListener);
        return this;
    }

    public WebCommonTitleView setOnTitleClickListener(OnClickListener clickListener) {
        mTitleTV.setOnClickListener(clickListener);
        return this;
    }

    public WebCommonTitleView setOnRightClickListener(OnClickListener clickListener) {
        mRightTV.setOnClickListener(clickListener);
        return this;
    }

    public WebCommonTitleView setOnRightImageClickListener(OnClickListener clickListener) {
        mRightIv.setOnClickListener(clickListener);
        return this;
    }

    public WebCommonTitleView setOnRightLeftImageDrawable(int resId) {
        mRightLeftIv.setVisibility(VISIBLE);
        mRightLeftIv.setImageResource(resId);
        return this;
    }

    public WebCommonTitleView setOnRightLeftImageClickListener(OnClickListener clickListener) {
        mRightLeftIv.setOnClickListener(clickListener);
        return this;
    }

    public WebCommonTitleView setOnRightLeftImageVisible(int visible) {
        mRightLeftIv.setVisibility(visible);
        return this;
    }

    public WebCommonTitleView setLeftText(String text) {
        //mLeftTV.setVisibility(View.VISIBLE);
        mLeftTV.setText(text);
        return this;
    }

    public WebCommonTitleView setBgColor(@ColorInt int colorId) {
        rlFirst.setBackgroundColor(colorId);
        return this;
    }

    public void setLeftTextColor(int color) {
        mLeftTV.setTextColor(color);
    }

    public WebCommonTitleView setLeftText(int text) {
        mLeftTV.setVisibility(View.VISIBLE);
        mLeftTV.setText(text);
        return this;
    }

    public WebCommonTitleView setTitleText(String text) {
        mTitleTV.setText(text);
        return this;
    }

    public void setTitleTextColor(int color) {
        mTitleTV.setTextColor(color);
    }

    public void setTitleTextSize(float size) {
        mTitleTV.setTextSize(size);
    }

    public void setTitleWeight(float weight) {
        LayoutParams params =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.weight = weight;
        params.gravity = Gravity.CENTER_VERTICAL;
        mTitleTV.setLayoutParams(params);
    }

    public WebCommonTitleView setTitleText(int text) {
        mTitleTV.setText(text);
        return this;
    }

    public WebCommonTitleView setRightText(String text) {
        mRightTV.setVisibility(View.VISIBLE);
        mRightTV.setText(text);
        return this;
    }

    public WebCommonTitleView setRightText(int text) {
        mRightTV.setVisibility(View.VISIBLE);
        mRightTV.setText(text);
        return this;
    }

    public void setRightTextColor(int rid) {
        mRightTV.setTextColor(rid);
    }

    public void setRightTextSize(int size) {
        mRightTV.setTextSize(size);
    }

    public WebCommonTitleView setRightTextVisibility(int visiable) {
        mRightTV.setVisibility(visiable);
        return this;
    }

    public void setLeftTextVisibility(int visiable) {
        mLeftTV.setVisibility(visiable);
    }

    public WebCommonTitleView setBackDrawableLeft(int rId) {
        if (rId != 0) {
            mLeftIv.setVisibility(View.VISIBLE);
            mLeftIv.setImageResource(rId);
        }
        return this;
    }

    public WebCommonTitleView setBackDrawableVisible(int visible) {
        mLeftIv.setVisibility(visible);
        return this;
    }

    public WebCommonTitleView setLeftIvResource(String url){
        mLeftIv.setVisibility(View.VISIBLE);
        if (null!= PascHybrid.getInstance().getHybridInitConfig().getHybridInitCallback()){
            mLeftIv.clearColorFilter();
            PascHybrid.getInstance().getHybridInitConfig().getHybridInitCallback().loadImage(mLeftIv,url);
        }
        return this;
    }

    public void setRightIvResource(String url){
        mRightIv.setVisibility(View.VISIBLE);
        if (null!=PascHybrid.getInstance().getHybridInitConfig().getHybridInitCallback()){
            mRightIv.clearColorFilter();
            PascHybrid.getInstance().getHybridInitConfig().getHybridInitCallback().loadImage(mRightIv,url);
        }
    }

    public WebCommonTitleView setRightImageVisible(int visible) {
        mRightIv.setVisibility(visible);
        return this;
    }

    public WebCommonTitleView setRightDrawableRight(int rId) {
        if (rId != 0) {
            mRightIv.setVisibility(View.VISIBLE);
            mRightIv.setImageResource(rId);
        }
        return this;
    }

    public void setTitleDrawableRight(int rId) {
        if (rId > 0) {
            Drawable drawable = getResources().getDrawable(rId);
            mTitleTV.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        } else {
            mTitleTV.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    public void setNextDrawableRight(int rId) {
        if (rId != 0) {
            mRightTV.setVisibility(View.VISIBLE);
            Drawable drawable = getResources().getDrawable(rId);
            mRightTV.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        } else {
            mTitleTV.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    public WebCommonTitleView setUnderLineVisible(boolean isVisible){
        if (isVisible){
            underLine.setVisibility(View.VISIBLE);
        }else {
            underLine.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 设置状态占位栏高度
     */
    public WebCommonTitleView setTopViewHeight(int height) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)topView.getLayoutParams();
        layoutParams.height = height;
        topView.setLayoutParams(layoutParams);
        return this;
    }

    /**
     * 设置整个toolbar渐变色
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public WebCommonTitleView setToolBarColor(List<String> colorList, int direction) {
        int[] colors = new int[]{Color.parseColor(colorList.get(0)),
                Color.parseColor(colorList.get(1))};
        GradientDrawable linearDrawable = new GradientDrawable();
        switch (direction){
            case 0:
                linearDrawable.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM);
                break;
            case 1:
                linearDrawable.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
                break;
            case 2:
                linearDrawable.setOrientation(GradientDrawable.Orientation.TL_BR);
                break;
            case 3:
                linearDrawable.setOrientation(GradientDrawable.Orientation.BL_TR);
                break;
        }

        linearDrawable.setColors(colors);
        linearDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        llTitleBar.setBackground(linearDrawable);
        return this;
    }

    /**
     * 设置整个toolbar颜色
     */
    public WebCommonTitleView setToolBarColor(String color) {
        toolbarColor = color;
        llTitleBar.setBackgroundColor(Color.parseColor(color));
        return this;
    }

    /**
     * 设置title的样式
     * @param typeface
     * @return
     */
    public WebCommonTitleView setTitleTypeface(Typeface typeface){
        mTitleTV.setTypeface(typeface);
        return this;
    }

    /*
     * 获取toolbar颜色，以便确定其他图标颜色
     */
    public String getToolBarColor(){
        return toolbarColor;
    }



    public void setSubTitleText(String content){
        mSubTitleTv.setText(content);
    }

    public void setSubTitleSize(float size){
        mSubTitleTv.setTextSize(size);
    }

    public void setSubTitleColor(int color){
        mSubTitleTv.setTextColor(color);
    }

    public void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void dismissLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    public ImageView getLeftIv() {
        return mLeftIv;
    }

    public TextView getRightTV() {
        return mRightTV;
    }

    public TextView getTitleTV() {
        return mTitleTV;
    }

    public TextView getLeftTV() {
        return mLeftTV;
    }

    public ImageView getRightIv() {
        return mRightIv;
    }

    /**
     * 右边往左第二个按钮是否隐藏
     */
    public boolean getRightLeftIconVisibility() {
        return mRightLeftIv.getVisibility() == VISIBLE;
    }
}
