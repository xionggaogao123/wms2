package com.huanhong.wms.entity.param;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@ApiModel(description="任务表参数")
public class ProcessAssignmentParam{

    @ApiModelProperty(value = "任务名称")
    private String name;

    @ApiModelProperty(value = "计划类型 1、出库 2、入库 3、调拨 4、采购计划 5、需求计划 6、到货检验  ")
    private Integer objectType;

    @ApiModelProperty(value = "计划类别-正常、加急、补计划、请选择（默认）")
    private Integer planClassification;

    @ApiModelProperty(value = "编号")
    private String documentNumber;

    @ApiModelProperty(value = "流程Id",hidden = true)
    private String processInstanceId;

    @ApiModelProperty(value = "任务名称",hidden = true)
    private String processName;

    @ApiModelProperty(value = "任务审批状态:0-未处理，1-审批通过，2-驳回")
    private Integer status;

    @ApiModelProperty(value = "任务办理人账号",hidden = true)
    private String userAccount;

    @ApiModelProperty(value = "用户名称",hidden = true)
    private String userName;

    @ApiModelProperty(value = "开始时间",hidden = true)
    private Date startTime;


}
