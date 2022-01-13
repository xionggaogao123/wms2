package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Data
@ApiModel(description = "更新子库")
public class UpdateSubliraryDTO {
    private static final long serialVersionUID = 1L;

    @NotEmpty
    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

    @ApiModelProperty(value = "子库名称")
    private String sublibraryName;

    @ApiModelProperty(value = "子库面积")
    private String sublibraryAcreage;

    @ApiModelProperty(value = "子库所在层")
    private String sublibraryFloor;

    @ApiModelProperty(value = "子库负责人")
    private String sublibraryPrincipal;

    @Min(0)
    @ApiModelProperty(value = "长(m：米)")
    private Double sublibraryLength;

    @Min(0)
    @ApiModelProperty(value = "宽(m：米)")
    private Double sublibraryWidth;

    @Min(0)
    @ApiModelProperty(value = "高(m：米)")
    private Double sublibraryHeight;

    @ApiModelProperty(value = "子库联系电话")
    private String sublibraryContactNumber;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "停用")
    private Integer stopUsing;

    @ApiModelProperty(value = "备注")
    private String remark;

}
