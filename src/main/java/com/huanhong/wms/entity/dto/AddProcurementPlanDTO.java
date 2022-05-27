package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("新建采购计划")
public class AddProcurementPlanDTO {

    private static final long serialVersionUID=1L;

    @NotBlank
    @ApiModelProperty(value = "需求计划单编号")
    private String originalDocumentNumber;

    @NotBlank
    @ApiModelProperty(value = "物料用途")
    private String materialUse;

    @NotNull
    @Min(1)
    @Max(3)
    @ApiModelProperty(value = "计划类别-1正常、2加急、3补计划、请选择（默认）")
    private Integer planClassification;

    @NotNull
    @Min(1)
    @Max(4)
    @ApiModelProperty(value = "状态:1.草拟 2.审批中 3.审批生效 4.作废 5.驳回")
    private Integer status;

    @NotBlank
    @ApiModelProperty(value = "计划部门")
    private String planningDepartment;

    @NotBlank
    @ApiModelProperty(value = "计划员")
    private String planner;

    @NotBlank
    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @NotBlank
    @ApiModelProperty(value = "需求部门")
    private String demandDepartment;

    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "平衡利库明细 id")
    private Integer balanceLibraryDetailId;
    @ApiModelProperty(value = "平衡利库单号")
    private String balanceLibraryNo;
}
