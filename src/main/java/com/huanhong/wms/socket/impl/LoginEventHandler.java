package com.huanhong.wms.socket.impl;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 登陆事件处理
 *
 * @author ldy81
 * @date 2020/5/11 18:17
 */
@Slf4j
@Service
public class LoginEventHandler {

    @Resource
    private RedissonClient redissonClient;

    /**
     * socket.io登录
     *
     * @param client     SocketIO客户端
     * @param clientType 客户端类型
     * @param license    许可证
     * @param request    请求
     */
    @OnEvent(value = "login")
    public void onLogin(SocketIOClient client, String clientType, String license, AckRequest request) {
        log.debug("onLogin ==> ip:{} clientType:{} license:{}", client.getRemoteAddress(), clientType, license);

        client.set("room", "store_" + license);
        client.joinRoom("store_" + license);
        log.info("onLogin ==> storeId: {} 已上线", license);
    }

}
