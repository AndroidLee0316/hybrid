package com.pasc.libbrowser.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import android.widget.Toast;
import com.example.chenkun305.libbrowser.R;
import com.pasc.libbrowser.Constants;
import com.pasc.libbrowser.UserConstant;
import com.pasc.libbrowser.event.WXLoginEvent;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import java.util.HashMap;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;

/**
 * 接收微信回调的页面
 * Created by ruanwei489 on 2018/5/3.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private Context mContext;

    public IWXAPI mWxApi;
    private TextView textView;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = WXEntryActivity.this;
        setContentView(R.layout.wx_entry_activity);
        textView = (TextView) findViewById(R.id.tv_content);
        mWxApi = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);
        mWxApi.handleIntent(getIntent(), this);
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWxApi.handleIntent(intent, this);
    }

    @Override public void onReq(BaseReq baseReq) {
    }

    @Override public void onResp(BaseResp baseResp) {
        String result = "";
        Map map = new HashMap();
        map.put("第三方应用", "微信");
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                switch (baseResp.getType()) {
                    case ConstantsAPI.COMMAND_SENDAUTH:
                        //登录回调,获得CODE
                        String state = ((SendAuth.Resp) baseResp).state;
                        if (state.equals(UserConstant.SEND_AUTH_REQ_STATE)) {
                            String code = ((SendAuth.Resp) baseResp).code;
                            EventBus.getDefault()
                                    .post(new WXLoginEvent(WXLoginEvent.WX_Login_Event_Success,
                                            code));
                            //EventUtils.onEvent(WXEntryActivity.this, "04010103第三方登录", "确认授权", map);
                            result = "授权成功";
                        } else if (state.equals(Constants.SEND_AUTH_REQ_STATE)) {
                            String code = ((SendAuth.Resp) baseResp).code;
                            EventBus.getDefault()
                                    .post(new WXLoginEvent(WXLoginEvent.WX_Bind_Third_Part, code));
                            finish();
                            return;
                        } else {
                            result = "授权失败";
                        }
                        break;
                    case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                        //微信分享回调
                        result = "分享成功";
                        break;
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                switch (baseResp.getType()) {
                    case ConstantsAPI.COMMAND_SENDAUTH:
                        //登录回调
                        result = "授权取消";
                        //埋点
                        //EventUtils.onEvent(WXEntryActivity.this, "04010103第三方登录", "退出授权", map);
                        break;
                    case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                        //微信分享回调
                        break;
                }
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                switch (baseResp.getType()) {
                    case ConstantsAPI.COMMAND_SENDAUTH:
                        //登录回调
                        result = "授权失败";
                        //EventUtils.onEvent(WXEntryActivity.this, "04010103第三方登录", "拒绝授权", map);
                        break;
                    case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                        //微信分享回调
                        break;
                }
                break;
            default:
                break;
        }
        Toast.makeText(mContext,result,Toast.LENGTH_LONG).show();
        finish();
    }

    @Override protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }
}
