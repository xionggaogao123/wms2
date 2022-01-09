package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "更新货架模板")
public class UpdateShelfTemplateDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "模板ID", required = true)
    private Integer id;

    @ApiModelProperty(value = "货架模板名称")
    private String shelfTemplateName;

    @Min(0)
    @ApiModelProperty(value = "货架类型- 0-货架、1-地堆")
    private Integer shelfType;

    @Min(0)
    @ApiModelProperty(value = "货架承重(kg ：千克  地堆：0)")
    private Double shelfLoadBearing;

    @Min(0)
    @ApiModelProperty(value = "底长(m：米)")
    private Double shelfBottomLength;

    @Min(0)
    @ApiModelProperty(value = "底宽(m：米)")
    private Double shelfBottomWidth;

    @Min(0)
    @ApiModelProperty(value = "高(m：米  地堆 0)")
    private Double shelfHeight;

    @Min(1)
    @Max(9)
    @ApiModelProperty(value = "每层单元格数")
    private Integer cellNumber;

    @Min(1)
    @Max(9)
    @ApiModelProperty(value = "货架层数-地堆即为一层")
    private Integer shelfLayer;

    @ApiModelProperty(value = "备注")
    private String remark;

}
