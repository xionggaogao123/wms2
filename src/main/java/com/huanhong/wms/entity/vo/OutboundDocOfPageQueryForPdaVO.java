package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "领料及调拨出库查询对象")
public class OutboundDocOfPageQueryForPdaVO {

    @ApiModelProperty(value = "单据编号")
    private String documentNumber;

    @ApiModelProperty(value = "库房ID")
    private String warehouseId;

    @ApiModelProperty(value = "领用单位")
    private String requisitioningUnit;

    @ApiModelProperty(value = "领用人")
    private String recipient;

}
