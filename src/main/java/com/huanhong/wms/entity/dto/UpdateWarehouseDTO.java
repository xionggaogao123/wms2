package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;


@ApiModel(value = "仓库更新对象", description = "仓库更新对象封装")
@Data
public class UpdateWarehouseDTO {
    private static final long serialVersionUID = 1L;

    @NotEmpty
    @ApiModelProperty(value = "仓库编号")
    private String warehouseId;


    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;


    @ApiModelProperty(value = "仓库面积")
    private String warehouseAcreage;


    @ApiModelProperty(value = "仓库层数")
    private String warehouseLayers;


    @ApiModelProperty(value = "仓库地址")
    private String warehouseAdress;


    @ApiModelProperty(value = "仓库负责人")
    private String warehousePrincipal;


    @ApiModelProperty(value = "仓库联系电话")
    private String warehouseContactNumber;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "停用")
    private Integer stopUsing;


    @ApiModelProperty(value = "备注")
    private String remark;
}
