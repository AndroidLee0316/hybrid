package com.pasc.lib.hybrid.util;

import android.content.Context;

import com.tencent.smtt.sdk.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BridgeUtil {
    public final static String PASC_OVERRIDE_SCHEMA = "pasc://";
    public final static String PASC_RETURN_DATA = PASC_OVERRIDE_SCHEMA + "return/";//格式为   yy://return/{function}/returncontent
    public final static String PASC_FETCH_QUEUE = PASC_RETURN_DATA + "_fetchQueue/";
    public final static String EMPTY_STR = "";
    public final static String UNDERLINE_STR = "_";
    public final static String SPLIT_MARK = "/";
    public final static String PASC_BRIDGE_INJECT = PASC_OVERRIDE_SCHEMA+ "__REQUEST__BRIDGE__INJECT__";

    public final static String CALLBACK_ID_FORMAT = "JAVA_CB_%s";

    public final static String JS_HANDLE_MESSAGE_FROM_JAVA = "javascript:try{PASCWebViewBridge._handleMessageFromNative('%s')}catch(e){console.log(e)};";
    public final static String JS_FETCH_QUEUE_FROM_JAVA = "javascript:PASCWebViewBridge._fetchQueue();";
    public final static String JAVASCRIPT_STR = "javascript:";

    //无网络
    public final static int NETWORK_DISCONNECTED = 0;
    //移动数据网络
    public final static int NETWORK_DATA = 1;
    //WiFi
    public final static int NETWORK_WIFI = 2;

    /**
     * 返回给前端的code： 成功
     */
    public final static int RESPONSE_CODE_SUCCESS = 0;

    /**
     * 返回给前端的code： API不可用，未注册
     */
    public final static int RESPONSE_CODE_API_UNUSABLE = -2;

    /**
     * 返回给前端的code： 内部错误
     */
    public final static int RESPONSE_CODE_ERROR = -1;


    public final static String RSPONSE_MSG_API_UNUSABLE = "Unusable API";

    // 例子 javascript:PASCWebViewBridge._fetchQueue(); --> _fetchQueue
    public static String parseFunctionName(String jsUrl) {
        return jsUrl.replace("javascript:PASCWebViewBridge.", "").replaceAll("\\(.*\\);", "");
    }

    // 获取到传递信息的body值
    // url = yy://return/_fetchQueue/[{"responseId":"JAVA_CB_2_3957","responseData":"Javascript Says Right back aka!"}]
    public static String getDataFromReturnUrl(String url) {
        if (url.startsWith(PASC_FETCH_QUEUE)) {
            // return = [{"responseId":"JAVA_CB_2_3957","responseData":"Javascript Says Right back aka!"}]
            return url.replace(PASC_FETCH_QUEUE, EMPTY_STR);
        }

        // temp = _fetchQueue/[{"responseId":"JAVA_CB_2_3957","responseData":"Javascript Says Right back aka!"}]
        String temp = url.replace(PASC_RETURN_DATA, EMPTY_STR);
        String[] functionAndData = temp.split(SPLIT_MARK);

        if (functionAndData.length >= 2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < functionAndData.length; i++) {
                sb.append(functionAndData[i]);
            }
            // return = [{"responseId":"JAVA_CB_2_3957","responseData":"Javascript Says Right back aka!"}]
            return sb.toString();
        }
        return null;
    }

    // 获取到传递信息的方法
    // url = yy://return/_fetchQueue/[{"responseId":"JAVA_CB_1_360","responseData":"Javascript Says Right back aka!"}]
    public static String getFunctionFromReturnUrl(String url) {
        // temp = _fetchQueue/[{"responseId":"JAVA_CB_1_360","responseData":"Javascript Says Right back aka!"}]
        String temp = url.replace(PASC_RETURN_DATA, EMPTY_STR);
        String[] functionAndData = temp.split(SPLIT_MARK);
        if (functionAndData.length >= 1) {
            // functionAndData[0] = _fetchQueue
            return functionAndData[0];
        }
        return null;
    }


    /**
     * js 文件将注入为第一个script引用
     *
     * @param view WebView
     * @param url  url
     */
    public static void webViewLoadJs(WebView view, String url) {
        String js = "var newscript = document.createElement(\"script\");";
        js += "newscript.src=\"" + url + "\";";
        js += "document.scripts[0].parentNode.insertBefore(newscript,document.scripts[0]);";
        view.loadUrl("javascript:" + js);
    }

    /**
     * 这里只是加载lib包中assets中的 PascWebViewJavascriptBridge.js
     *
     * @param view webview
     * @param path 路径
     */
    public final static void webViewLoadLocalJs(WebView view, String path) {
        String jsContent = assetFile2Str(view.getContext(), path);
        view.loadUrl("javascript:" + jsContent);
    }

    /**
     * 解析assets文件夹里面的代码,去除注释,取可执行的代码
     *
     * @param c      context
     * @param urlStr 路径
     * @return 可执行代码
     */
    public static String assetFile2Str(Context c, String urlStr) {
        InputStream in = null;
        try {
            in = c.getAssets().open(urlStr);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder sb = new StringBuilder();
            do {
                line = bufferedReader.readLine();
                if (line != null && !line.matches("^\\s*\\/\\/.*")) { // 去除注释
                    sb.append(line);
                }
            } while (line != null);

            bufferedReader.close();
            in.close();

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
