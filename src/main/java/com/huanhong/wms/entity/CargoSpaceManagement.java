package com.huanhong.wms.entity;

import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="货位管理")
public class CargoSpaceManagement extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "货架编号")
    private String shelfNumber;

    @ApiModelProperty(value = "货位编号")
    private String cargoSpaceNumber;

    @ApiModelProperty(value = "货位所在层")
    private String cargoSpaceFloor;

    @ApiModelProperty(value = "货位类型-0-货架、1-室外地堆、2-室内地堆")
    private String cargoSpaceType;

    @ApiModelProperty(value = "底长(m：米)")
    private Double cargoSpaceLength;

    @ApiModelProperty(value = "底宽(m：米)")
    private Double cargoSpaceWidth;

    @ApiModelProperty(value = "高（m: 米 地堆无限高）")
    private Double cargoSpaceHeight;

    @ApiModelProperty(value = "货位承重(地堆无限大)")
    private String cargoSpaceBearing;

    @ApiModelProperty(value = "备注")
    private String remark;


}
