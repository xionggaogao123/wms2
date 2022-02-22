package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@Data
@Accessors(chain = true)
@ApiModel(description = "部门DTO")
public class DeptDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "部门ID")
    private Integer id;

    @ApiModelProperty(value = "部门代码")
    private Integer code;

    @ApiModelProperty(value = "部门名称", required = true)
    private String name;

    @ApiModelProperty(value = "等级", required = true)
    private Integer level;

    @ApiModelProperty(value = "父Id", required = true)
    private Integer parentId;

    @ApiModelProperty(value = "父Code")
    private Integer parentCode;

    @ApiModelProperty(value = "顺序 从小到大")
    private Integer sort;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "状态 0.禁用  1.启用")
    private Integer state;


}
