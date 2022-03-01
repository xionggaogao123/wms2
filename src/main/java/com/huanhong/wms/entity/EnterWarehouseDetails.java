package com.huanhong.wms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description="采购入库单明细表")
public class EnterWarehouseDetails extends SuperEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "原单据编号")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String originalDocumentNumber;

    @ApiModelProperty(value = "物料编码")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String materialCoding;

    @ApiModelProperty(value = "批次")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String batch;

    @ApiModelProperty(value = "有效日期")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime validPeriod;

    @ApiModelProperty(value = "应收数量")
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private Integer quantityReceivable;

    @ApiModelProperty(value = "实收数量")
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private Integer actualQuantity;

    @ApiModelProperty(value = "不含税单价")
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private BigDecimal unitPriceWithoutTax;

    @ApiModelProperty(value = "不含税金额")
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private BigDecimal excludingTaxAmount;

    @ApiModelProperty(value = "含税单价")
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private BigDecimal unitPriceIncludingTax;

    @ApiModelProperty(value = "含税金额")
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private BigDecimal taxIncludedAmount;

    @ApiModelProperty(value = "税率")
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private Float taxRate;

    @ApiModelProperty(value = "贷款税额")
    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    private BigDecimal loanTax;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "备注")
    private String remark;

    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "仓库")
    private String warehouse;

    @TableField(value = "create_time" ,fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @TableField(value = "last_update",fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "最后更新时间")
    private LocalDateTime lastUpdate;
}
