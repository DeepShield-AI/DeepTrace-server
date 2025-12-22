package com.qcl.utils;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.apache.lucene.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * OkHttp工具类
 * 封装常用的HTTP请求方法
 * Created by macro on 2025/11/20.
 */
@Component
public class OkHttpUtil {

    private static OkHttpClient okHttpClient;

    @Autowired
    public OkHttpUtil(OkHttpClient okHttpClient) {
        OkHttpUtil.okHttpClient = okHttpClient;
    }

    /**
     * GET请求
     *
     * @param url 请求URL
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String get(String url) throws IOException {
        return get(url, null,null);
    }

    public static String get(String url, Map<String,String> params) throws IOException {
        return get(url, params,null);
    }


    /**
     * GET请求
     *
     * @param url     请求URL
     * @param headers 请求头
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String get(String url, Map<String,String> params, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder().url(buildUrlWithParams(url, params));

        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.body() == null) {
                return "";
            }
            return response.body().string();
        }
    }

    /**
     * GET请求并返回详细响应
     *
     * @param url     请求URL
     * @param headers 请求头
     * @return 完整的响应对象
     * @throws IOException IO异常
     */
    public static Response getWithResponse(String url, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);

        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.build();
        return okHttpClient.newCall(request).execute();
    }


    /**
     * 构建带参数的完整URL
     * @param baseUrl 基础URL
     * @param params 参数Map
     * @return 完整URL
     */
    private static String buildUrlWithParams(String baseUrl, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }

        String queryString = buildQueryString(params);
        return baseUrl + queryString;
    }

    /**
     * 将参数Map转换为URL查询字符串
     * @param params 参数Map
     * @return URL查询字符串，格式如: ?key1=value1&key2=value2
     */
    private static String buildQueryString(Map<String, String> params) {
        if (CollectionUtils.isEmpty(params)) {
            return "";
        }

        StringBuilder queryString = new StringBuilder("?");
        boolean first = true;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) {
                queryString.append("&");
            }

            try {
                String encodedKey = java.net.URLEncoder.encode(entry.getKey(), "UTF-8");
                String encodedValue = java.net.URLEncoder.encode(entry.getValue(), "UTF-8");
                queryString.append(encodedKey).append("=").append(encodedValue);
            } catch (java.io.UnsupportedEncodingException e) {
                // 如果编码失败，使用原始值
                queryString.append(entry.getKey()).append("=").append(entry.getValue());
            }

            first = false;
        }

        return queryString.toString();
    }

    /**
     * POST请求（JSON数据）
     *
     * @param url  请求URL
     * @param json JSON数据
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String postJson(String url, Map<String, String> params) throws IOException {
        return postJson(url, params, null);
    }

    /**
     * POST请求（JSON数据）
     *
     * @param url     请求URL
     * @param json    JSON数据
     * @param headers 请求头
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String postJson(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        MediaType jsonType = MediaType.get("application/json; charset=utf-8");
        String json = JSON.toJSONString(params);
        RequestBody body = RequestBody.create(json, jsonType);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);

        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.body() == null) {
                return "";
            }
            return response.body().string();
        }
    }

    /**
     * POST请求并返回详细响应
     *
     * @param url     请求URL
     * @param json    JSON数据
     * @param headers 请求头
     * @return 完整的响应对象
     * @throws IOException IO异常
     */
    public static Response postJsonWithResponse(String url, String json, Map<String, String> headers) throws IOException {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(body);

        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.build();
        return okHttpClient.newCall(request).execute();
    }

    /**
     * POST请求（表单数据）
     *
     * @param url    请求URL
     * @param params 表单参数
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String postForm(String url, Map<String, String> params) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return postForm(url, params, headers);
    }

    /**
     * POST请求（表单数据）
     *
     * @param url     请求URL
     * @param params  表单参数
     * @param headers 请求头
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String postForm(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (params != null) {
            params.forEach(formBuilder::add);
        }

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(formBuilder.build());

        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.body() == null) {
                return "";
            }
            return response.body().string();
        }
    }

    /**
     * POST请求（表单数据）并返回详细响应
     *
     * @param url     请求URL
     * @param params  表单参数
     * @param headers 请求头
     * @return 完整的响应对象
     * @throws IOException IO异常
     */
    public static Response postFormWithResponse(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (params != null) {
            params.forEach(formBuilder::add);
        }

        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(formBuilder.build());

        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.build();
        return okHttpClient.newCall(request).execute();
    }


    /**
     * PUT请求（JSON数据）
     *
     * @param url  请求URL
     * @param params  请求参数
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String putJson(String url,  Map<String, String> params) throws IOException {
        return putJson(url, params, null);
    }

    /**
     * PUT请求（JSON数据）
     *
     * @param url     请求URL
     * @param params  请求参数
     * @param headers 请求头
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String putJson(String url,  Map<String, String> params, Map<String, String> headers) throws IOException {
        MediaType jsonType = MediaType.get("application/json; charset=utf-8");
        String json = JSON.toJSONString(params);
        RequestBody body = RequestBody.create(json, jsonType);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .put(body);

        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.body() == null) {
                return "";
            }
            return response.body().string();
        }
    }

    /**
     * PUT请求（JSON数据）并返回详细响应
     *
     * @param url     请求URL
     * @param json    JSON数据
     * @param headers 请求头
     * @return 完整的响应对象
     * @throws IOException IO异常
     */
    public static Response putJsonWithResponse(String url, String json, Map<String, String> headers) throws IOException {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, JSON);

        Request.Builder builder = new Request.Builder()
                .url(url)
                .put(body);

        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.build();
        return okHttpClient.newCall(request).execute();
    }

    /**
     * DELETE请求
     *
     * @param url 请求URL
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String delete(String url) throws IOException {
        return delete(url, null);
    }

    /**
     * DELETE请求
     *
     * @param url     请求URL
     * @param headers 请求头
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String delete(String url, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .delete();

        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.body() == null) {
                return "";
            }
            return response.body().string();
        }
    }
    /**
     * DELETE请求并返回详细响应
     *
     * @param url     请求URL
     * @param headers 请求头
     * @return 完整的响应对象
     * @throws IOException IO异常
     */
    public static Response deleteWithResponse(String url, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .delete();

        if (headers != null) {
            headers.forEach(builder::addHeader);
        }

        Request request = builder.build();
        return okHttpClient.newCall(request).execute();
    }

    /**
     * 自定义请求
     *
     * @param request 请求对象
     * @return 响应结果
     * @throws IOException IO异常
     */
    public static String execute(Request request) throws IOException {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.body() == null) {
                return "";
            }
            return response.body().string();
        }
    }

    /**
     * 自定义请求并返回详细响应
     *
     * @param request 请求对象
     * @return 完整的响应对象
     * @throws IOException IO异常
     */
    public static Response executeWithResponse(Request request) throws IOException {
        return okHttpClient.newCall(request).execute();
    }

    /**
     * 异步请求
     *
     * @param request  请求对象
     * @param callback 回调函数
     */
    public static void enqueue(Request request, Callback callback) {
        okHttpClient.newCall(request).enqueue(callback);
    }
}