package com.pasc.libbrowser;

import android.text.TextUtils;
import android.util.Log;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static okhttp3.logging.HttpLoggingInterceptor.Level.NONE;

/**
 * 接口生成器
 */
public class ApiGenerator {

    private final static String HOST = "https://smt-app-stg.pingan.com.cn:10019/";
    //新浪的 长连接->短链接
    private final static String SHARE_HOST = "https://api.weibo.com/";
    private final static String TAG = "ApiGenerator";

    private static Retrofit retrofit;

    private static Retrofit xmlRetrofit;

    private static final Gson HELPER_GSON = new Gson();

    private static  Retrofit shareRetrofit;

    static {
        // Create a trust manager that does not validate certificate chains
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

        SSLContext sslContext = null;
        try {
            // Install the all-trusting trust manager
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { trustManager }, new SecureRandom());
        } catch (Exception ignore) {
        }

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override public boolean verify(final String hostname, final SSLSession session) {
                return !TextUtils.isEmpty(hostname);
            }
        };

        Interceptor loggingInterceptor =
                new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override public void log(String message) {
                        if (message.startsWith("{") && message.endsWith("}")) {
                            Log.i(TAG,message);
                        } else if (message.startsWith("jsonData=")) {
                            try {
                                Log.i(TAG,URLDecoder.decode(message.replace("jsonData=", ""),
                                        "UTF-8"));
                            } catch (UnsupportedEncodingException ignore) {
                            }
                        } else {
                            Log.d(TAG, message);
                        }
                    }
                }).setLevel(NONE);

        Dispatcher dispatcher = new Dispatcher(Executors.newScheduledThreadPool(3));
        OkHttpClient.Builder builder =
                new OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES)
                        .readTimeout(1, TimeUnit.MINUTES)
                        .writeTimeout(1, TimeUnit.MINUTES)
                        .dispatcher(dispatcher)
                        .addInterceptor(loggingInterceptor)
                        .addNetworkInterceptor(new StethoInterceptor());
        if (sslContext != null) {
            builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                    .hostnameVerifier(hostnameVerifier);
        }
        OkHttpClient okHttpClient = builder.build();

        // 自定义Gson配置，处理接口List数据不传时，换成emptyList
        //Gson gson = new GsonBuilder().registerTypeAdapter(BaseResp.class,
        //        new JsonDeserializer<BaseResp>() {
        //            @Override public BaseResp deserialize(JsonElement json, Type typeOfT,
        //                    JsonDeserializationContext context) throws JsonParseException {
        //                BaseResp baseResp = HELPER_GSON.fromJson(json, typeOfT);
        //                Type itemType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
        //                Class clazz = itemType instanceof Class ? (Class) itemType
        //                        : (Class) ((ParameterizedType) itemType).getRawType();
        //                if (clazz == List.class && baseResp.data == null) {
        //                    baseResp.data = Collections.EMPTY_LIST;
        //                } else if (clazz == VoidObject.class) {
        //                    baseResp.data = VoidObject.getInstance();
        //                } else if (baseResp.data == null) {
        //                    try {
        //                        baseResp.data = clazz.newInstance();
        //                    } catch (InstantiationException e) {
        //                        e.printStackTrace();
        //                    } catch (IllegalAccessException e) {
        //                        e.printStackTrace();
        //                    }
        //                }
        //                return baseResp;
        //            }
        //        }).create();

        retrofit = new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(HOST)
                .client(okHttpClient)
                //.addConverterFactory(new ReqParamConverterFactory()) // 专用于适配本app的接口请求参数转换
                //.addConverterFactory(new NullOrEmptyConverterFactory())
                //.addConverterFactory(SimpleXmlConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        xmlRetrofit = new Retrofit.Builder().baseUrl(HOST)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                //.addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        //适用于将长连接转换为短链接
        shareRetrofit = new Retrofit.Builder().baseUrl(SHARE_HOST)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * 创建接口
     */
    public static <S> S createApi(Class<S> apiClass) {
        return retrofit.create(apiClass);
    }

    public static <S> S createShareApi(Class<S> apiClass){
        return shareRetrofit.create(apiClass);
    }

    /**
     * 创建接口
     */
    public static <S> S createXmlApi(Class<S> apiClass) {
        return xmlRetrofit.create(apiClass);
    }
}