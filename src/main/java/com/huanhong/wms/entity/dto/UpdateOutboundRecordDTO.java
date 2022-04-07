package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "货位编号")
    private String cargoSpaceId;

    @ApiModelProperty(value = "批次")
    private String batch;

    @Min(0)
    @ApiModelProperty(value = "出货数量")
    private Double outQuantity;

    @ApiModelProperty(value = "详细信息（json 货位 批次 数量）")
    private String details;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "状态：0-审批中（锁库存）1-审批生效（出库）")
    private Integer status;
}
