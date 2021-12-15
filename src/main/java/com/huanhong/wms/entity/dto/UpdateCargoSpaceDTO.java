package com.huanhong.wms.entity.dto;


import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "更新货位")
public class UpdateCargoSpaceDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "货位编号")
    private String cargoSpaceId;

    @ApiModelProperty(value = "货位所在层")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String cargoSpaceFloor;

    @ApiModelProperty(value = "货位类型-0-货架、1-室外地堆、2-室内地堆")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String cargoSpaceType;

    @ApiModelProperty(value = "底长(m：米)")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private Double cargoSpaceLength;

    @ApiModelProperty(value = "底宽(m：米)")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private Double cargoSpaceWidth;

    @ApiModelProperty(value = "高（m: 米 地堆无限高）")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private Double cargoSpaceHeight;

    @ApiModelProperty(value = "货位承重(地堆无限大)")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String cargoSpaceBearing;

    @ApiModelProperty(value = "备注")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String remark;

}
