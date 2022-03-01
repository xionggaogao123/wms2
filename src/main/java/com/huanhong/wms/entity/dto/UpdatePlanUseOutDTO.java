package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("更新领料出库单")
public class UpdatePlanUseOutDTO {

    private static final long serialVersionUID=1L;

    @NotNull
    @ApiModelProperty(value = "入库单ID", required = true)
    private Integer id;

    @Min(1)
    @Max(4)
    @ApiModelProperty(value = "状态状态:1.草拟,2.审批中,3.审批生效,4.作废")
    private Integer status;

    @Min(1)
    @Max(3)
    @ApiModelProperty(value = "计划类别-1.正常、2.加急、3.补计划、请选择（默认）")
    private Integer planClassification;

    @ApiModelProperty(value = "领用单位")
    private String requisitioningUnit;

    @ApiModelProperty(value = "库管员")
    private String librarian;

    @ApiModelProperty(value = "费用承担单位")
    private String costBearingUnit;

    @ApiModelProperty(value = "费用项目")
    private String expenseItem;

    @ApiModelProperty(value = "物资用途")
    private String materialUse;

    @ApiModelProperty(value = "领用用途")
    private String requisitionUse;

    @ApiModelProperty(value = "备注")
    private String remark;

}
