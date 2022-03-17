package com.huanhong.wms.entity;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="采购计划主表")
public class ProcurementPlan extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "流程id")
    private String processInstanceId;

    @ApiModelProperty(value = "采购计划单据编号")
    private String planNumber;

    @ApiModelProperty(value = "物料用途")
    private String materialUse;

    @ApiModelProperty(value = "计划类别")
    private String planCategory;

    @ApiModelProperty(value = "计划时间")
    private String planningTime;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "计划部门")
    private String planningDepartment;

    @ApiModelProperty(value = "计划员")
    private String planner;

    @ApiModelProperty(value = "需求部门")
    private String demandDepartment;

    @ApiModelProperty(value = "原单据编号")
    private String originalDocumentNumber;

    @ApiModelProperty(value = "备注")
    private String remark;


}
