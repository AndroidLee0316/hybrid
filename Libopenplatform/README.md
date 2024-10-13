LibOpenPlatform
====================
介绍
--------
##### 平安智慧城开放平台组件
Download
--------
##### 在APP的Gradle中配置，版本号请使用最新版本
    implementation 'com.pasc.lib:open-platform:0.0.5-SNAPSHOT'
    
Usage
---
1.在application中初始化，实现**OpenPlatformProvider**
    
    OpenPlatformManager.getInstance().init(new OpenPlatformProvider() {
          /*
             开放平台的baseURL，不同城市有不同的host
             */
          @Override public String getOpenPlatformBaseUrl() {
            return BuildConfig.DEBUG ? "http://sz-smt-zag-stg1.pingan.com.cn:10080/smtapp"
                    : "https://smt-app.pingan.com.cn/smtapp";
          }
          /*
             开放平台需要上层提供用户token
             */
          @Override public String getUserToken() {
            return null;
          }
          /*
             开放平台可以动态注册交互行为，由server端返回交互行为名称列表，我们再去动态注册交互行为，可以保证
             一定的安全性
             nativeApis: 需要注册的交互行为协议名称列表
             */
          @Override public void openPlatformBehavior(PascWebView pascWebView, List<String> nativeApis) {
    
          }
        });

Note
---
- 该库已引用pascHybrid框架，接入方不用重复引入pascHybrid