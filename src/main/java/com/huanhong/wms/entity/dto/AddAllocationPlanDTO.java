package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel(description="调拨计划主表新增DTO")
public class AddAllocationPlanDTO {

    private static final long serialVersionUID = 1L;

    @Min(1)
    @Max(2)
    @ApiModelProperty(value = "业务类型：1 计划调拨 2 预备调拨")
    private Integer businessType;

    @Min(1)
    @Max(4)
    @ApiModelProperty(value = "计划状态-状态: 1草拟 2审批中 3审批生效 4作废 5.驳回")
    private Integer planStatus;

    @NotNull
    @ApiModelProperty(value = "调拨日期")
    private LocalDateTime assignmentDate;

    @NotBlank
    @ApiModelProperty(value = "调出仓库")
    private String sendWarehouse;

    @ApiModelProperty(value = "调出负责人")
    private String sendUser;

    @NotBlank
    @ApiModelProperty(value = "调入仓库")
    private String receiveWarehouse;

    @ApiModelProperty(value = "调入负责人")
    private String receiveUser;

    @NotBlank
    @ApiModelProperty(value = "申请人")
    private String applicant;

    @ApiModelProperty(value = "备注")
    private String remark;
}
