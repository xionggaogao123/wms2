package com.huanhong.wms.entity.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel(value = "MeterialVO查询对象", description = "物料查询对象封装")
@Data
public class MeterialVO {

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "俗称")
    private String slang;

    @ApiModelProperty(value = "分类编码")
    private String typeCode;

    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;

    @ApiModelProperty(value = "辅助单位")
    private String auxiliaryUnit;

    @ApiModelProperty(value = "品牌")
    private String brand;

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

    @ApiModelProperty(value = "停用")
    private Integer stopUsing;
}