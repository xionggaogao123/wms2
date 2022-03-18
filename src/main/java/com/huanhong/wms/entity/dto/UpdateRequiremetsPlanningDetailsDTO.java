package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(value = "更新需求计划明细DTO")
public class UpdateRequiremetsPlanningDetailsDTO {

    private static final long serialVersionUID=1L;

    @NotNull
    @ApiModelProperty(value = "id", required = true)
    private Integer id;


    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @Min(0)
    @ApiModelProperty(value = "需求数量")
    private Double requiredQuantity;

    @Min(0)
    @ApiModelProperty(value = "计划采购数量")
    private Double plannedPurchaseQuantity;

    @Min(0)
    @ApiModelProperty(value = "批准数量")
    private Double approvedQuantity;

    @Min(0)
    @ApiModelProperty(value = "预估单价")
    private BigDecimal estimatedUnitPrice;

    @Min(0)
    @ApiModelProperty(value = "预估金额")
    private BigDecimal estimatedAmount;

    @ApiModelProperty(value = "要求到货时间")
    private LocalDateTime arrivalTime;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "使用用途")
    private String usePurpose;

    @ApiModelProperty(value = "使用地点")
    private String usePlace;

    @ApiModelProperty(value = "备注")
    private String remark;

}
