package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(value = "更新需求计划DTO")
public class UpdateRequirementsPlanningDTO {

    private static final long serialVersionUID=1L;

    @NotNull
    @ApiModelProperty(value = "id", required = true)
    private Integer id;

    @ApiModelProperty(value = "流程Id")
    private String processInstanceId;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "计划部门")
    private String planUnit;

    @ApiModelProperty(value = "申请人")
    private String applicant;

    @Min(1)
    @Max(3)
    @ApiModelProperty(value = "计划类别-1-正常、2-加急、3-补计划、请选择（默认）")
    private Integer planClassification;

    @Min(0)
    @ApiModelProperty(value = "预估总金额")
    private BigDecimal estimatedTotalAmount;

    @Min(1)
    @Max(4)
    @ApiModelProperty(value = "状态: 1-草拟、2-审批中、3-审批生效、4-作废 5.驳回")
    private Integer  planStatus;


    @ApiModelProperty(value = "物料用途")
    private String materialUse;
}
