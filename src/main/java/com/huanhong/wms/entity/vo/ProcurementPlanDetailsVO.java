package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "采购计划明细单查询")
public class ProcurementPlanDetailsVO {

    @ApiModelProperty(value = "采购计划单据编号")
    private String planNumber;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;
}
