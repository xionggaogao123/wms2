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
@ApiModel(description="货架管理")
public class ShelfManagement extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;

    @ApiModelProperty(value = "货架编号")
    private String shelfId;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "货架类型- 0-货架、1-地堆")
    private Integer shelfType;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "货架承重(kg:千克 地堆0)")
    private Double shelfLoadBearing;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "底长(m：米)")
    private Double shelfBottomLength;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "底宽(m：米)")
    private Double shelfBottomWidth;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "高(m：米 地堆0)")
    private Double shelfHeight;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "每层单元格数")
    private Integer cellNumber;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "货架层数-地堆即为一层")
    private Integer shelfLayer;

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
