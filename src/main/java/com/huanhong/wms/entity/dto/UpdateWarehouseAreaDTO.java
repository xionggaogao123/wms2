package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;


@Data
@ApiModel(description = "更新库区")
public class UpdateWarehouseAreaDTO {
    private static final long serialVersionUID = 1L;

    @NotEmpty
    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;

    @ApiModelProperty(value = "库区名称")
    private String warehouseAreaName;

    @Min(0)
    @ApiModelProperty(value = "长(m：米)")
    private Double warehouseAreaLength;

    @Min(0)
    @ApiModelProperty(value = "宽(m：米)")
    private Double warehouseAreaWidth;

    @Min(0)
    @ApiModelProperty(value = "高(m：米)")
    private Double warehouseAreaHeight;

    @ApiModelProperty(value = "库区负责人")
    private String warehouseAreaPrincipal;

    @ApiModelProperty(value = "库区联系电话")
    private String warehouseAreaContactNumber;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "停用")
    private Integer stopUsing;

    @ApiModelProperty(value = "备注")
    private String remark;
}
