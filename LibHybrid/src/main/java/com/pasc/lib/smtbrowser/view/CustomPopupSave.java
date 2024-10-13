package com.pasc.lib.smtbrowser.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.pasc.lib.hybrid.R;
import com.pasc.lib.hybrid.util.Utils;
import com.pasc.lib.smtbrowser.view.adapter.TAdapter;
import com.pasc.lib.smtbrowser.view.adapter.TAdapterDelegate;
import com.pasc.lib.smtbrowser.view.adapter.TViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义的PopupWindow
 * Created by wjn on 2017/2/20.
 */

public class CustomPopupSave extends PopupWindow {

    private Context mContext;

    private View mView;
    ListView lvContent;

    private int itemSize = 0;

    private BaseAdapter listAdapter;
    private List itemTextList = new ArrayList<>();
    private List<onSeparateItemClickListener> itemListenerList = new ArrayList<>();
    private AdapterView.OnItemClickListener itemListener;

    public CustomPopupSave(Activity context) {
        this.mContext = context;
        initView();
        initAdapter();
    }

    private void initView() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.view_popup, null);
        lvContent = (ListView) mView.findViewById(R.id.lv_web_popup);
        this.setContentView(mView);
        //this.setOutsideTouchable(true);
        this.setFocusable(true);

        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mView.findViewById(R.id.loopin_popup_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        if (itemSize > 0) {
            updateListView();
        }

        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

    }

    /**
     * 初始化adapter
     */
    private void initAdapter() {
        listAdapter = new TAdapter(mContext, itemTextList, new TAdapterDelegate() {

            @Override
            public int getViewTypeCount() {
                return itemTextList.size();
            }

            @Override
            public Class<? extends TViewHolder> viewHolderAtPosition(int position) {
                return CustomPupopViewHolder.class;
            }

            @Override
            public boolean enabled(int position) {
                return true;
            }
        });
        itemListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemListenerList.get(position).onClick();
                dismiss();
            }
        };
    }

    /**
     * 添加dialog的item
     *
     * @param itemText item文本
     * @param listener item的点击监听
     */
    @Nullable
    public void addItem(String itemText, Integer iconResId, onSeparateItemClickListener listener) {
        //itemTextList.add(new Pair<String, Integer>(itemText, R.color.black_message));
        itemTextList.add(new Pair<String, Integer>(itemText, iconResId));
        itemListenerList.add(listener);
        itemSize = itemTextList.size();
    }

    /**
     * 添加dialog的item
     *
     * @param itemText item文本
     * @param listener item的点击监听
     */
    @Nullable
    public void addItem(String itemText, String url, onSeparateItemClickListener listener) {
        //itemTextList.add(new Pair<String, Integer>(itemText, R.color.black_message));
        Map<String,String> map = new HashMap<>(16);
        map.put(itemText, url);
        itemTextList.add(map);
        itemListenerList.add(listener);
        itemSize = itemTextList.size();
    }


    /**
     * 清除数据
     */
    public void clearData() {
        itemTextList.clear();
        itemListenerList.clear();
        itemSize = 0;
    }

    /**
     * 更新listView
     */
    private void updateListView() {
        listAdapter.notifyDataSetChanged();
        if (lvContent != null) {
            lvContent.setAdapter(listAdapter);
            lvContent.setOnItemClickListener(itemListener);
            this.setWidth(measureContentWidth(listAdapter) + Utils.dp2px(20));
        }
    }


    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        if (itemSize <= 0) {
            return;
        }
        updateListView();
        super.showAsDropDown(anchor, xoff, yoff);
    }

    public interface onSeparateItemClickListener {
        void onClick();
    }

    private int measureContentWidth(ListAdapter listAdapter) {
        ViewGroup mMeasureParent = null;
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;

        final ListAdapter adapter = listAdapter;
        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(mContext);
            }

            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            final int itemWidth = itemView.getMeasuredWidth();

            if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }

        return maxWidth;
    }

}
