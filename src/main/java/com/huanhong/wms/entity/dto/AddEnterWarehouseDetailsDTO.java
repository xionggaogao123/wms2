package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "新增入库明细单")
public class AddEnterWarehouseDetailsDTO {

    private static final long serialVersionUID=1L;

    @NotBlank
    @ApiModelProperty(value = "原单据编号")
    private String originalDocumentNumber;

    @NotBlank
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @NotBlank
    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "有效日期")
    private LocalDateTime validPeriod;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "应收数量")
    private Integer quantityReceivable;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "实收数量")
    private Integer actualQuantity;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "不含税单价")
    private BigDecimal unitPriceWithoutTax;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "不含税金额")
    private BigDecimal excludingTaxAmount;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "含税单价")
    private BigDecimal unitPriceIncludingTax;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "含税金额")
    private BigDecimal taxIncludedAmount;

    @Min(0)
    @ApiModelProperty(value = "税率")
    private Float taxRate;

    @NotBlank
    @ApiModelProperty(value = "仓库")
    private String warehouse;

    @ApiModelProperty(value = "贷款税额")
    private BigDecimal loanTax;

    @ApiModelProperty(value = "备注")
    private String remark;
}
