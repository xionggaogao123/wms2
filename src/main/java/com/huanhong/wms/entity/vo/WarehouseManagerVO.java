package com.huanhong.wms.entity.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "仓库管理员关联")
@Data
public class WarehouseManagerVO {

    @ApiModelProperty(value = "登录账号")
    private String loginName;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

}
