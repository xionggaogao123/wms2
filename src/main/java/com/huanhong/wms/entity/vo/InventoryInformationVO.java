package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "库存查询对象", description = "库存查询对象封装")
public class InventoryInformationVO {

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "货主")
    private String consignor;

    @ApiModelProperty(value = "供应商")
    private String supplier;

}
