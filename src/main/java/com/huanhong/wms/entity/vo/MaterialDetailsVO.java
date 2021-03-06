package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(value = "物料详情返回VO", description = "物料详情返回VO")
public class MaterialDetailsVO {

    @ApiModelProperty(value = "ID")
    private String id;

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

    @ApiModelProperty(value = "物料单位长度-米/M")
    private Double materialLength;

    @ApiModelProperty(value = "物料单位宽-米/M")
    private Double  materialWidth;

    @ApiModelProperty(value = "物料单位高度-米/M")
    private Double  materialHeight;

    @ApiModelProperty(value = "物料单位重量-kg/千克")
    private Double  materialWeight;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "最后更新时间")
    private LocalDateTime lastUpdate;

    @ApiModelProperty(value = "停用")
    private Integer stopUsing;
    
    @ApiModelProperty(value = "文件list")
    private List<OssVo> listFile;
}
