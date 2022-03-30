package com.huanhong.common.units.weixin;

import lombok.Data;

@Data
public class WorkOrderTemplateMessage extends TemplateMessage {
    /**
     * 模版id
     */
    private String template_id = "sn0scbOPjZE87AwGd0u0f0xBBSk478GZOhkEYwb2F-c";

    /**
     * 模板内容，不填则下发空模板
     */
    private WorkOrderTemplateMessageData data;
}
