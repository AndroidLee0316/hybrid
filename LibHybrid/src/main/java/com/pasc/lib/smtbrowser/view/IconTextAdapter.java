package com.pasc.lib.smtbrowser.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.R;

import java.util.List;

/**
 * @date 2021-04-13
 * @des
 * @modify
 **/
public class IconTextAdapter extends BaseAdapter {
    List<IconTextBean> iconTextBeans;
    Context context;

    public IconTextAdapter(Context context, List<IconTextBean> iconTextBeans) {
        this.context = context;
        this.iconTextBeans = iconTextBeans;
    }

    @Override
    public int getCount() {
        return iconTextBeans.size ();
    }

    @Override
    public Object getItem(int position) {
        return iconTextBeans.get (position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyHolder myHolder = null;
//        if (convertView==null){
        convertView = View.inflate (context, R.layout.item_custom_pupop_list, null);
        myHolder = new MyHolder (convertView);
//        }else {
//            myHolder= (MyHolder) convertView.getTag ();
//        }
        IconTextBean iconTextBean = iconTextBeans.get (position);
        myHolder.itemView.setText (iconTextBean.text);
        if (iconTextBean.iconResource > 0) {
            myHolder.itemIv.setImageBitmap (null);
            myHolder.itemIv.setImageResource (iconTextBean.iconResource);
        } else {
            if (null != PascHybrid.getInstance ().getHybridInitConfig ().getHybridInitCallback ()) {
                PascHybrid.getInstance ().getHybridInitConfig ().getHybridInitCallback ().loadImage (myHolder.itemIv, iconTextBean.iconUrl);
            }
        }
        return convertView;
    }

    public static class MyHolder {
        public View rootView;
        public TextView itemView;
        public ImageView itemIv;

        public MyHolder(View rootView) {
            this.rootView = rootView;
            rootView.setTag (this);
            itemView = (TextView) rootView.findViewById (R.id.custom_dialog_text_view);
            itemIv = (ImageView) rootView.findViewById (R.id.custom_dialog_iv);
        }
    }
}
