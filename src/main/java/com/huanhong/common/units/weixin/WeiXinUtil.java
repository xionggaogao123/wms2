package com.huanhong.common.units.weixin;

import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huanhong.common.units.HttpUtils;
import com.huanhong.common.units.StrUtils;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 微信工具类
 *
 * @author zhls1
 */
@Slf4j
public class WeiXinUtil {

    /**
     * 方法getAccessToken的功能描述:TODO 获取微信 access_token
     *
     * @param
     * @return java.lang.String
     * @author 赵雷颂 zhls1992@qq.com
     * @since 2018-10-15
     */
    public static String getAccessToken(String appId) {
        String result = HttpUtils.get(StrUtils.format(WeixinConstant.TOKEN_URL, appId, WeixinConstant.APP_MAP.get(appId)));
        JSONObject jsonObject = StrUtils.toJSONObject(result);
        if (null == jsonObject) {
            return null;
        }
        if (jsonObject.containsKey(WeixinConstant.ERROR_CODE)) {
            log.error(result);
            return null;
        }
        return jsonObject.getString(WeixinConstant.ACCESS_TOKEN);
    }

    /**
     * 方法getJsapiTicket的功能描述:TODO 获取微信 JSAPI_TICKET
     *
     * @param accessToken
     * @return java.lang.String
     * @author 赵雷颂 zhls1992@qq.com
     * @since 2018-10-15
     */
    public static String getJsApiTicket(String accessToken) {
        String result = HttpUtils.get(StrUtils.format(WeixinConstant.TICKET_URL, accessToken));
        JSONObject jsonObject = StrUtils.toJSONObject(result);
        if (null == jsonObject) {
            return null;
        }
        if (0 != jsonObject.getIntValue(WeixinConstant.ERROR_CODE)) {
            log.error(result);
            return null;
        }
        return jsonObject.getString(WeixinConstant.JS_API_TICKET);
    }

    /**
     * 方法createSignature的功能描述:TODO JS-SDK 签名生成
     *
     * @param jsApiTicket
     * @param url
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @author 赵雷颂 zhls1992@qq.com
     * @since 2018-10-15
     */
    public static Map<String, Object> createSignature(String jsApiTicket, String url) {
        Map<String, Object> ret = new HashMap<>(8);
        String nonceStr = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis() / 1000;
        String string1;
        String signature = "";
        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsApiTicket +
                "&noncestr=" + nonceStr +
                "&timestamp=" + timestamp +
                "&url=" + url;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes(StandardCharsets.UTF_8));
            signature = Convert.toHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            log.error("createSignature 失败", e);
            e.printStackTrace();
        }
        ret.put("nonceStr", nonceStr);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);
        return ret;
    }

    /**
     * 方法code2Session的功能描述:TODO 登录凭证校验。通过 wx.login() 接口获得临时登录凭证 code 后传到开发者服务器调用此接口完成登录流程。
     *
     * @param appId
     * @param secret
     * @param js_code
     * @return java.lang.String
     * @author 赵雷颂 zhls1992@qq.com
     * @since 2018-10-31
     */
    public static JSONObject code2Session(String appId, String secret, String js_code) {
        String result = HttpUtils.get(StrUtils.format(WeixinConstant.CODE2SESSION, appId, secret, js_code));
        JSONObject jsonObject = StrUtils.toJSONObject(result);
        if (null == jsonObject) {
            return null;
        }
        if (0 != jsonObject.getIntValue(WeixinConstant.ERROR_CODE)) {
            log.error(result);
            return null;
        }
        return jsonObject;
    }

    /**
     * 方法sendTemplateMessage的功能描述:TODO 发送模版消息
     *
     * @param accessToken
     * @param templateMessage
     * @return com.alibaba.fastjson.JSONObject
     * @author 赵雷颂 zhls1992@qq.com
     * @since 2018-11-01
     */
    public static JSONObject sendTemplateMessage(String accessToken, TemplateMessage templateMessage) {
        String result = HttpUtils.post(StrUtils.format(WeixinConstant.MINI_SEND_TEMPLATE_MESSAGE, accessToken), JSON.toJSONString(templateMessage));
        JSONObject jsonObject = StrUtils.toJSONObject(result);
        if (null == jsonObject) {
            return null;
        }
        if (0 != jsonObject.getIntValue(WeixinConstant.ERROR_CODE)) {
            log.error(result);
            return null;
        }
        return jsonObject;
    }

    /**
     * 统一服务消息
     *
     * @param accessToken
     * @param templateMessage
     * @return
     */
    public static JSONObject sendUniFormMessage(String accessToken, MpTemplate templateMessage) {
        String result = HttpUtils.post(StrUtils.format(WeixinConstant.UNIFORM_SEND_MESSAGE, accessToken), JSON.toJSONString(templateMessage));
        JSONObject jsonObject = StrUtils.toJSONObject(result);
        log.info("param:{},result:{}", StrUtils.toJSONStr(templateMessage), jsonObject);
        if (null == jsonObject) {
            return null;
        }
        if (0 != jsonObject.getIntValue(WeixinConstant.ERROR_CODE)) {
            log.error(result);
            return null;
        }
        return jsonObject;
    }

    public static JSONObject sendSubscribeMessage(String accessToken, TemplateMessage templateMessage) {
        String result = HttpUtils.post(StrUtils.format(WeixinConstant.SUBSCRIBE_SEND_MESSAGE, accessToken), JSON.toJSONString(templateMessage));
        JSONObject jsonObject = StrUtils.toJSONObject(result);
        if (null == jsonObject) {
            return null;
        }
        if (0 != jsonObject.getIntValue(WeixinConstant.ERROR_CODE)) {
            log.error(result);
            return null;
        }
        return jsonObject;
    }


}
