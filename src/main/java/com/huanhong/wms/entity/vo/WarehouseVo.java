package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "WarehouseVo查询对象", description = "库房查询对象封装")
@Data
public class WarehouseVo {

    @ApiModelProperty(value = "所属公司ID")
    private Integer companyId;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "库房名称")
    private String warehouseName;

}
