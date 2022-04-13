package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "更新仓库管理员")
public class UpdateWarehouseManagerDTO {

    private static final long serialVersionUID=1L;

    @NotNull
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

}
