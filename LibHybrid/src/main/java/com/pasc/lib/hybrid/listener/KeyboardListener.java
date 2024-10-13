package com.pasc.lib.hybrid.listener;


import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

public class KeyboardListener implements ViewTreeObserver.OnGlobalLayoutListener {

    private Context mContext;
    private boolean isKeyboardOpened = false;//键盘是否打开
    private OnKeyboardListener mKeyboardListener;//键盘状态监听
    private Window mWindow;
    private View contentView;

    public KeyboardListener(Context mContext) {
        this.mContext = mContext;
        mWindow = ((Activity) mContext).getWindow();
        if (mWindow == null) {
            return;
        }
        View decorView = mWindow.getDecorView();
        if (decorView == null) {
            return;
        }
        //获取contentView
        contentView = decorView.findViewById(android.R.id.content);
        if (contentView == null) {
            return;
        }
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(this);//监听布局树是否发生改变
    }


    @Override
    public void onGlobalLayout() {


        if (mKeyboardListener != null) {
            //获取布局可视区域
            if (contentView == null) {
                return;
            }
            Rect rect = new Rect();
            contentView.getWindowVisibleDisplayFrame(rect);//获取布局的可视区域

            int screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
            int keyboardHeight = screenHeight - rect.bottom;
            if(keyboardHeight <= 0){
                if(isKeyboardOpened){
                    // 键盘关闭
                    isKeyboardOpened = false;
                    mKeyboardListener.onKeyboardClose();
                }
            } else if(keyboardHeight > 0){
                if(isKeyboardOpened){
                    // 键盘改变
                    mKeyboardListener.onKeyboardChanged(keyboardHeight);
                }else{
                    // 键盘打开
                    isKeyboardOpened = true;
                    mKeyboardListener.onKeyboardOpened(keyboardHeight);
                }
            }
        }
    }


    public void removeGlobalLayoutListener() {
        if(contentView ==null){
            return;
        }
        contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    public void setOnKeyboardListener(OnKeyboardListener onKeyboardListener) {
        this.mKeyboardListener = onKeyboardListener;
    }


    public interface OnKeyboardListener {
        void onKeyboardOpened(int keyboardHeight);
        void onKeyboardChanged(int keyboardHeight);
        void onKeyboardClose();
    }
}
