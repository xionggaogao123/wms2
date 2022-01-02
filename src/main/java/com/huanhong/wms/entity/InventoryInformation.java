package com.huanhong.wms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "库存表")
public class InventoryInformation extends SuperEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;

    @ApiModelProperty(value = "辅助单位")
    private String auxiliaryUnit;

    @ApiModelProperty(value = "货位编号")
    private String cargoSpaceId;

    @ApiModelProperty(value = "库存数量")
    private Double inventoryCredit;

    @ApiModelProperty(value = "安全数量")
    private Double safeQuantity;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "货主")
    private String consignor;

    @ApiModelProperty(value = "有效日期")
    private LocalDateTime effectiveDate;

    @ApiModelProperty(value = "单价(泰丰盛和)")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "管理费率(默认1.1)")
    private Double managementFeeRate;

    @ApiModelProperty(value = "单价(使用单位)")
    private BigDecimal salesUnitPrice;

    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "优先存放位置")
    private String priorityStorageLocation;

    @TableField(value = "create_time" ,fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "备注")
    private String remark;
}
