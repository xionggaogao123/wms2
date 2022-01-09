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

    @ApiModelProperty(value = "货位编号")
    private String cargoSpaceId;

    @Min(0)
    @Max(9)
    @NotNull
    @ApiModelProperty(value = "货位所在层")
    private Integer cargoSpaceFloor;

    @NotNull
    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "货位类型-0-货架、1-地堆")
    private Integer cargoSpaceType;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "底长(m：米)")
    private Double cargoSpaceLength;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "底宽(m：米)")
    private Double cargoSpaceWidth;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "高（m: 米 地堆无限高）")
    private Double cargoSpaceHeight;

//    @Min(0)
//    @NotNull
//    @ApiModelProperty(value = "货位承重(地堆无限大)--现阶段隐藏不显示")
//    private String cargoSpaceBearing;


    @ApiModelProperty(value = "备注")
    private String remark;
}
