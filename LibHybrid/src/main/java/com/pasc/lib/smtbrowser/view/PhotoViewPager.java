package com.pasc.lib.smtbrowser.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.smtbrowser.view.photoview.OnOutsidePhotoTapListener;

import java.util.ArrayList;

/**
 * 使用PhotoView来浏览图片的ViewPager
 * Created by ex-lingchun001 on 2018/4/12.
 */

public class PhotoViewPager extends ViewPager {

    private PhotoPagerAdapter adapter;
    private ArrayList<String> picUrl;

    public PhotoViewPager(Context context) {
        super(context);
        //init();
    }

    public PhotoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init();
    }

    public void init() {
        init("#000000");
    }

    public void init(String color) {
        if (adapter == null) {
            adapter = new PhotoPagerAdapter(color);
            picUrl = new ArrayList<>();
        }
        setAdapter(adapter);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void setPicUrl(ArrayList<String> urls, int curIndex,String color) {
        if (adapter == null) {
            adapter = new PhotoPagerAdapter(color);
            picUrl = new ArrayList<>();
        }
        picUrl.clear();
        picUrl.addAll(urls);
        adapter.notifyDataSetChanged();
        setCurrentItem(curIndex);
    }

    private void finishWithAnimation(final MoreGesturePhotoView view) {

        ValueAnimator translateXAnimator = ValueAnimator.ofFloat(0, 1);
        translateXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if(view!=null){
                    view.setX((Float) valueAnimator.getAnimatedValue());
                }
            }
        });
        translateXAnimator.setDuration(300);
        translateXAnimator.start();

        ValueAnimator translateYAnimator = ValueAnimator.ofFloat(0, 1);
        translateYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if(view!=null){
                    view.setY((Float) valueAnimator.getAnimatedValue());
                }
            }
        });
        translateYAnimator.setDuration(300);
        translateYAnimator.start();

        ValueAnimator scaleYAnimator = ValueAnimator.ofFloat(1, 0);
        scaleYAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if(view!=null){
                    view.setScaleY((Float) valueAnimator.getAnimatedValue());
                }
            }
        });
        scaleYAnimator.setDuration(300);
        scaleYAnimator.start();

        ValueAnimator scaleXAnimator = ValueAnimator.ofFloat(1, 0);
        scaleXAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if(view!=null){
                    view.setScaleX((Float) valueAnimator.getAnimatedValue());
                }
            }
        });

        scaleXAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animator.removeAllListeners();
                setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        scaleXAnimator.setDuration(300);
        scaleXAnimator.start();
    }

    public void backClick() {
        finishWithAnimation(adapter.getCurPhotoView());
    }

    class PhotoPagerAdapter extends PagerAdapter {
        private MoreGesturePhotoView curPhotoView;
        private String color = "#000000";

        public PhotoPagerAdapter(String color){
            if(!TextUtils.isEmpty(color)){
                this.color = color;
            }
        }

        @Override
        public int getCount() {
            return picUrl == null ? 0 : picUrl.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final MoreGesturePhotoView photoView = new MoreGesturePhotoView(container.getContext(),Color.parseColor(color));

            if (null!= PascHybrid.getInstance().getHybridInitConfig().getHybridInitCallback()){
                PascHybrid.getInstance().getHybridInitConfig().getHybridInitCallback().loadImage(photoView,picUrl.get(position));
            }
            photoView.setOnOutsidePhotoTapListener(new OnOutsidePhotoTapListener() {
                @Override
                public void onOutsidePhotoTap(ImageView imageView) {
                    finishWithAnimation(photoView);
                }
            });
            photoView.setOnExitListener(new MoreGesturePhotoView.OnExitListener() {
                @Override
                public void onExit(MoreGesturePhotoView view, float translateX, float translateY,
                                   float w, float h) {
                    finishWithAnimation(photoView);
                }
            });
            photoView.setOnTapListener(new MoreGesturePhotoView.OnTapListener() {
                @Override
                public void onTap(MoreGesturePhotoView view) {
                    finishWithAnimation(photoView);
                }
            });
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            curPhotoView = (MoreGesturePhotoView) object;
        }

        public MoreGesturePhotoView getCurPhotoView() {
            return curPhotoView;
        }
    }

}
