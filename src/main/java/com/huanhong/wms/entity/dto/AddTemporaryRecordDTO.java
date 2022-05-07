package com.huanhong.wms.entity.dto;


import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description="新增临库出入库记录")
public class AddTemporaryRecordDTO {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @ApiModelProperty(value = "单据编号")
    private String documentNumber;

    @ApiModelProperty(value = "需求计划单据编号")
    private String requirementsPlanningNumber;

    @NotBlank
    @Min(1)
    @Max(2)
    @ApiModelProperty(value = "记录类型：1-临时库入库 2-临时库出库")
    private Integer recordType;

    @NotBlank
    @ApiModelProperty(value = "库房ID")
    private String warehouseId;

    @NotBlank
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @NotBlank
    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;

    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @Min(0)
    @ApiModelProperty(value = "入库数量")
    private Double enterQuantity;

    @Min(0)
    @ApiModelProperty(value = "出库数量")
    private Double outQuantity;

    @ApiModelProperty(value = "库管员")
    private String warehouseManager;

    @ApiModelProperty(value = "领用人")
    private String recipient;

    @ApiModelProperty(value = "备注")
    private String remark;
}