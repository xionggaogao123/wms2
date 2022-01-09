package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "更新库存")
public class UpdateInventoryInformationDTO {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;


    @NotEmpty
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "货位")
    private String cargoSpace;


    @Min(0)
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "库存数量")
    private Double inventoryCredit;

    @Min(0)
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "安全数量")
    private Double safeQuantity;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @NotEmpty
    @ApiModelProperty(value = "批次")
    private String batch;


    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "货主")
    private Integer consignor;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "有效日期")
    private LocalDateTime effectiveDate;

    @Min(0)
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "单价(泰丰盛和)")
    private BigDecimal unitPrice;

    @Min(0)
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "管理费率(默认1.1)")
    private Double managementFeeRate;

    @Min(0)
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "单价(使用单位)")
    private BigDecimal salesUnitPrice;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "优先存放位置")
    private String priorityStorageLocation;

    @ApiModelProperty(value = "备注")
    private String remark;

}
