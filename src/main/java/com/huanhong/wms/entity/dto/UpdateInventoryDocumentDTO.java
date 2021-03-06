package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("清点单更新DTO")
public class UpdateInventoryDocumentDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "入库单ID", required = true)
    private Integer id;

    @ApiModelProperty(value = "送货单编号")
    private String deliveryNoteNumber;

    @ApiModelProperty(value = "询价单编号")
    private String rfqNumber;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "清点 0-未清点(默认) 1-已清点")
    private  Integer complete;


    @ApiModelProperty(value = "备注")
    private String remark;
}
