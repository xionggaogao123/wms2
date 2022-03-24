package com.huanhong.wms.entity;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="消息表")
public class Message extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "消息类型 0抄送 1转发")
    private Integer type;

    @ApiModelProperty(value = "消息名称")
    private String name;

    @ApiModelProperty(value = "计划类型 1、出库 2、入库 3、调拨 4、采购计划 5、需求计划 6、到货检验")
    private Integer objectType;

    @ApiModelProperty(value = "计划id")
    private Integer objectId;

    @ApiModelProperty(value = "计划类别-正常、加急、补计划、请选择（默认）")
    private Boolean planClassification;

    @ApiModelProperty(value = "编号")
    private String documentNumber;

    @ApiModelProperty(value = "流程Id")
    private String processInstanceId;

    @ApiModelProperty(value = "操作人ID")
    private Integer handleUserId;

    @ApiModelProperty(value = "操作人名称")
    private String handleUserName;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "读取状态:0-未读，1-已读")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;


}
