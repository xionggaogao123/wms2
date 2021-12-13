package com.huanhong.wms.entity.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "WarehouseAreaVO查询对象", description = "库区查询对象封装")
@Data
public class WarehouseAreaVO {

    @ApiModelProperty(value = "子库编号")
    private String subLibraryId;

    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;

    @ApiModelProperty(value = "库区名称")
    private String warehouseAreaName;
}
