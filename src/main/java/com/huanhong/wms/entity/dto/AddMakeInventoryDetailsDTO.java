package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("新增盘点单明细DTO")
public class AddMakeInventoryDetailsDTO {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "盘点单单据编号")
    private String documentNumber;


    @ApiModelProperty(value = "库房编号")
    private String warehouseId;


    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;


    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;


    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @NotBlank
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @NotBlank
    @ApiModelProperty(value = "批次")
    private String batch;

    @NotNull
    @ApiModelProperty(value = "库存数量")
    private Double inventoryCredit;

    @ApiModelProperty(value = "实盘数量")
    private Double checkCredit;

    @ApiModelProperty(value = "稽核数量")
    private Double auditCredit;

    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;

    @ApiModelProperty(value = "盘点人Id")
    private Integer checkerId;

    @ApiModelProperty(value = "盘点人名")
    private String checkerName;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "状态: 0-待盘点，1-已盘点")
    private Integer checkStatus;

    @ApiModelProperty(value = "单价(泰丰盛和)")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "单价(使用单位)")
    private BigDecimal salesUnitPrice;

    @ApiModelProperty(value = "差异原因")
    private String reason;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "供应商")
    private String supplier;

}
