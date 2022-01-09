package com.huanhong.wms.entity.dto;


import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@ApiModel(value = "UpdateShelfVO更新对象", description = "货架更新对象封装")
@Data
public class UpdateShelfDTO {

    private static final long serialVersionUID = 1L;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "货架编号")
    private String shelfId;

    @Min(0)
    @Max(1)
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "货架类型- 0-货架、1-地堆")
    private Integer shelfType;

    @Min(0)
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "货架承重(kg:千克 地堆0)")
    private Double shelfLoadBearing;

    @Min(0)
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "底长(m：米)")
    private Double shelfBottomLength;

    @Min(0)
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "底宽(m：米)")
    private Double shelfBottomWidth;

    @Min(0)
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "高(m：米 地堆0)")
    private Double shelfHeight;

    @Min(0)
    @Max(9)
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "货架层数-地堆即为一层")
    private Integer shelfLayer;

    @Min(0)
    @Max(9)
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "每层单元格数")
    private Integer cellNumber;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "备注")
    private String remark;
}
