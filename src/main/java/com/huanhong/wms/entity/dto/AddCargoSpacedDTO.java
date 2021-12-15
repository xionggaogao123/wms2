package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "新增货位")
public class AddCargoSpacedDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "货架编号")
    private String shelfId;

    @NotNull
    @ApiModelProperty(value = "货位编号")
    private String cargoSpaceId;

    @NotNull
    @ApiModelProperty(value = "货位所在层")
    private String cargoSpaceFloor;

    @NotNull
    @Min(0)
    @Max(2)
    @ApiModelProperty(value = "货位类型-0-货架、1-室外地堆、2-室内地堆")
    private String cargoSpaceType;

    @NotNull
    @ApiModelProperty(value = "底长(m：米)")
    private Double cargoSpaceLength;

    @NotNull
    @ApiModelProperty(value = "底宽(m：米)")
    private Double cargoSpaceWidth;

    @NotNull
    @ApiModelProperty(value = "高（m: 米 地堆无限高）")
    private Double cargoSpaceHeight;

    @NotNull
    @ApiModelProperty(value = "货位承重(地堆无限大)")
    private String cargoSpaceBearing;

    @ApiModelProperty(value = "备注")
    private String remark;
}
