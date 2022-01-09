package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "新增物料")
public class AddMaterialDTO {

    private static final long serialVersionUID = 1L;


    @NotNull
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "俗称")
    private String slang;

    @NotNull
    @ApiModelProperty(value = "分类编码")
    private String typeCode;

    @NotNull
    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @NotNull
    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;


    @ApiModelProperty(value = "辅助单位")
    private String auxiliaryUnit;

    @NotNull
    @ApiModelProperty(value = "材质")
    private String material;

    @NotNull
    @ApiModelProperty(value = "执行标准")
    private String executiveStandard;


    @ApiModelProperty(value = "技术要求")
    private String skillsRequiremen;


    @ApiModelProperty(value = "图号")
    private String drawingNumber;


    @ApiModelProperty(value = "安全质量标准")
    private String safetyQualityStandards;


    @ApiModelProperty(value = "生产厂家")
    private String supplier;


    @Min(0)
    @ApiModelProperty(value = "物料单位长度-米/M")
    private Double materialLength;

    @Min(0)
    @ApiModelProperty(value = "物料单位宽-米/M")
    private Double  materialWidth;

    @Min(0)
    @ApiModelProperty(value = "物料单位高度-米/M")
    private Double  materialHeight;

    @Min(0)
    @ApiModelProperty(value = "物料单位重量-kg/千克")
    private Double  materialWeight;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "备注")
    private String remark;

}
