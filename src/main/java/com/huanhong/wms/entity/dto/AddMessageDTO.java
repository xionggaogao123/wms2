package com.huanhong.wms.entity.dto;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@ApiModel(description="新增消息参数")
public class AddMessageDTO{

    @ApiModelProperty(value = "消息类型 0抄送 1转发")
    private Integer type;

    @ApiModelProperty(value = "消息名称")
    private String name;

    @ApiModelProperty(value = "计划类型 1、出库 2、入库 3、调拨 4、采购计划 5、需求计划 6、到货检验")
    private Integer objectType;

    @ApiModelProperty(value = "计划id")
    private Integer objectId;

    @ApiModelProperty(value = "操作人ID",hidden = true)
    private Integer handleUserId;

    @ApiModelProperty(value = "操作人名称",hidden = true)
    private String handleUserName;

    @ApiModelProperty(value = "用户id")
    private Integer userId;



}
