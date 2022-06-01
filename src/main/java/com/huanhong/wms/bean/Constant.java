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

    String BASIC = "Basic ";

    String LOCALHOST_IP = "127.0.0.1";

    String REQUEST_ID = "reqId";

    String CONTENT_TYPE = "Content-Type";

    String CONTENT_TYPE_JSON = "application/json";

    String OK = "OK";

    String SMS_CODE_PREFIX = "SmsCode-";
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
    /**
     * id
     */
    String ID = "id";

    /**
     * 名称
     */
    String NAME = "name";

    /**
     * 编码
     */
    String CODE = "code";

    /**
     * 值
     */
    String VALUE = "value";
    /**
     * 未知标识
     */
    String UNKNOWN = "Unknown";

    /**
     * 用户代理
     */
    String USER_AGENT = "User-Agent";

    Integer CHECK_STATUS_0 = 0;

    Integer CHECK_STATUS_1 = 1;

    Integer STATUS_9999 = 9999;

    Double AMOUNT_0 = 0.000000000001;
}

