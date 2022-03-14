package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("新建出库流水")
public class AddOutboundRecordDTO {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    @ApiModelProperty(value = "原单据编号")
    private String documentNumber;

    @NotEmpty
    @ApiModelProperty(value = "库房ID")
    private String warehouseId;

    @NotEmpty
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @NotNull
    @ApiModelProperty(value = "出货数量")
    private Double outQuantity;

    @NotEmpty
    @ApiModelProperty(value = "详细信息（json 货位 批次 数量）")
    private String details;

    @NotNull
    @ApiModelProperty(value = "状态：0-审批中（锁库存）1-审批生效（出库）")
    private Integer status;
}
