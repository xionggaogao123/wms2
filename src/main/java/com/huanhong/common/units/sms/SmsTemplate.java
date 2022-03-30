package com.huanhong.common.units.sms;

/**
 * 短信通知模版
 *
 * @author ldy81
 * @date 2019/12/14 16:32
 */
public enum SmsTemplate {

    /**
     * 验证码${code}，您正在绑定手机号，感谢您的支持！
     */
    绑定手机号码("绑定手机号码", "SMS_187952700"),

    /**
     * 验证码${code}，您正在尝试修改登录密码，请妥善保管账户信息。
     */
    修改登录密码("修改登录密码", "SMS_187952700"),

    /**
     * 验证码${code}，您正在尝试找回登录密码，请妥善保管账户信息。
     */
    找回登录密码("找回登录密码", "SMS_187952700"),

    /**
     * 验证码${code}，您正在尝试找回签名密码，请妥善保管账户信息。
     */
    找回签名密码("找回签名密码", "SMS_187952700");



    private String title;
    private String code;
    private String content;
    private String uri;

    SmsTemplate(String title, String code, String content, String uri) {
        this.title = title;
        this.code = code;
        this.content = content;
        this.uri = uri;
    }

    SmsTemplate(String title, String code) {
        this.title = title;
        this.code = code;
    }

    public String getTitle() {
        return this.title;
    }

    public String getCode() {
        return this.code;
    }

    public String getContent() {
        return this.content;
    }

    public String getUri() {
        return this.uri;
    }

}
