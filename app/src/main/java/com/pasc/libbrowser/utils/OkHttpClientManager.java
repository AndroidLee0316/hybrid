package com.pasc.libbrowser.utils;

import android.text.TextUtils;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;

/**
 * Created by yangwen881 on 17/2/24.
 */
class OkHttpClientManager {

    public static final int CONNECT_TIMEOUT_SECOND = 10;
    public static final int WRITE_TIMEOUT_SECOND = 10;
    public static final int READ_TIMEOUT_SECOND = 30;

    private OkHttpClient mOkHttpClient;

    private OkHttpClientManager() {
    }

    public static OkHttpClientManager getInstance() {
        return OkHttpClientManagerHolder.sOkHttpClientManager;
    }

    private static class OkHttpClientManagerHolder {
        public static OkHttpClientManager sOkHttpClientManager = new OkHttpClientManager();
    }

    /**
     * 默认HttpClient设置，信任所有HTTPS请求。
     */
    private OkHttpClient initDefaultOkHttpClient() {
        return getDefaultOkHttpBuilder().build();
    }

    public static OkHttpClient.Builder getDefaultOkHttpBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[] {new UnSafeTrustManager()}, new SecureRandom());

            builder.connectTimeout(CONNECT_TIMEOUT_SECOND, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT_SECOND, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT_SECOND, TimeUnit.SECONDS)
                    .hostnameVerifier(new UnSafeHostnameVerifier())
                    .sslSocketFactory(sc.getSocketFactory());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return builder;
    }

    public static class UnSafeTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static class UnSafeHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return !TextUtils.isEmpty(hostname);
        }
    }
}