package com.huanhong.wms.entity.dto;


import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel(description = "更新子库")
public class UpdateSubliraryDTO {

    @NotEmpty
    @ApiModelProperty(value = "子库编号")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String sublibraryId;

    @ApiModelProperty(value = "子库名称")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String sublibraryName;

    @ApiModelProperty(value = "子库面积")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String sublibraryAcreage;

    @ApiModelProperty(value = "子库所在层")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String sublibraryFloor;

    @ApiModelProperty(value = "子库负责人")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String sublibraryPrincipal;

    @ApiModelProperty(value = "长(m：米)")
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private Double sublibraryLength;

    @ApiModelProperty(value = "宽(m：米)")
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private Double sublibraryWidth;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "高(m：米)")
    private Double sublibraryHeight;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "子库联系电话")
    private String sublibraryContactNumber;

    @ApiModelProperty(value = "备注")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String remark;

}
