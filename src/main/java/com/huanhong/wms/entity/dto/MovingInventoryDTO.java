package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiOperation("库存移动DTO")
public class MovingInventoryDTO {

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
    @ApiModelProperty(value = "原货位编码")
    private String preCargoSpaceId;


    @NotBlank
    @ApiModelProperty(value = "新货位编码")
    private String hindCargoSpaceId;


    @Min(0)
    @NotNull
    @ApiModelProperty(value = "可移数量-此货位此批次的物料库存量")
    private Double preInventoryCredit;


    @Min(0)
    @NotNull
    @ApiModelProperty(value = "移动数量-本次需要移动的数量")
    private Double hindInventoryCredit;


}
