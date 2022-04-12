package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "更新移库记录DTO")
public class UpdateMovingInventoryRecordsDTO {

    @NotNull
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "原货位编号")
    private String preCargoSpaceId;

    @ApiModelProperty(value = "新货位编号")
    private String newCargoSpaceId;

}
