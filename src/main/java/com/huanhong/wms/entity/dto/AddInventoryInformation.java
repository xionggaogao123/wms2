package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "新增库存")
public class AddInventoryInformation {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @NotEmpty
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @NotEmpty
    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;


    @ApiModelProperty(value = "辅助单位")
    private String auxiliaryUnit;


    @NotNull
    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;


    @NotNull
    @ApiModelProperty(value = "库存数量")
    private Double inventoryCredit;


    @NotNull
    @ApiModelProperty(value = "安全数量")
    private Double safeQuantity;


    @NotEmpty
    @ApiModelProperty(value = "批次")
    private String batch;


    @NotNull
    @ApiModelProperty(value = "货主")
    private String consignor;


    @NotEmpty
    @ApiModelProperty(value = "有效日期")
    private LocalDateTime effectiveDate;


    @NotNull
    @ApiModelProperty(value = "单价(泰丰盛和)")
    private BigDecimal unitPrice;


    @NotNull
    @ApiModelProperty(value = "管理费率(默认1.1)")
    private Double managementFeeRate;


    @NotNull
    @ApiModelProperty(value = "单价(使用单位)")
    private BigDecimal salesUnitPrice;


    @NotEmpty
    @ApiModelProperty(value = "供应商")
    private String supplier;


    @ApiModelProperty(value = "优先存放位置")
    private String priorityStorageLocation;


    @ApiModelProperty(value = "备注")
    private String remark;

}
