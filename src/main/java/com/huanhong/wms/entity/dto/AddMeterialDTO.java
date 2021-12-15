package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "新增物料")
public class AddMeterialDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @NotNull
    @ApiModelProperty(value = "物料名称")
    private String materialName;


    @ApiModelProperty(value = "俗称")
    private String slang;

    @NotNull
    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @NotNull
    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;

    @NotNull
    @ApiModelProperty(value = "辅助单位")
    private String auxiliaryUnit;

    @NotNull
    @ApiModelProperty(value = "材质")
    private String material;

    @NotNull
    @ApiModelProperty(value = "执行标准")
    private String executiveStandard;

    @NotNull
    @ApiModelProperty(value = "技术要求")
    private String skillsRequiremen;

    @NotNull
    @ApiModelProperty(value = "图号")
    private String drawingNumber;


    @ApiModelProperty(value = "安全质量标准")
    private String safetyQualityStandards;

    @NotNull
    @ApiModelProperty(value = "生产厂家")
    private String supplier;


    @ApiModelProperty(value = "物料单位长度-米/M")
    private String materialLength;


    @ApiModelProperty(value = "物料单位宽-米/M")
    private String materialWidth;


    @ApiModelProperty(value = "物料单位高度-米/M")
    private String materialHeight;


    @ApiModelProperty(value = "物料单位重量-kg/千克")
    private String materialWeight;


    @ApiModelProperty(value = "品牌")
    private String brand;


    @ApiModelProperty(value = "备注")
    private String remark;

    @NotNull
    @ApiModelProperty(value = "失效提醒时间")
    private LocalDateTime expirationReminderTime;

}
