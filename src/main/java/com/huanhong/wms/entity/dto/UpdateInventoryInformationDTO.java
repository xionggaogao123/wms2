package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "更新库存")
public class UpdateInventoryInformationDTO {

    private static final long serialVersionUID = 1L;


    @NotNull
    @ApiModelProperty(value = "库存ID", required = true)
    private Integer id;


//    @NotBlank
//    @ApiModelProperty(value = "物料编码")
//    private String materialCoding;
//
//
//    @NotBlank
//    @ApiModelProperty(value = "批次")
//    private String batch;


    @NotBlank
    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;


    @Min(0)
    @ApiModelProperty(value = "库存数量")
    private Double inventoryCredit;


    @Min(0)
    @ApiModelProperty(value = "安全数量")
    private Double safeQuantity;


    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;


    @ApiModelProperty(value = "有效日期")
    private LocalDateTime effectiveDate;


    @Min(0)
    @ApiModelProperty(value = "单价(泰丰盛和)")
    private BigDecimal unitPrice;


    @Min(0)
    @ApiModelProperty(value = "管理费率(默认1.1)")
    private Double managementFeeRate;


    @Min(0)
    @ApiModelProperty(value = "单价(使用单位)")
    private BigDecimal salesUnitPrice;


    @ApiModelProperty(value = "供应商")
    private String supplier;


    @ApiModelProperty(value = "优先存放位置")
    private String priorityStorageLocation;


    @ApiModelProperty(value = "备注")
    private String remark;

}
