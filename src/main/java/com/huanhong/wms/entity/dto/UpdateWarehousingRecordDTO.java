package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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

}
