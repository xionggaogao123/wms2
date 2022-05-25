package com.huanhong.wms.entity;

import com.baomidou.mybatisplus.annotation.Version;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="平衡利库明细")
public class BalanceLibraryDetail extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "平衡利库单号")
    private String balanceLibraryNo;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "需求数量")
    private Double requiredQuantity;

    @ApiModelProperty(value = "计划采购数量")
    private Double plannedPurchaseQuantity;

    @ApiModelProperty(value = "批准数量")
    private Double approvedQuantity;

    @ApiModelProperty(value = "预估单价")
    private BigDecimal estimatedUnitPrice;

    @ApiModelProperty(value = "预估金额")
    private BigDecimal estimatedAmount;

    @ApiModelProperty(value = "要求到货时间")
    private LocalDateTime requestArrivalTime;

    @ApiModelProperty(value = "使用地点")
    private String usePlace;

    @ApiModelProperty(value = "使用用途")
    private String usePurpose;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "库存量")
    private Double inventory;

    @ApiModelProperty(value = "安全库存")
    private Double safetyStock;

    @ApiModelProperty(value = "备注")
    private String remark;

    @Version
    @ApiModelProperty(value = "版本-乐观锁")
    private Integer version;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "物料id")
    private Integer materialId;


}
