package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("更新出库流水")
public class UpdateOutboundRecordDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "入库单ID", required = true)
    private Integer id;

    @ApiModelProperty(value = "出库类型：1-领料出库 2-调拨出库")
    private Integer outType;

    @ApiModelProperty(value = "库房ID")
    private String warehouseId;

    @NotBlank
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @NotBlank
    @ApiModelProperty(value = "货位编号")
    private String cargoSpaceId;

    @NotBlank
    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "单价(使用单位)")
    private BigDecimal salesUnitPrice;

    @Min(0)
    @ApiModelProperty(value = "出货数量")
    private Double outQuantity;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "状态：0-审批中（锁库存）1-审批生效（出库）")
    private Integer status;

    @Min(0)
    @Max(5)
    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;
}
