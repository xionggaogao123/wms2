package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Device;
import com.huanhong.wms.entity.dto.hik.DeviceGroup;
import com.huanhong.wms.entity.dto.hik.HikMqMessage;
import com.huanhong.wms.properties.HikCloudProperties;
import com.huanhong.wms.service.HikCloudService;
import com.huanhong.wms.service.IDeviceAlarmService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class HikCloudServiceImpl implements HikCloudService {

    @Autowired
    private HikCloudProperties hikCloudProperties;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IDeviceAlarmService deviceAlarmService;

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
        log.debug("获取海康云眸 token,url:{},params:{},response:{}", url, params, response);
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
        log.debug("获取取流认证,url:{},response:{}", url, response);
        JSONObject result = JSONObject.parseObject(response);
        if (result.containsKey("code") && result.getInteger("code") != 200) {
            return Result.failure(result.getString("message"));
        }
        rBucket.set(result.getJSONObject("data").toJSONString());
        return Result.success(result.getJSONObject("data"));
    }

    @Override
    public Result createConsumerGroup() {
        String url = hikCloudProperties.getHost() + hikCloudProperties.getMqConsumerGroupUrl();
        Map<String, Object> params = new HashMap<>(8);
        params.put("consumerName", "group1");
        String token = getToken();
        if (StrUtil.isBlank(token)) {
            log.warn("海康云眸token 获取失败");
            return Result.failure("云眸token 获取失败");
        }
        String response = HttpUtil.createPost(url).header("Authorization", "bearer " + token).form(params).execute().body();
        log.debug("创建消费者 ,url:{},params:{},response:{}", url, params, response);
        return getResult(response);
    }

    @Override
    public Result<List<HikMqMessage>> consumerMessage(String consumerId, Boolean autoCommit) {
        String url = hikCloudProperties.getHost() + hikCloudProperties.getMqConsumerMessageUrl();
        Map<String, Object> params = new HashMap<>(8);
        params.put("consumerId", consumerId);
        params.put("autoCommit", autoCommit);
        String token = getToken();
        if (StrUtil.isBlank(token)) {
            log.warn("海康云眸token 获取失败");
            return Result.failure("云眸token 获取失败");
        }
        String response = HttpUtil.createPost(url).header("Authorization", "bearer " + token).form(params).execute().body();
        log.debug("消费消息 ,url:{},params:{},response:{}", url, params, response);
        JSONObject jsonObject = JSONObject.parseObject(response);
        Result<List<HikMqMessage>> result = new Result();
        result.setOk(jsonObject.getBoolean("success"));
        result.setMessage(jsonObject.getString("message"));
        result.setStatus(jsonObject.getInteger("code"));
        result.setData(JSON.parseArray(jsonObject.getString("data"), HikMqMessage.class));
        return result;
    }

    @Override
    public Result consumerOffset(String consumerId) {
        String url = hikCloudProperties.getHost() + hikCloudProperties.getMqConsumerOffsetUrl();
        Map<String, Object> params = new HashMap<>(8);
        params.put("consumerId", consumerId);
        String token = getToken();
        if (StrUtil.isBlank(token)) {
            log.warn("海康云眸token 获取失败");
            return Result.failure("云眸token 获取失败");
        }
        String response = HttpUtil.createPost(url).header("Authorization", "bearer " + token).form(params).execute().body();
        log.debug("提交偏移量 ,url:{},params:{},response:{}", url, params, response);
        return getResult(response);
    }

    @Override
    public void consumerMessage() {
        String consumerId = null;
        RBucket<String> rBucket = redissonClient.getBucket(StrUtil.format(HikCloudProperties.CONSUMER_GROUP_FORMAT, hikCloudProperties.getClientId()));
        if (rBucket.isExists()) {
            consumerId = rBucket.get();
        } else {
            Result result = createConsumerGroup();
            if (result.isOk()) {
                consumerId = ((JSONObject) result.getData()).getString("consumerId");
                rBucket.set(consumerId);
            } else {
                log.warn("consumerMessage-createConsumerGroup error:{}", result);
                return;
            }
        }
        // 开始消费
        Result<List<HikMqMessage>> result = consumerMessage(consumerId, false);
        if (!result.isOk()) {
            log.warn("consumerMessage-consumerMessage error:{}", result);
            Result result2 = createConsumerGroup();
            if (result2.isOk()) {
                consumerId = ((JSONObject) result2.getData()).getString("consumerId");
                rBucket.set(consumerId);
            } else {
                log.warn("consumerMessage-createConsumerGroup error:{}", result);
                return;
            }
            result = consumerMessage(consumerId, false);
            if (!result.isOk()) {
                log.warn("consumerMessage-consumerMessage 2 error:{}", result);
                return;
            }

        }
        // 消息入库
        List<HikMqMessage> data = result.getData();
        if (CollectionUtil.isEmpty(data)) {
//            log.info("海康云眸暂无消息");
            return;
        }
        Result add = deviceAlarmService.add(data);
        if (!add.isOk()) {
            log.warn("consumerMessage-deviceAlarmService error:{}", add);
            return;
        }
        // 提交偏移量
        Result offset = consumerOffset(consumerId);
        if (!offset.isOk()) {
            log.warn("consumerMessage-consumerOffset error:{}", result);
            return;
        }

    }

    @Override
    public Result createDeviceGroup(DeviceGroup deviceGroup) {
        String url = hikCloudProperties.getHost() + hikCloudProperties.getGroupCreateUrl();
        Map<String, Object> params = BeanUtil.beanToMap(deviceGroup);

        String token = getToken();
        if (StrUtil.isBlank(token)) {
            log.warn("海康云眸token 获取失败");
            return Result.failure("云眸token 获取失败");
        }
        String response = HttpUtil.createPost(url).header("Authorization", "bearer " + token).form(params).execute().body();
        log.debug("创建设备组 ,url:{},params:{},response:{}", url, params, response);
        return getResult(response);
    }

    @Override
    public Result getDeviceGroup(String groupNo) {
        String url = hikCloudProperties.getHost() + hikCloudProperties.getGroupGetUrl();
        url = StrUtil.format(url, groupNo);
        String token = getToken();
        if (StrUtil.isBlank(token)) {
            log.warn("海康云眸token 获取失败");
            return Result.failure("云眸token 获取失败");
        }
        String response = HttpUtil.createGet(url).header("Authorization", "bearer " + token).execute().body();
        log.debug("获取设备组 ,url:{},response:{}", url, response);
        return getResult(response);
    }

    @Override
    public Result createDevice(Device device) {
        String url = hikCloudProperties.getHost() + hikCloudProperties.getDeviceCreateUrl();
        Map<String, Object> params = BeanUtil.beanToMap(device);

        String token = getToken();
        if (StrUtil.isBlank(token)) {
            log.warn("海康云眸token 获取失败");
            return Result.failure("云眸token 获取失败");
        }
        String response = HttpUtil.createPost(url).header("Authorization", "bearer " + token).form(params).execute().body();
        log.debug("注册设备 ,url:{},params:{},response:{}", url, params, response);
        return getResult(response);
    }

    @Override
    public Result getDevice(String deviceSerial) {
        String url = hikCloudProperties.getHost() + hikCloudProperties.getDeviceGetUrl();
        url = StrUtil.format(url, deviceSerial);
        String token = getToken();
        if (StrUtil.isBlank(token)) {
            log.warn("海康云眸token 获取失败");
            return Result.failure("云眸token 获取失败");
        }
        String response = HttpUtil.createGet(url).header("Authorization", "bearer " + token).execute().body();
        log.debug("获取设备 ,url:{},response:{}", url, response);
        return getResult(response);
    }

    @NotNull
    private Result getResult(String response) {
        JSONObject jsonObject = JSONObject.parseObject(response);
        Result result = new Result();
        result.setOk(jsonObject.getBoolean("success"));
        result.setMessage(jsonObject.getString("message"));
        result.setStatus(jsonObject.getInteger("code"));
        result.setData(jsonObject.get("data"));
        return result;
    }
}
