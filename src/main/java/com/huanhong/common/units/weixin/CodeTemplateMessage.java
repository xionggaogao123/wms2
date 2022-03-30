package com.huanhong.common.units.weixin;

import lombok.Data;

@Data
public class CodeTemplateMessage extends TemplateMessage{
    /**
     * 模版id
     */
    private String template_id = "ChuzLGQX8U6BOU_d3Vip0xKkx1tIzkAMCwsMguknBW8";

    /**
     * 模板内容，不填则下发空模板
     */
    private CodeTemplateMessageData data;
}
