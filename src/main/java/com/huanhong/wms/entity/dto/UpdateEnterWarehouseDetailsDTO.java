package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "更新入库明细")

public class UpdateEnterWarehouseDetailsDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "明细单据ID", required = true)
    private Integer id;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "有效日期")
    private LocalDateTime validPeriod;

    @ApiModelProperty(value = "应收数量")
    private Double quantityReceivable;

    @ApiModelProperty(value = "实收数量")
    private Double actualQuantity;

    @ApiModelProperty(value = "不含税单价")
    private BigDecimal unitPriceWithoutTax;

    @ApiModelProperty(value = "不含税金额")
    private BigDecimal excludingTaxAmount;

    @ApiModelProperty(value = "含税单价")
    private BigDecimal unitPriceIncludingTax;

    @ApiModelProperty(value = "含税金额")
    private BigDecimal taxIncludedAmount;

    @ApiModelProperty(value = "税率")
    private Float taxRate;

    @ApiModelProperty(value = "贷款税额")
    private BigDecimal loanTax;

    @ApiModelProperty(value = "备注")
    private String remark;

}
