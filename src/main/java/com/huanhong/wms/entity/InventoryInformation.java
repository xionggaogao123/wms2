package com.huanhong.wms.entity;

import java.math.BigDecimal;
import com.huanhong.wms.SuperEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="库存表")
public class InventoryInformation extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;

    @ApiModelProperty(value = "货位")
    private String cargoSpace;

    @ApiModelProperty(value = "库存数量")
    @TableField("Inventory_credit")
    private String inventoryCredit;

    @ApiModelProperty(value = "安全数量")
    private String safeQuantity;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "货主")
    private String consignor;

    @ApiModelProperty(value = "有效日期")
    private String effectiveDate;

    @ApiModelProperty(value = "单价(泰丰盛和)")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "管理费率(默认1.1)")
    private Float managementFeeRate;

    @ApiModelProperty(value = "单价(使用单位)")
    private BigDecimal salesUnitPrice;

    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "优先存放位置")
    private String priorityStorageLocation;

    @ApiModelProperty(value = "备注")
    private String remark;


}
