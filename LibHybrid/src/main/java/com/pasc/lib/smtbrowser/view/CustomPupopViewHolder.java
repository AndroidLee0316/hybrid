package com.pasc.lib.smtbrowser.view;

import android.util.Pair;
import android.widget.ImageView;
import android.widget.TextView;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.R;
import com.pasc.lib.smtbrowser.view.adapter.TViewHolder;
import java.util.Map;

/**
 * create by wujianning385 on 2018/7/30.
 */
public class CustomPupopViewHolder extends TViewHolder {

  private TextView itemView;
  private ImageView itemIv;

  private int i;

  @Override protected int getResId() {
    return R.layout.item_custom_pupop_list;
  }

  @Override protected void inflate() {
    itemView = (TextView) view.findViewById(R.id.custom_dialog_text_view);
    itemIv = (ImageView) view.findViewById(R.id.custom_dialog_iv);
  }

  @Override protected void refresh(Object item) {
    if (item instanceof Pair<?, ?>) {
      Pair<String, Integer> pair = (Pair<String, Integer>) item;
      itemView.setText(pair.first);
      if (pair.second != null) {
//        itemIv.setBackgroundResource(pair.second);
        itemIv.setImageResource (pair.second);
      }
      //itemView.setTextColor(context.getResources().getColor(pair.second));
    } else if (item instanceof Map) {
      Map<String, String> map = (Map<String, String>) item;
      String txt = "";
      String url = "";
      for (Map.Entry<String, String> entry : map.entrySet()) {
        txt = entry.getKey();
        url = entry.getValue();
      }
      itemView.setText(txt);
      if (null != PascHybrid.getInstance().getHybridInitConfig().getHybridInitCallback()) {
          PascHybrid.getInstance().getHybridInitConfig().getHybridInitCallback().loadImage(itemIv, url);
      }
    }
  }
}
