package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(description = "更新入库记录DTO")
public class UpdateWarehousingRecordDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "入库记录ID", required = true)
    private Integer id;

    @ApiModelProperty(value = "库房ID")
    private String warehouseId;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "入库数量")
    private Double enterQuantity;

    @Min(0)
    @ApiModelProperty(value = "单价(泰丰盛和)")
    private BigDecimal unitPrice;

    @Min(0)
    @ApiModelProperty(value = "单价(使用单位)")
    private BigDecimal salesUnitPrice;

    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;

    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "库管员（提交入库申请表单的用户）")
    private String warehouseManager;

}
