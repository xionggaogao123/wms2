package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "添加仓库管理员")
public class AddWarehouseManagerDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户Id")
    private Integer userId;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;



}
