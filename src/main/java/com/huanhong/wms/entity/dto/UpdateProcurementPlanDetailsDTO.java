package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("更新采购计划明细")
public class UpdateProcurementPlanDetailsDTO {

    @NotNull
    @ApiModelProperty(value = "id")
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
    private LocalDateTime requestArrivalTime;


    @ApiModelProperty(value = "使用地点")
    private String usePlace;

    @ApiModelProperty(value = "使用用途")
    private String usePurpose;

    @Min(0)
    @ApiModelProperty(value = "库存量")
    private Double inventory;

    @Min(0)
    @ApiModelProperty(value = "安全库存")
    private Double safetyStock;

    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "物料ID")
    private String materialId;
    @ApiModelProperty(value = "物料名称")
    private String materialName;
}
