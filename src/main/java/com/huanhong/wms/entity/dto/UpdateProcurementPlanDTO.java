package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("更新采购计划")
public class UpdateProcurementPlanDTO {

    private static final long serialVersionUID=1L;

    @NotNull
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "流程id")
    private String processInstanceId;

    @ApiModelProperty(value = "物料用途")
    private String materialUse;

    @Min(1)
    @Max(3)
    @ApiModelProperty(value = "计划类别-1.正常、2.加急、3补计划、请选择（默认）")
    private Integer planClassification;

    @Min(1)
    @Max(4)
    @ApiModelProperty(value = "状态:1.草拟 2.审批中 3.审批生效 4.作废")
    private Integer status;

    @ApiModelProperty(value = "计划部门")
    private String planningDepartment;

    @ApiModelProperty(value = "计划员")
    private String planner;

    @ApiModelProperty(value = "需求部门")
    private String demandDepartment;

    @ApiModelProperty(value = "备注")
    private String remark;
}
