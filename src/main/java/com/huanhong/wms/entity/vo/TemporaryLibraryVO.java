package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "临库查询对象", description = "临库查询对象封装")
public class TemporaryLibraryVO {

    @ApiModelProperty(value = "临库入库单编号")
    private String documentNumber;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;
    
    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @ApiModelProperty(value = "批次")
    private String batch;
}
