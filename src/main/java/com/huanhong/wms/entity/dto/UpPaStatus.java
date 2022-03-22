package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(description="更新任务审批状态参数")
public class UpPaStatus {


    @ApiModelProperty(value = "意见")
    private String reason;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "任务审批状态:1-审批通过，2-驳回")
    private Integer status;

}
