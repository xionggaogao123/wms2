package com.huanhong.wms.socket;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSON;
import io.socket.client.IO;
import io.socket.client.Socket;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

@Slf4j
@DisplayName("socket.io 客户端")
public class SocketClientTest {

    public static Socket socket;
    public static IO.Options ops;

    @BeforeAll
    public static void init() {
        ops = new IO.Options();
        ops.secure = true;
        ops.reconnection = true;
        // ops.timeout = 30000;
        try {
            socket = IO.socket("http://aux.aiairy.com:3232", ops);
            socket.on(Socket.EVENT_CONNECT, args -> socket.emit("login", "local_server", "1m7j28tno13g9zyhcw5lsv0kxpdabeua"));
            socket.on(Socket.EVENT_DISCONNECT, args -> log.warn(Socket.EVENT_DISCONNECT));
            socket.on(Socket.EVENT_RECONNECT, args -> log.info(Socket.EVENT_RECONNECT));
            socket.on(Socket.EVENT_RECONNECTING, args -> log.info(Socket.EVENT_RECONNECTING));
            // socket.on(Socket.EVENT_PING, args -> log.debug(Socket.EVENT_PING));
            socket.on(Socket.EVENT_PONG, args -> log.debug(Socket.EVENT_PONG));
            socket.on(Socket.EVENT_ERROR, args -> log.error(JSON.toJSONString(args)));
        } catch (URISyntaxException e) {
            log.error("socket.io 客户端异常 ==> ", e);
        }
    }

    @Test
    @DisplayName("测试监控设备socket")
    public void testDeviceSocket() {
        socket.on("accessControl", args -> log.debug(JSON.toJSONString(args)));
        socket.connect();
        do {
            socket.emit("heartbeat", "hb");
            ThreadUtil.sleep(60000);
        } while (true);
    }

    @Test
    @DisplayName("测试socket")
    public void testSocket() {
        socket.connect();
        do {
            socket.send("{\"dest\":\"7269FFFFFFFF\", \"source\":\"72690000002C\", \"func\":\"KeepOnline\",\n" +
                    "\"data\":[{\"node\": \"HB\"}]}");
            ThreadUtil.sleep(3000);
        } while (true);
    }


}