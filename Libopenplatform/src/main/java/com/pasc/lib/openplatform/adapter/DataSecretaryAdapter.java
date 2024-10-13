package com.pasc.lib.openplatform.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.openplatform.resp.DataSecretaryResp;
import com.pasc.libopenplatform.R;

import java.util.List;

public class DataSecretaryAdapter extends BaseQuickAdapter<DataSecretaryResp, BaseViewHolder> {
    public DataSecretaryAdapter(@Nullable List<DataSecretaryResp> data) {
        super( R.layout.openplatform_item_data_secretary,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DataSecretaryResp item) {
        helper.setText(R.id.tv_data_name,item.thirdPartyServicesName).setText(R.id.tv_data_auth_info,item.thirdPartyServicesNameDetail);
        PascHybrid.getInstance()
                .getHybridInitConfig()
                .getHybridInitCallback()
                .loadImage((ImageView) helper.getView(R.id.iv_data_icon), item.logo);
    }
}
