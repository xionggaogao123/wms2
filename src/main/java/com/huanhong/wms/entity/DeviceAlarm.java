package com.huanhong.wms.entity;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="设备报警表")
public class DeviceAlarm extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "设备名")
    private String deviceName;

    @ApiModelProperty(value = "设备号")
    private String deviceNo;

    @ApiModelProperty(value = "消息类型")
    private String msgType;

    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

    @ApiModelProperty(value = "hik 消息 id")
    private String messageId;

    @ApiModelProperty(value = "事件描述")
    private String eventDescription;

    @ApiModelProperty(value = "仓库名")
    private String warehouseName;


}
