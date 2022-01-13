package com.huanhong.wms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="子库管理")
public class SublibraryManagement extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "子库编号")
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

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "宽(m：米)")
    private Double sublibraryWidth;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "高(m：米)")
    private Double sublibraryHeight;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "子库联系电话")
    private String sublibraryContactNumber;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "停用")
    private Integer stopUsing;

    @TableField(value = "create_time" ,fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "备注")
    private String remark;

}
