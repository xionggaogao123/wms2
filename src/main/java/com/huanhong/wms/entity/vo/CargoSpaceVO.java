package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "CargoSpaceVO查询对象", description = "货位查询对象封装")
public class CargoSpaceVO {

    @ApiModelProperty(value = "货架编号")
    private String shelfId;

    @ApiModelProperty(value = "货位编号")
    private String cargoSpaceId;

}
