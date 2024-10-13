LibWebPage
====================
介绍
--------
##### 平安智慧城统一hybrid框架
Download
--------
##### 在APP的Gradle中配置，版本号请使用最新版本

    implementation 'com.pasc.lib:hybrid:1.1.86-SNAPSHOT'

Usage
---
1.在application中初始化，可以在HybridOptions中设置一些功能选项接口

    Dove.getInstance().init(options());
    private HybridOptions options() {
        HybridOptions hybridOptions = new HybridOptions();
        hybridOptions.setLoadImageCallback(new LoadImageCallback() {
          @Override public void loadImage(ImageView imageView, String url) {
            Picasso.with(applicationContext).load(url).placeholder(R.color.gray_333333).into(imageView);
          }
        });
        hybridOptions.setWebSettingsCallback(new WebSettingsCallback() {
          @Override public void onWebSettings(WebSettings settings) {
            settings.setUserAgent(settings.getUserAgentString()
                    + "/NTSMT_Android,VERSION:"
                    + DeviceUtils.getVersionName(App.this));
          }
        });
        return hybridOptions;
     }
     
2.在打开WebView之前，需要在WebConfig中添加交互行为

    WebPageConfig webPageConfig = new WebPageConfig.Builder()
            .addCustomerBehavior(ConstantBehaviorName.LOG_EVENT, new WebLogBehavior())//添加自定义的交互行为
            .setBackIconRes(R.mipmap.temp_ic_back)//可设置返回按钮图标
            .create();
  打开WebView方式，可选择哪种打开策略。具体策略可参考源码。
  
     Dove.getInstance()
         .with(webPageConfig)
         .start(getActivity(), new WebStrategy().setUrl(url)
         .setStatusBarVisibility(WebStrategyType.STATUSBAR_VISIBLE));
3.新建自定义交互行为，比如上一步中的**addCustomerBehavior(ConstantBehaviorName.LOG_EVENT, new WebLogBehavior())**，
第一个参数表示交互协议名，第二个参数是交互逻辑实体对象。强烈建议自定义交互
行为实现**Serializable**，避免异常回收当前WebViewActivity后，重新打开找不到交互方法。
下面是一个日志记录行为例子：

     public class WebLogBehavior implements BehaviorHandler,Serializable{
     
         public static final String TAG = WebLogBehavior.class.getSimpleName();
     
         /*
          context:当前webView的context
          data：H5端传给我们的数据
          function：通过CallBackFunction对象返回给H5数据
          response: 智慧城hybrid协商的返回数据格式对象，调用方可自行修改对象参数，具体文档：
          http://iqsz-d6889:8090/pages/viewpage.action?pageId=4852675
          */
         @Override public void handler(Context context, String data, CallBackFunction function,
                 NativeResponse response) {
             Gson gson = new Gson();
             WebLogBean webLog = gson.fromJson(data,WebLogBean.class);
             switch (webLog.level){
                 case 0:
                     XLog.tag(TAG).i(webLog.info);
                     break;
                 case 1:
                     XLog.tag(TAG).i(webLog.info);
                     break;
                 case 2:
                     XLog.tag(TAG).w(webLog.info);
                     break;
                 case 3:
                     XLog.tag(TAG).e(webLog.info);
                     break;
             }
           function.onCallBack(gson.toJson(response));
         }
     }
4.[交互行为说明文档](http://iqsz-d6889:8090/pages/viewpage.action?pageId=4852675),
需平安内网打开

Note
----
- 目前hybrid内聚的默认交互行为已经在上面的[交互行为说明文档](http://iqsz-d6889:8090/pages/viewpage.action?pageId=4852675)中有所记录，调用者可自行查看，
如果默认的交互行为不符合需求，请添加实现自定义交互行为。
- 开放平台目前是另外一个组件，并不内聚在hybrid中，项目如果需要引入开放平台功能的话，
请重新添加开放平台依赖，具体参照开放平台组件文档