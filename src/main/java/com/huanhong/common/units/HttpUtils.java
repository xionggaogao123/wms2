package com.huanhong.common.units;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.Method;

import java.util.Map;

/**
 * TODO: httpUtils
 * Date:2018/8/12 18:18
 * Created by 赵雷颂 ,zhls1992@qq.com
 */
public class HttpUtils extends cn.hutool.http.HttpUtil {

    /**
     * 发起get请求
     * @param url
     * @param headers
     * @return
     */
    public static String doGet(String url, Map<String, String> headers) {
        return cn.hutool.http.HttpUtil.createGet(url)
                .addHeaders(headers).execute().body();
    }

    /**
     * 发起post请求
     * @param url
     * @param headers
     * @param body
     * @return
     */
    public static String doPost(String url, Map<String, String> headers, String body) {
        return cn.hutool.http.HttpUtil.createPost(url)
                .addHeaders(headers).body(body).execute().body();
    }
    /**
     * 发起put请求
     * @param url
     * @param headers
     * @param body
     * @return
     */
    public static String doPut(String url, Map<String, String> headers, String body) {
        return HttpRequest.put(url)
                .addHeaders(headers).body(body).execute().body();
    }

    /**
     * 发起delete请求
     * @param url
     * @param headers
     * @return
     */
    public static String doDelete(String url, Map<String, String> headers) {
        return cn.hutool.http.HttpUtil.createRequest(Method.DELETE,url)
                .addHeaders(headers).execute().body();
    }

    /**
     * 发送post请求
     *
     * @param urlString 网址
     * @param paramMap put表单数据
     * @return 返回数据
     */
    public static String put(String urlString, Map<String, Object> paramMap) {
        return put(urlString, paramMap, -1);
    }

    /**
     * 发送post请求
     *
     * @param urlString 网址
     * @param paramMap put表单数据
     * @param timeout 超时时长，-1表示默认超时，单位毫秒
     * @return 返回数据
     * @since 3.2.0
     */
    public static String put(String urlString, Map<String, Object> paramMap, int timeout) {
        return HttpRequest.put(urlString).form(paramMap).timeout(timeout).execute().body();
    }

}
