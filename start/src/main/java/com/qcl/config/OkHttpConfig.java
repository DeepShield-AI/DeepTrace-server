package com.qcl.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
/**
 * OkHttpClient配置类
 * Created by macro on 2025/11/20.
 */
@Configuration
public class OkHttpConfig {

    /**
     * 默认连接超时时间（秒）
     */
    private static final int DEFAULT_CONNECT_TIMEOUT = 10;

    /**
     * 默认读取超时时间（秒）
     */
    private static final int DEFAULT_READ_TIMEOUT = 30;

    /**
     * 默认写入超时时间（秒）
     */
    private static final int DEFAULT_WRITE_TIMEOUT = 30;

    /**
     * 信任所有证书的TrustManager
     */
    private static final TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            }
    };

    /**
     * SSL上下文
     */
    private static final SSLContext trustAllSslContext;

    static {
        try {
            trustAllSslContext = SSLContext.getInstance("SSL");
            trustAllSslContext.init(null, trustAllCerts, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * SSL Socket工厂
     */
    private static final SSLSocketFactory trustAllSslSocketFactory = trustAllSslContext.getSocketFactory();

    /**
     * 创建OkHttpClient Bean实例
     * 全局共用一个OkHttpClient实例，以复用连接池提升性能
     *
     * @return OkHttpClient实例
     */
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(32, 30, TimeUnit.MINUTES))
//                .sslSocketFactory(trustAllSslSocketFactory, (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier((hostname, session) -> true)
                .build();
    }
}
