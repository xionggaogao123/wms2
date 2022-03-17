package com.huanhong.wms.entity;

import java.time.LocalDateTime;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="流程任务表")
public class ProcessAssignment extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "任务名称")
    private String name;

    @ApiModelProperty(value = "计划类型")
    private Boolean objectType;

    @ApiModelProperty(value = "计划id")
    private Integer objectId;

    @ApiModelProperty(value = "计划类别-正常、加急、补计划、请选择（默认）")
    private Integer planClassification;

    @ApiModelProperty(value = "编号")
    private String documentNumber;

    @ApiModelProperty(value = "流程Id")
    private String processInstanceId;

    @ApiModelProperty(value = "流程key")
    private String processDefinitionKey;

    @ApiModelProperty(value = "任务名称")
    private String processName;

    @ApiModelProperty(value = "任务key")
    private String taskDefinitionKey;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "任务审批状态:0-未处理，1-审批通过，2-驳回")
    private Boolean status;

    @ApiModelProperty(value = "任务办理人账号")
    private String userAccount;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "备注")
    private String remark;


}
