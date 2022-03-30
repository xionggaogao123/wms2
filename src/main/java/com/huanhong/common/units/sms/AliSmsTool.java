package com.huanhong.common.units.sms;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.huanhong.common.units.StrUtils;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 新版阿里大鱼短信工具类
 * Date:2017/9/10 8:44
 * Created by 赵雷颂 ,zhls1992@qq.com
 *
 * @author Administrator
 */
@Slf4j
public class AliSmsTool {

    /**
     * 产品域名,开发者无需替换
     */
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";

    private static final String ACCESS_KEY_ID = "LTAInhg4UwhIGcIx";
    private static final String ACCESS_KEY_SECRET = "b7WLiUL8p4G9q0eD4YUDkk2MyjhDSL";

    private static final String SIGN_NAME = "焕鸿智慧云";

    /**
     * 发送短信验证码
     *
     * @param tel  手机号
     * @param type 类型 1注册 2登陆 3修改密码 4忘记密码
     * @author 赵雷颂 zhls1992@qq.com
     * @since 2017/9/10 9:07
     */
    public static SMSResult sendCode(int type, String tel, String code) {
        //验证码 ${code} 若非本人操作，请无视。
        String template = "SMS_119090209";
        if (type == 1) {
            //验证码${code}，您正在注册成为新用户，感谢您的支持！
            template = "SMS_94295320";
        } else if (type == 2) {
            //验证码${code}，您正在登录，若非本人操作，请勿泄露。
            template = "SMS_94295322";
        } else if (type == 3) {
            //验证码${code}，您正在尝试修改登录密码，请妥善保管账户信息。
            template = "SMS_94295319";
        } else if (type == 4) {
            //验证码${code}，您正在尝试修改登录密码，请妥善保管账户信息。
            template = "SMS_94295319";
        }
        Map<String, String> map = new HashMap<>(1);
        map.put("code", code);
        return send(tel, template, map);
    }

    /**
     * @param param    参数
     * @param tel      手机号
     * @param template 模版id
     * @author 赵雷颂 zhls1992@qq.com
     * @since 2017/9/10 9:03
     */
    public static SMSResult send(String tel, String template, Map<String, String> param) {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(DOMAIN);
        request.setAction("SendSms");
        request.setVersion("2017-05-25");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", tel);
        request.putQueryParameter("SignName", SIGN_NAME);
        request.putQueryParameter("TemplateCode", template);
        request.putQueryParameter("TemplateParam", StrUtils.toJSONStr(param));
        try {
            CommonResponse response = client.getCommonResponse(request);
            return JSON.parseObject(response.getData(), SMSResult.class);
        } catch (Exception e) {
            log.error("短信发送失败", e);
            SMSResult result = new SMSResult();
            result.setCode("system_error");
            result.setMessage("短信发送异常");
            return result;
        }
//            Code	描述
//            OK	请求成功
//            isp.RAM_PERMISSION_DENY	RAM权限DENY
//            isv.OUT_OF_SERVICE	业务停机
//            isv.PRODUCT_UN_SUBSCRIPT	未开通云通信产品的阿里云客户
//            isv.PRODUCT_UNSUBSCRIBE	产品未开通
//            isv.ACCOUNT_NOT_EXISTS	账户不存在
//            isv.ACCOUNT_ABNORMAL	账户异常
//            isv.SMS_TEMPLATE_ILLEGAL	短信模板不合法
//            isv.SMS_SIGNATURE_ILLEGAL	短信签名不合法
//            isv.INVALID_PARAMETERS	参数异常
//            isp.SYSTEM_ERROR	系统错误
//            isv.MOBILE_NUMBER_ILLEGAL	非法手机号
//            isv.MOBILE_COUNT_OVER_LIMIT	手机号码数量超过限制
//            isv.TEMPLATE_MISSING_PARAMETERS	模板缺少变量
//            isv.BUSINESS_LIMIT_CONTROL	业务限流
//            isv.INVALID_JSON_PARAM	JSON参数不合法，只接受字符串值
//            isv.BLACK_KEY_CONTROL_LIMIT	黑名单管控
//            isv.PARAM_LENGTH_LIMIT	参数超出长度限制
//            isv.PARAM_NOT_SUPPORT_URL	不支持URL
//            isv.AMOUNT_NOT_ENOUGH	账户余额不足
    }

    public static void main(String[] args) {
        Map<String, String> variable = new HashMap<>(3);
        variable.put("name", "刘德宜");
        variable.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")));
        System.out.println("variable => " + variable.toString());
        //SMSResult result = AliSmsTool.send("18621717406", SmsTemplate.访者最终预约信息_张东路.getCode(), variable);
        //System.out.println("AliSms Result => " + result.toString());
    }
}
