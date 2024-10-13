package com.pasc.lib.hybrid.behavior;

import com.pasc.lib.hybrid.callback.WebSettingCallback;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * create by wujianning385 on 2018/7/19.
 */
public class WebPageConfig implements Serializable{
    private WebSettingCallback webSettingCallback;

    public void setWebSettingCallback(WebSettingCallback webSettingCallback) {
        this.webSettingCallback = webSettingCallback;
    }

    public WebSettingCallback getWebSettingCallback() {
        return webSettingCallback;
    }

    /**
     * 扩展提供给web的接口
     */
    private Map<String ,BehaviorHandler> customerBehaviors;
    /**
     * 针对一些第三方的页面，或者老版本的交互行为，打开页面只能添加一个
     */
    private Map<String, Object> jsInterfaces;

    public Map<String, BehaviorHandler> getCustomerBehaviors() {
        return customerBehaviors;
    }

    public void setCustomerBehaviors(Map<String, BehaviorHandler> customerBehaviors) {
        this.customerBehaviors = customerBehaviors;
    }

    public Map<String, Object> getJsInterfaces() {
        return jsInterfaces;
    }

    public void setJsInterfaces(Map<String, Object> jsInterfaces) {
        this.jsInterfaces = jsInterfaces;
    }

    public static class Builder implements Serializable{

        private WebSettingCallback webSettingCallback;

        private Map<String ,BehaviorHandler> customerBehaviors = new HashMap<>(16);

        private Map<String, Object> jsInterfaces= new HashMap<>(1);

        public Builder addCustomerBehavior(String handlerName,BehaviorHandler behaviorHandler){
            customerBehaviors.put(handlerName,behaviorHandler);
            return this;
        }

        public Builder addJsInterface(String handlerName,Object obj){
            jsInterfaces.clear();
            jsInterfaces.put(handlerName,obj);
            return this;
        }

        public Builder setWebSettingCallback(WebSettingCallback webSettingCallback){
            this.webSettingCallback = webSettingCallback;
            return this;
        }

        public WebPageConfig create(){
            WebPageConfig config = new WebPageConfig();
            config.setCustomerBehaviors(customerBehaviors);
            config.setJsInterfaces(jsInterfaces);
            config.setWebSettingCallback(webSettingCallback);
            return config;
        }
    }
}
