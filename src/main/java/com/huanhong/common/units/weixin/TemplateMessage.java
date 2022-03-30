package com.huanhong.common.units.weixin;

import lombok.Data;

/**
 * 微信模版消息
 * @author zhls1
 */
@Data
public class TemplateMessage {
    /**
     * 接收者（用户）的 openid
     */
    private String touser;
    /**
     * 点击模板卡片后的跳转页面，仅限本小程序内的页面。支持带参数,（示例index?foo=bar）。该字段不填则模板无跳转。 否
     */
    private String page;
    /**
     * 表单提交场景下，为 submit 事件带上的 formId；支付场景下，为本次支付的 prepay_id
     */
    private String form_id;
    /**
     * 公众号appid，要求与小程序有绑定且同主体
     */
    private String appid;
    /**
     * 模板需要放大的关键词，不填则默认无放大
     */
    private String emphasis_keyword;
    /**
     * 跳转小程序类型：developer为开发版；trial为体验版；formal为正式版；默认为正式版  否
     */
    private String miniprogram_state;
    /**
     * 进入小程序查看”的语言类型，支持zh_CN(简体中文)、en_US(英文)、zh_HK(繁体中文)、zh_TW(繁体中文)，默认为zh_CN
     * 返回值  否
     */
    private String lang;
    /**
     * 模板内容，不填则下发空模板
     */
//    private MessageData data;



}
