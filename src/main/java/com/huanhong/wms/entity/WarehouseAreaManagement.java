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
@ApiModel(description="库房区域管理")
public class WarehouseAreaManagement extends SuperEntity {

    private static final long serialVersionUID=1L;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库区名称")
    private String warehouseAreaName;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "长(m：米)")
    private Double warehouseAreaLength;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "宽(m：米)")
    private Double warehouseAreaWidth;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "高(m：米)")
    private Double warehouseAreaHeight;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库区负责人")
    private String warehouseAreaPrincipal;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库区联系电话")
    private String warehouseAreaContactNumber;

    @TableField(updateStrategy= FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "备注")
    private String remark;


}
