package com.huanhong.wms.bean;

/**
 * 项目常量bean
 *
 * @author 刘德宜 wudihaike@vip.qq.com
 * @version v1.0
 * @since 2017/12/18 11:09
 */
public interface Constant {

    int HTTP_SUCCEED = 200;

    String HEARTBEAT = "heartbeat";

    String GUEST = "guest";

    String BEARER = "Bearer ";

    String LOCALHOST_IP = "127.0.0.1";

    String REQUEST_ID = "reqId";

    String CONTENT_TYPE = "Content-Type";

    String CONTENT_TYPE_JSON = "application/json";

    String OK = "OK";

    /**
     * 逗号分隔符
     */
    String COMMA_SEPARATOR = ",";

    /**
     * 26字母
     */
    char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    /**
     * 编码
     */
    String UTF8 = "UTF-8";

}

