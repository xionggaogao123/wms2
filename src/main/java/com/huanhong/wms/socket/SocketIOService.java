package com.huanhong.wms.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.huanhong.wms.socket.impl.LoginEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * SocketIO 实现类
 *
 * @author 刘德宜 wudihaike@vip.qq.com
 * @version v1.0
 * @since 2018/10/18 12:07
 */
@Slf4j
@Service
public class SocketIOService implements com.huanhong.common.socketio.SocketIOService {

    @Value("${wss.server.enabled}")
    private boolean enabled;

    @Resource
    private SocketIOServer server;
    @Resource
    private LoginEventHandler loginEventHandler;

    /**
     * Spring IoC容器创建之后，在加载SocketIOServiceImpl Bean之后启动
     */
    @PostConstruct
    private void autoStartup() {
        if(enabled) {
            start();
        }
    }

    /**
     * Spring IoC容器在销毁SocketIOServiceImpl Bean之前关闭,避免重启项目服务端口占用问题
     */
    @PreDestroy
    private void autoStop() {
        if(enabled) {
            stop();
        }
    }

    @Override
    public void start() {
        // 监听客户端连接
        server.addConnectListener(client -> log.info("onConnect ==> uuid: {} ip: {}", client.getSessionId(), client.getRemoteAddress()));
        // 监听客户端断开连接
        server.addDisconnectListener(client -> {
            //  client.leaveRoom(client.get("room"));
            Object clientId = client.get("client_id");
            if (clientId != null) {
                Object clientName = client.get("client_name");
                log.warn("onDisconnect ==> id:{} name:{} 已离线", clientId, clientName);
            } else {
                log.debug("onDisconnect ==> uuid: {} ip: {}", client.getSessionId(), client.getRemoteAddress());
            }
        });
        // 登陆事件
        server.addListeners(loginEventHandler);
//        // 处理自定义的事件，与连接监听类似
//        socketIOServer.addEventListener(PUSH_EVENT, PushMessage.class, (client, data, ackSender) -> {
//            // TODO do something
//        });
        server.start();
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }
}