package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@ApiModel(value = "Meterial更新对象", description = "Meterial更新对象封装")
@Data
public class UpdateMaterialDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @Length(max = 20, min = 1, message = "物料名称长度在1～20位之间")
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @Length(max = 20, min = 0, message = "俗称长度在0～20位之间")
    @ApiModelProperty(value = "俗称")
    private String slang;

    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;

    @ApiModelProperty(value = "辅助单位")
    private String auxiliaryUnit;

    @ApiModelProperty(value = "材质")
    private String material;

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

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "停用")
    private Integer stopUsing;
}
