package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("新增盘点单DTO")
public class AddMakeInventoryDTO {

    private static final long serialVersionUID=1L;

    @NotEmpty
    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

    @NotEmpty
    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;

    @NotEmpty
    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @NotEmpty
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @NotEmpty
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @NotEmpty
    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @NotEmpty
    @ApiModelProperty(value = "批次")
    private String batch;

    @NotNull
    @Min(0)
    @ApiModelProperty(value = "库存数量")
    private Double inventoryCredit;

    @NotNull
    @Min(0)
    @ApiModelProperty(value = "实盘数量")
    private Double checkCredit;

    @NotEmpty
    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;

    @NotNull
    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "状态: 0-待盘点，1-已盘点")
    private Integer checkStatus;

    @NotEmpty
    @ApiModelProperty(value = "备注")
    private String remark;
}
