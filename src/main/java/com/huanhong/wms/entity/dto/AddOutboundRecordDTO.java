package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel("新建出库流水")
public class AddOutboundRecordDTO {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    @ApiModelProperty(value = "原单据编号")
    private String documentNumber;

    @ApiModelProperty(value = "出库类型：1-领料出库 2-调拨出库")
    private Integer outType;

    @NotEmpty
    @ApiModelProperty(value = "库房ID")
    private String warehouseId;

    @NotEmpty
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

    @NotNull
    @ApiModelProperty(value = "出货数量")
    private Double outQuantity;

    @NotNull
    @ApiModelProperty(value = "状态：0-审批中（锁库存）1-审批生效（出库）")
    private Integer status;
}
