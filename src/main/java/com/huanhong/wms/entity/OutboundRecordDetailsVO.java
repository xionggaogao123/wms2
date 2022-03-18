package com.huanhong.wms.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("用户更新出库记录")
public class OutboundRecordDetailsVO {

    @NotNull
    @ApiModelProperty("id")
    private Integer id;

    @NotBlank
    @ApiModelProperty("货位id")
    private String cargoSpaceId;

    @NotBlank
    @ApiModelProperty("批次")
    private String batch;

    @NotNull
    @ApiModelProperty("数量")
    private Double inventoryCredit;

}
