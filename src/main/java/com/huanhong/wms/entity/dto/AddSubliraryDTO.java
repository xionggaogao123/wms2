package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "新增子库")
public class AddSubliraryDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "库房编号")
    @NotEmpty
    private String warehouseId;

    @ApiModelProperty(value = "子库编号")
    @NotEmpty
    private String sublibraryId;

    @ApiModelProperty(value = "子库名称")
    @NotEmpty
    private String sublibraryName;

    @ApiModelProperty(value = "子库面积")
    @NotEmpty
    private String sublibraryAcreage;

    @ApiModelProperty(value = "子库所在层")
    @NotEmpty
    private String sublibraryFloor;

    @ApiModelProperty(value = "子库负责人")
    @NotEmpty
    private String sublibraryPrincipal;

    @Min(0)
    @ApiModelProperty(value = "长(m：米)")
    @NotNull
    private Double sublibraryLength;

    @Min(0)
    @ApiModelProperty(value = "宽(m：米)")
    @NotNull
    private Double sublibraryWidth;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "高(m：米)")
    private Double sublibraryHeight;

    @NotEmpty
    @ApiModelProperty(value = "子库联系电话")
    private String sublibraryContactNumber;

    @ApiModelProperty(value = "备注")
    private String remark;
}
