package com.huanhong.common.units.weixin;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信常量
 *
 * @author zhls1
 */
public interface WeixinConstant {

    /**
     * 小程序 appid
     */
    String MINI_APP_ID = "wx3b0e82a66fd10423";
    /**
     * 小程序 appSecret
     */
    String MINI_APP_SECRET = "58cc84c90b22442ad3aebb28775a0cde";
    /**
     * 公众号 appid
     */
    String APP_ID = "wxb74c5e4b5aa9e4db";
    /**
     * 公众号 appSecret
     */
    String APP_SECRET = "6c4f363d8d9d5d50b717a6afa69d6950";

    /**
     * 验证码模版消息id
     */
    String CODE_MSG_TEMPLATE_ID = "ChuzLGQX8U6BOU_d3Vip0xKkx1tIzkAMCwsMguknBW8";


    /**
     * 微信错误码
     */
    String ERROR_CODE = "errcode";
    /**
     * access_token
     */
    String ACCESS_TOKEN = "access_token";

    String ACCESS_TOKEN_PREFIX = "dsl:access_token";
    /**
     * jsapi_ticket
     */
    String JS_API_TICKET = "ticket";

    String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={}&secret={}";

    String TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token={}&type=jsapi";
    /**
     * 登录凭证校验
     */
    String CODE2SESSION = "https://api.weixin.qq.com/sns/jscode2session?appid={}&secret={}&js_code={}&grant_type=authorization_code";
    /**
     * 小程序发送模板消息
     */
    String MINI_SEND_TEMPLATE_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token={}";
    String UNIFORM_SEND_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/uniform_send?access_token={}";
    String SUBSCRIBE_SEND_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token={}";
    /**
     * 小程序待付款模版消息
     */
    String MINI_UNPAID_TEMPLATE_ID = "QxtcOH7X7pdsd_H0H88FlLLnl6h4Vy--4kleUbXf8rk";
    /**
     * 微信app map
     */
    Map<String, String> APP_MAP = new HashMap<String, String>() {
        private static final long serialVersionUID = -5520810184444959348L;

        {
            put(MINI_APP_ID, MINI_APP_SECRET);
            put(APP_ID, APP_SECRET);

        }
    };



}
