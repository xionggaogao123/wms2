package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "新增库区")
public class AddWarehouseAreaDTO {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

    @NotEmpty
    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;

    @NotEmpty
    @ApiModelProperty(value = "库区名称")
    private String warehouseAreaName;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "长(m：米)")
    private Double warehouseAreaLength;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "宽(m：米)")
    private Double warehouseAreaWidth;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "高(m：米)")
    private Double warehouseAreaHeight;

    @NotEmpty
    @ApiModelProperty(value = "库区负责人")
    private String warehouseAreaPrincipal;

    @NotEmpty
    @ApiModelProperty(value = "库区联系电话")
    private String warehouseAreaContactNumber;

    @ApiModelProperty(value = "备注")
    private String remark;
}
