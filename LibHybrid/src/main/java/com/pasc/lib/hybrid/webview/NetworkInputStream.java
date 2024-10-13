package com.pasc.lib.hybrid.webview;

import android.util.Base64;

import com.pasc.lib.hybrid.PascHybrid;
import com.pasc.lib.hybrid.util.Constants;
import com.pasc.lib.hybrid.util.IOUtils;
import com.pasc.lib.hybrid.util.LogUtils;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Created by buyongyou on 2019/1/17.
 * @Email: buyongyou490@pingan.com.cn
 * @des
 */
 class NetworkInputStream extends InputStream {
    private static final String TAG = PascWebViewClient.class.getSimpleName();
    private String url;
    private InputStream inputStream;
    private boolean initialized=false;

    NetworkInputStream(String url) {
        this.url = url;
    }

    @Override
    public int read() throws IOException {
        if (!initialized) {
            LogUtils.i(TAG, "start initial : " + url);
            try {
                String path = url.substring(url.indexOf(PascHybrid.PROTOFUL) + PascHybrid.PROTOFUL.length()).trim();
                if (path.contains(Constants.IMAGE_KEY)) {
                    path = path.substring(0, path.indexOf(Constants.IMAGE_KEY));
                }
                if (path.contains(Constants.VIDEO_KEY)) {
                    path = path.substring(0, path.indexOf(Constants.VIDEO_KEY));
                }
                if (path.contains(Constants.AUDIO_KEY)) {
                    path = path.substring(0, path.indexOf(Constants.AUDIO_KEY));
                }
                inputStream = new FileInputStream(new File(path));
                if (url.trim().contains(Constants.IMAGE_TYPE)
                    || url.trim().contains(Constants.VIDEO_TYPE)
                    || url.trim().contains(Constants.AUDIO_TYPE)) {
                    byte[] bytes = IOUtils.toByteArray(inputStream);
                    byte[] encode = Base64.encode(bytes, Base64.NO_WRAP);
                    inputStream = new ByteArrayInputStream(encode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.e(TAG, "load async exception :" + url + " ; " + e.getMessage());

                byte[] result = wrapperErrorResponse(e);
                LogUtils.i(TAG, "Exception: " + new String(result));
                inputStream = IOUtils.toInputStream(new String(result));
            } finally {
                initialized = true;
            }
        }
        // 返回数据
        if (null != inputStream) {
            return inputStream.read();
        }
        return  -1;
    }

    private byte[] wrapperErrorResponse(Exception exception){
        if (null == exception) {
            return new byte[0];
        }
        try {
            JSONObject result = new JSONObject();
            result.put(Constants.KEY_NETWORK_ERROR, true);
            return (Constants.ERROR_PREFIX + result.toString()).getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

}