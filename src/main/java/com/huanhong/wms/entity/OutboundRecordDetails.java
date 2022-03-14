package com.huanhong.wms.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用于处理出库记录中的详情字段")
public class OutboundRecordDetails {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("货位id")
    private String cargoSpaceId;

    @ApiModelProperty("批次")
    private String  batch;

    @ApiModelProperty("数量")
    private Double inventoryCredit;
}
