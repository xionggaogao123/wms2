package com.huanhong.wms.entity;

import java.math.BigDecimal;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="需求计划表")
public class RequirementsPlanning extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "流程id")
    private String processInstanceId;

    @ApiModelProperty(value = "单据编号（需求计划）")
    private String planNumber;

    @ApiModelProperty(value = "计划部门")
    private String planUnit;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "申请人")
    private String applicant;

    @ApiModelProperty(value = "计划类别-1正常、2加急、3补计划、请选择（默认）")
    private Integer planClassification;

    @ApiModelProperty(value = "预估总金额")
    private BigDecimal estimatedTotalAmount;

    @ApiModelProperty(value = "状态: 1草拟、2审批中、3审批生效、4作废")
    private Integer planStatus;

    @ApiModelProperty(value = "物料用途")
    private String materialUse;

    @ApiModelProperty(value = "版本-乐观锁")
    private Integer version;


}
