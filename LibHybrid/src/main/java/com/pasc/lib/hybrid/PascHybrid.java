package com.pasc.lib.hybrid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseArray;
import com.google.gson.Gson;
import com.pasc.lib.hybrid.behavior.BehaviorHandler;
import com.pasc.lib.hybrid.behavior.DefaultBehaviorManager;
import com.pasc.lib.hybrid.behavior.WebPageConfig;
import com.pasc.lib.hybrid.callback.ActivityResultCallback;
import com.pasc.lib.hybrid.callback.CallBackFunction;
import com.pasc.lib.hybrid.callback.StatisticsCallback;
import com.pasc.lib.hybrid.callback.ToolBarCallback;
import com.pasc.lib.hybrid.callback.WebActivityDestroyCallback;
import com.pasc.lib.hybrid.nativeability.WebStrategy;
import com.pasc.lib.hybrid.util.BridgeUtil;
import com.pasc.lib.hybrid.util.NetWorkUtils;
import com.pasc.lib.hybrid.webview.PascWebView;
import com.pasc.lib.smtbrowser.entity.NativeResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class PascHybrid {

    public static final String PROTOFUL = "data://fh5ios.com";
    private HashSet<String> mPath = new HashSet<>();
    /**
     * 持有web的callback集合
     */
    private SparseArray<Map<String, CallBackFunction>> mCallBackFunctions = new SparseArray<>();

    //当前Activity的HashCode
    private int currentActCode = -1;
    /**
     * 存储浏览器对象和浏览器所需的开放平台能力
     */
    public Map<PascWebView, List<String>> openplatformWebview =
            new HashMap<PascWebView, List<String>>();
    /**
     * 初始化hybrid参数（behavir和外部实现的接口）
     */
    HybridInitConfig mHybridInitConfig;

    public ActivityResultCallback activityResultCallback;
    public WebActivityDestroyCallback webActivityDestroyCallback;
    public ToolBarCallback toolBarCallback;
    public StatisticsCallback statisticsCallback;

    //存储所有activity的webstrategy
    public Map<Integer, WebStrategy> webStrategyMap = new HashMap<Integer, WebStrategy>();


    public static class SingletonHolder {
        public static PascHybrid instance = new PascHybrid();
    }

    private PascHybrid() {
    }

    public static PascHybrid getInstance() {
        return PascHybrid.SingletonHolder.instance;
    }

    public void setStatisticsCallback(StatisticsCallback statisticsCallback) {
        this.statisticsCallback = statisticsCallback;
    }

    public void setToolBarCallback(ToolBarCallback toolBarCallback) {
        this.toolBarCallback = toolBarCallback;
    }

    public void init(HybridInitConfig hybridInitConfig) {
        mHybridInitConfig = hybridInitConfig;
    }

    public HybridInitConfig getHybridInitConfig() {
        return mHybridInitConfig;
    }

    /**
     * 设置上传文件回调
     */
    public void setActivityResultCallback(ActivityResultCallback activityResultCallback) {
        this.activityResultCallback = activityResultCallback;
    }

    public void setWebActivityDestroyCallback(WebActivityDestroyCallback webActivityDestroyCallback) {
        this.webActivityDestroyCallback = webActivityDestroyCallback;
    }

    public PascHybrid with(WebPageConfig config) {
        DefaultBehaviorManager.getInstance().setWebPageConfig(config);
        return this;
    }

    public void start(Context context, String url) {
        if (null == mHybridInitConfig) {
            throw new IllegalArgumentException("Please call PascHybrid.getInstance().init() .");
        }
        WebPageConfig config = DefaultBehaviorManager.getInstance().getWebPageConfig();
        Map<String, BehaviorHandler> tempBehaviors = new HashMap<>(16);
        tempBehaviors.putAll(mHybridInitConfig.customerBehaviors);
        if (config != null) {
            tempBehaviors.putAll(config.getCustomerBehaviors());
        } else {
            config = new WebPageConfig.Builder().create();
            DefaultBehaviorManager.getInstance().setWebPageConfig(config);
        }
        DefaultBehaviorManager.getInstance().setCustomerBehaviors(tempBehaviors);
        boolean isOfflineMode = false;
        if (!TextUtils.isEmpty(url) && url.startsWith("file:///")) {
            isOfflineMode = true;
        }
        if (NetWorkUtils.isNetworkConnected(context) || isOfflineMode) {
            PascWebviewActivity.startWebviewActivity(context, url);
        } else {
            Intent intent = new Intent(context, NoNetActivity.class);
            intent.putExtra("type", "1");
            intent.putExtra("url", url);
            context.startActivity(intent);
        }
    }

    /**
     * 带策略启动
     */
    public void start(Context context, @NonNull WebStrategy strategy) {
        if (null == mHybridInitConfig) {
            throw new IllegalArgumentException("Please call PascHybrid.getInstance().init() .");
        }
        WebPageConfig config = DefaultBehaviorManager.getInstance().getWebPageConfig();
        Map<String, BehaviorHandler> tempBehaviors = new HashMap<>(16);
        tempBehaviors.putAll(mHybridInitConfig.customerBehaviors);
        if (config != null) {
            tempBehaviors.putAll(config.getCustomerBehaviors());
        } else {
            config = new WebPageConfig.Builder().create();
            DefaultBehaviorManager.getInstance().setWebPageConfig(config);
        }
        DefaultBehaviorManager.getInstance().setCustomerBehaviors(tempBehaviors);
        if(strategy != null ){
            webStrategyMap.put(strategy.hashCode(),strategy);
            boolean isOfflineMode = false;
            if (!TextUtils.isEmpty(strategy.url) && strategy.url.startsWith("file:///")) {
                isOfflineMode = true;
            }
            if (NetWorkUtils.isNetworkConnected(context) || isOfflineMode) {
                PascWebviewActivity.startWebviewActivity(context, strategy, strategy.hashCode());
            } else {
                Intent intent = new Intent(context, NoNetActivity.class);
                intent.putExtra("type", "2");
                intent.putExtra("strategy",strategy.hashCode());
                context.startActivity(intent);
            }
        }

    }

    /**
     * 带策略启动
     *  + 带回调，解决 openNewWebview 接口没有activity finish 回调的问题
     */
    public void startForResult(Activity activity, @NonNull WebStrategy strategy, int requestCode) {
        if (null == mHybridInitConfig) {
            throw new IllegalArgumentException("Please call PascHybrid.getInstance().init() .");
        }
        WebPageConfig config = DefaultBehaviorManager.getInstance().getWebPageConfig();
        Map<String, BehaviorHandler> tempBehaviors = new HashMap<>(16);
        tempBehaviors.putAll(mHybridInitConfig.customerBehaviors);
        if (config != null) {
            tempBehaviors.putAll(config.getCustomerBehaviors());
        } else {
            config = new WebPageConfig.Builder().create();
            DefaultBehaviorManager.getInstance().setWebPageConfig(config);
        }
        DefaultBehaviorManager.getInstance().setCustomerBehaviors(tempBehaviors);
        webStrategyMap.put(strategy.hashCode(),strategy);
        boolean isOfflineMode = false;
        if (!TextUtils.isEmpty(strategy.url) && strategy.url.startsWith("file:///")) {
            isOfflineMode = true;
        }
        if (NetWorkUtils.isNetworkConnected(activity) || isOfflineMode) {
            PascWebviewActivity.startWebviewActivityForResult(activity, strategy, strategy.hashCode(), requestCode);
        } else {
            Intent intent = new Intent(activity, NoNetActivity.class);
            intent.putExtra("type", "2");
            intent.putExtra("strategy",strategy.hashCode());
            activity.startActivityForResult(intent,requestCode);
        }
    }


    /**
     * 保存返回给web的callback
     * <p>
     * 一些操作需要延迟返回数据给web，比如拉起登录，登录成功后返回用户数据给web
     * 一个activity保存属于自己的WebView回调集合
     *
     * @param actHashcode  当前webViewActivity的Hashcode
     * @param protocolName 协议名
     * @param function     给H5的回调
     */
    public void saveCallBackFunction(int actHashcode, String protocolName,
                                     CallBackFunction function) {
        currentActCode = actHashcode;
        Map<String, CallBackFunction> currentActMap = mCallBackFunctions.get(currentActCode);
        if (null == currentActMap) {
            currentActMap = new HashMap<>(16);
            mCallBackFunctions.put(currentActCode, currentActMap);
        }
        if (!TextUtils.isEmpty(protocolName)) {
            currentActMap.put(protocolName, function);
        }
    }

    /**
     * 触发callback，返回数据给web
     */
    public <T> void triggerCallbackFunction(String protocolName, T t) {
        triggerCallbackFunction(protocolName, BridgeUtil.RESPONSE_CODE_SUCCESS, "", t);
    }

    /**
     * 触发callback，返回数据给web
     */
    public <T> void triggerCallbackFunction(String protocolName, int code, String msg, T t) {
        Map<String, CallBackFunction> currentActMap = mCallBackFunctions.get(currentActCode);
        if (!TextUtils.isEmpty(protocolName) && currentActMap != null && currentActMap.containsKey(
                protocolName)) {
            NativeResponse nativeResponse = new NativeResponse();
            nativeResponse.code = code;
            nativeResponse.data = t;
            nativeResponse.message = msg;
            currentActMap.get(protocolName).onCallBack(new Gson().toJson(nativeResponse));
            currentActMap.remove(protocolName);
        }
    }

    /**
     * 关闭App时调用
     */
    public void destroy() {
        mCallBackFunctions.clear();
        webStrategyMap.clear();
    }

    /**
     * 关闭当前WebViewActivity时，清空当前页面给H5的回调
     */
    public void removeCurrentParams(int actHashcode) {
        mCallBackFunctions.remove(actHashcode);
        webStrategyMap.remove(actHashcode);
    }

    public void addAuthorizationPath(String... path) {
        mPath.addAll(Arrays.asList(path));
    }

    public void addAuthorizationPath(List<String> path) {
        mPath.addAll(path);
    }

    public HashSet<String> getAuthorizationPath() {
        return mPath;
    }

}
