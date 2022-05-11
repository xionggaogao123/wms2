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

    @NotBlank
    @ApiModelProperty(value = "盘点单单据编号")
    private String documentNumber;

    @NotBlank
    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @NotBlank
    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

    @NotBlank
    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;

    @NotBlank
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

    @Min(0)
    @Max(2)
    @NotNull
    @ApiModelProperty(value = "库存类型：0-暂存库存 1-正式库存 2-临时库存")
    private Integer inventoryType;

    @NotNull
    @ApiModelProperty(value = "库存数量")
    private Double inventoryCredit;

    @ApiModelProperty(value = "实盘数量")
    private Double checkCredit;

    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;

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

    @Min(0)
    @Max(5)
    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;

}
