package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("新增采购计划明细")
public class AddProcurementPlanDetailsDTO {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "采购计划单据编号")
    private String planNumber;

    @NotBlank
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @NotNull
    @Min(0)
    @ApiModelProperty(value = "需求数量")
    private Double requiredQuantity;

    @NotNull
    @Min(0)
    @ApiModelProperty(value = "计划采购数量")
    private Double plannedPurchaseQuantity;


    @Min(0)
    @ApiModelProperty(value = "批准数量")
    private Double approvedQuantity;

    @NotNull
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

    @NotBlank
    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @Min(0)
    @ApiModelProperty(value = "库存量")
    private Double inventory;

    @Min(0)
    @ApiModelProperty(value = "安全库存")
    private Double safetyStock;

    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "物料ID")
    private Integer materialId;
    @ApiModelProperty(value = "物料名称")
    private String materialName;
}
