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
@ApiModel(description="货位管理")
public class CargoSpaceManagement extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "货架编号")
    private String shelfId;

    @ApiModelProperty(value = "货位编号")
    private String cargoSpaceId;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "货位所在层")
    private Integer cargoSpaceFloor;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "货位类型-0-货架、1-地堆")
    private Integer cargoSpaceType;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "底长(m：米)")
    private Double cargoSpaceLength;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "底宽(m：米)")
    private Double cargoSpaceWidth;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "高（m: 米 地堆无限高）")
    private Double cargoSpaceHeight;

//    @ApiModelProperty(value = "货位承重(地堆0)")
//    private Integer cargoSpaceBearing;

    @TableField(value = "create_time" ,fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "停用")
    private Integer stopUsing;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "存满 0 - 空   1 - 未满  2 - 已满")
    private Integer full;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "备注")
    private String remark;

}
