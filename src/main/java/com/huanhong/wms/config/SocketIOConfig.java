package com.huanhong.wms.config;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIOConfig {

    @Value("${wss.server.host}")
    private String host;

    @Value("${wss.server.port}")
    private Integer port;

    @Value("${wss.server.timeout}")
    private Integer timeout;

    @Value("${wss.server.upgrade-timeout}")
    private Integer upgradeTimeout;

    @Value("${wss.server.interval}")
    private Integer interval;

    @Bean
    public SocketIOServer socketIOServer() {
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setTcpNoDelay(true);
        socketConfig.setSoLinger(0);
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setSocketConfig(socketConfig);
        config.setHostname(host);
        config.setPort(port);
        config.setUpgradeTimeout(upgradeTimeout);
        config.setPingTimeout(timeout);
        config.setPingInterval(interval);
        //该处可以用来进行身份验证
        config.setAuthorizationListener(data -> {
            // http://localhost:8081?username=test&password=test
            // 例如果使用上面的链接进行connect，可以使用如下代码获取用户密码信息，本文不做身份验证
            // String username = data.getSingleUrlParam("username");
            // String password = data.getSingleUrlParam("password");
            return true;
        });
        return new SocketIOServer(config);
    }

}
