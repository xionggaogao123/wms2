package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "新增移库记录DTO")
public class AddMovingInventoryRecordsDTO {


    @NotBlank
    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @NotBlank
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @NotBlank
    @ApiModelProperty(value = "批次")
    private String batch;

    @NotBlank
    @ApiModelProperty(value = "原货位编号")
    private String preCargoSpaceId;

    @NotBlank
    @ApiModelProperty(value = "新货位编号")
    private String newCargoSpaceId;

    @ApiModelProperty(value = "原库存数量")
    private Double inventoryCredit;

    @ApiModelProperty(value = "移动数量")
    private Double moveQuantity;

}
