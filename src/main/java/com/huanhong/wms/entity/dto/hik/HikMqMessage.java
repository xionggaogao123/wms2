package com.huanhong.wms.entity.dto.hik;

import lombok.Data;

@Data
public class HikMqMessage<T> {
    private String msgId;
    private String msgType;
    private T content;
    private String timestamp;
}
