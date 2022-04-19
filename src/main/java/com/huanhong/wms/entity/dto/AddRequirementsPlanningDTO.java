package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(value = "新增需求计划DTO")
public class AddRequirementsPlanningDTO {

    private static final long serialVersionUID=1L;

    @NotEmpty
    @ApiModelProperty(value = "计划部门")
    private String planUnit;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @NotEmpty
    @ApiModelProperty(value = "申请人")
    private String applicant;

    @Min(1)
    @Max(3)
    @NotNull
    @ApiModelProperty(value = "计划类别-1-正常、2-加急、3-补计划、请选择（默认）")
    private Integer planClassification;

    @Min(0)
    @ApiModelProperty(value = "预估总金额")
    private BigDecimal estimatedTotalAmount;

    @Min(1)
    @Max(4)
    @NotNull
    @ApiModelProperty(value = "状态: 1-草拟、2-审批中、3-审批生效、4-作废")
    private Integer  planStatus;

    @NotEmpty
    @ApiModelProperty(value = "物料用途")
    private String materialUse;

}
