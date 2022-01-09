package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "新增货架")
public class AddShelfDTO {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;

    @NotEmpty
    @ApiModelProperty(value = "货架编号")
    private String shelfId;

    @Min(0)
    @Max(1)
    @NotNull
    @ApiModelProperty(value = "货架类型- 0-货架、1-地堆")
    private Integer shelfType;

    @Min(0)
    @ApiModelProperty(value = "货架承重(kg:千克 地堆0)")
    private Double shelfLoadBearing;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "底长(m：米)")
    private Double shelfBottomLength;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "底宽(m：米)")
    private Double shelfBottomWidth;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "高(m：米 地堆0)")
    private Double shelfHeight;

    @Min(0)
    @Max(9)
    @NotNull
    @ApiModelProperty(value = "每层单元格数")
    private Integer cellNumber;

    @Min(0)
    @Max(9)
    @NotNull
    @ApiModelProperty(value = "货架层数-地堆即为一层")
    private Integer shelfLayer;

    @ApiModelProperty(value = "备注")
    private String remark;
}
