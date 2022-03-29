package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(description="更新调拨计划明细DTO")
public class UpdateAllocationPlanDetailDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @Min(0)
    @ApiModelProperty(value = "请调数量")
    private Double requestQuantity;

    @Min(0)
    @ApiModelProperty(value = "准调数量")
    private Double calibrationQuantity;

    @ApiModelProperty(value = "批次")
    private String batch;

    @Min(0)
    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;

    @Min(0)
    @ApiModelProperty(value = "总金额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "备注")
    private String remark;

}
