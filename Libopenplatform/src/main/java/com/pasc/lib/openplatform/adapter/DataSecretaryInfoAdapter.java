package com.pasc.lib.openplatform.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pasc.lib.openplatform.resp.DataSecretaryDetailResp;
import com.pasc.libopenplatform.R;

import java.util.List;

public class DataSecretaryInfoAdapter
    extends BaseQuickAdapter<DataSecretaryDetailResp.DataDetail, BaseViewHolder> {

  public DataSecretaryInfoAdapter(@Nullable List<DataSecretaryDetailResp.DataDetail> data) {
    super(R.layout.openplatform_item_data_secretary_info, data);
  }

  @Override protected void convert(BaseViewHolder helper, DataSecretaryDetailResp.DataDetail item) {
    helper.setText(R.id.tv_info_type, item.userDataTypeName);
    TextView textView = helper.getView(R.id.tv_info_content);

    if (item.isOpened) {
      textView.setMaxLines(15);
      helper.setVisible(R.id.iv_arrow, false);
    } else {
      textView.setMaxLines(2);
      textView.setEllipsize(TextUtils.TruncateAt.END);
      if (item.relateField.length() > 45) {
        helper.setVisible(R.id.iv_arrow, true);
      } else {
        //textView.setMaxLines(8);
        helper.setVisible(R.id.iv_arrow, false);
      }
    }
    textView.setText(item.relateField);

    helper.addOnClickListener(R.id.iv_arrow);
  }

  public void expandInfo(int position, DataSecretaryDetailResp.DataDetail item) {
    item.isOpened = true;
    notifyItemChanged(position, item);
  }
}
