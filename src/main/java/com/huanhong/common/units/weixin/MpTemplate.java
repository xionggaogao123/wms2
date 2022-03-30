package com.huanhong.common.units.weixin;

import lombok.Data;

@Data
public class MpTemplate {
    /**
     * 接收者（用户）的 openid
     */
    private String touser;

    private MpTemplateMsg mp_template_msg;
}
