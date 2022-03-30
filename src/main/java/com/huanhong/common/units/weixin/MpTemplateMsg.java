package com.huanhong.common.units.weixin;

import lombok.Data;

@Data
public class MpTemplateMsg {
    /**
     * 模版id
     */
    private String template_id = "QDpXD23Ns_87Lxkz16MDLK3_308FbqQe59CqxGswq4M";

    /**
     * 公众号appid，要求与小程序有绑定且同主体
     */
    private String appid = WeixinConstant.APP_ID;
    /**
     * 公众号模板消息所要跳转的url
     */
    private String url;
    /**
     * 公众号模板消息所要跳转的小程序，小程序的必须与公众号具有绑定关系
     */
    private Miniprogram miniprogram;
    /**
     * 公众号模板消息的数据
     */
    private MessageData data;
}
