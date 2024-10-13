package com.pasc.lib.openplatform.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.openplatform.bean.UserAuthContentBean;
import com.pasc.libopenplatform.R;

import java.util.List;

/**
 * 功能：
 *
 * @author lichangbao702
 * @email : lichangbao702@pingan.com.cn
 * @date : 2020/2/11
 */
public class OpenAuthSelectAdapter extends RecyclerView.Adapter<OpenAuthSelectAdapter.OpenAuthSelectViewHodler>{

    private List<UserAuthContentBean.ItemBean> itemList;

    private OnClickCallBack clickCallBack;

    public OpenAuthSelectAdapter(List<UserAuthContentBean.ItemBean> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public OpenAuthSelectViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.openplatform_item_auth_select,null);
        OpenAuthSelectViewHodler viewHodler = new OpenAuthSelectViewHodler(view);
        return viewHodler;
    }

    @Override
    public void onBindViewHolder(@NonNull OpenAuthSelectViewHodler holder, final int position) {
        final UserAuthContentBean.ItemBean itemBean = itemList.get(position);
        //标题
        if (TextUtils.isEmpty(itemBean.title)){
            holder.titleTV.setVisibility(View.GONE);
        }else {
            holder.titleTV.setVisibility(View.VISIBLE);
            holder.titleTV.setText(itemBean.title);
        }

        //副标题
        if (TextUtils.isEmpty(itemBean.subTitle)){
            holder.subTitleTV.setVisibility(View.GONE);
        }else {
            holder.subTitleTV.setVisibility(View.VISIBLE);
            holder.subTitleTV.setText(itemBean.subTitle);
        }

//        //图标
//        if (TextUtils.isEmpty(itemBean.iconURL)){
//            holder.iconIV.setVisibility(View.GONE);
//        }else {
//            holder.iconIV.setVisibility(View.VISIBLE);
//            PascHybrid.getInstance()
//                    .getHybridInitConfig()
//                    .getHybridInitCallback()
//                    .loadImage(holder.iconIV, itemBean.iconURL);
//        }

        //副标题
        if (itemBean.select){
            holder.selectIV.setImageResource(R.drawable.paschybrid_ic_select);
        }else {
            holder.selectIV.setImageResource(0);
        }

        if (position == itemList.size() - 1){
            holder.splitView.setVisibility(View.GONE);
        }else {
            holder.splitView.setVisibility(View.VISIBLE);
        }

        holder.rootRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < itemList.size(); i++) {
                    if (i == position){
                        itemList.get(i).select = true;
                        if (clickCallBack != null){
                            clickCallBack.onItemSelect(itemList.get(i));
                        }
                    }else {
                        itemList.get(i).select = false;
                    }
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (itemList != null){
            return itemList.size();
        }
        return 0;
    }


    static class OpenAuthSelectViewHodler extends RecyclerView.ViewHolder{

        RelativeLayout rootRL;
//        ImageView iconIV;
        TextView titleTV;
        TextView subTitleTV;
        ImageView selectIV;
        View splitView;


        public OpenAuthSelectViewHodler(View itemView) {
            super(itemView);
            rootRL = itemView.findViewById(R.id.open_auth_select_item_root);
//            iconIV = itemView.findViewById(R.id.open_auth_select_item_icon);
            titleTV = itemView.findViewById(R.id.open_auth_select_item_title);
            subTitleTV = itemView.findViewById(R.id.open_auth_select_item_subtitle);
            selectIV = itemView.findViewById(R.id.open_auth_select_item_select);
            splitView = itemView.findViewById(R.id.open_auth_select_item_split);
        }
    }

    public void setClickCallBack(OnClickCallBack clickCallBack) {
        this.clickCallBack = clickCallBack;
    }

    public interface OnClickCallBack{
        void onItemSelect(UserAuthContentBean.ItemBean item);
    }
}
