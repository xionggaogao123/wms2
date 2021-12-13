package com.huanhong.wms.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="子库管理")
public class SublibraryManagement extends SuperEntity {

    private static final long serialVersionUID=1L;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "子库名称")
    private String sublibraryName;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "子库面积")
    private String sublibraryAcreage;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "子库所在层")
    private String sublibraryFloor;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "子库负责人")
    private String sublibraryPrincipal;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "长(m：米)")
    private Double sublibraryLength;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "宽(m：米)")
    private Double sublibraryWidth;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "高(m：米)")
    private Double sublibraryHeight;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "子库联系电话")
    private String sublibraryContactNumber;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "备注")
    private String remark;


}
