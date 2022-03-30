package com.huanhong.wms.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.properties.HikCloudProperties;
import com.huanhong.wms.service.HikCloudService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class HikCloudServiceImpl implements HikCloudService {

    @Autowired
    private HikCloudProperties hikCloudProperties;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public String getToken() {
        RBucket<String> rBucket = redissonClient.getBucket(StrUtil.format(HikCloudProperties.TOKEN_FORMAT, hikCloudProperties.getClientId()));
        if (rBucket.isExists()) {
            return rBucket.get();
        }
        String url = hikCloudProperties.getHost() + hikCloudProperties.getTokenUrl();
        Map<String, Object> params = new HashMap<>(8);
        params.put("client_id", hikCloudProperties.getClientId());
        params.put("client_secret", hikCloudProperties.getClientSecret());
        params.put("grant_type", "client_credentials");
        params.put("scope", "app");
        String response = HttpUtil.post(url, params);
        log.info("获取海康云眸 token,url:{},params:{},response:{}", url, params, response);
        String token = null;
        JSONObject result = JSONObject.parseObject(response);
        if (result.containsKey("access_token")) {
            token = result.getString("access_token");
            long expireTime = result.getLong("expires_in");
            rBucket.set(token);
            rBucket.expire(expireTime, TimeUnit.SECONDS);
        }
        return token;
    }

    @Override
    public Result getEzvizToken() {
        RBucket<String> rBucket = redissonClient.getBucket(StrUtil.format(HikCloudProperties.EZVIZ_TOKEN_FORMAT, hikCloudProperties.getClientId()));
        if (rBucket.isExists()) {
            return Result.success(JSONObject.parseObject(rBucket.get()));
        }
        String url = hikCloudProperties.getHost() + hikCloudProperties.getEzvizTokenUrl();
        String token = getToken();
        if (StrUtil.isBlank(token)) {
            log.warn("海康云眸token 获取失败");
            return Result.failure("云眸token 获取失败");
        }
        String response = HttpUtil.createGet(url).header("Authorization", "bearer " + token).execute().body();
        log.info("获取取流认证,url:{},response:{}", url, response);
        JSONObject result = JSONObject.parseObject(response);
        if (result.containsKey("code") && result.getInteger("code") != 200) {
            return Result.failure(result.getString("message"));
        }
        rBucket.set(result.getJSONObject("data").toJSONString());
        return Result.success(result.getJSONObject("data"));
    }
}
