package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(description="更新调拨计划出库明细表")
public class UpdateAllocationOutDetailsDTO {

    @NotNull
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "出库状态：0-未出库，1-部分出库，2-全部出库")
    private Integer outStatus;

    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;

    @ApiModelProperty(value = "准调数量")
    private Double calibrationQuantity;

    @ApiModelProperty(value = "实出数量")
    private Double outboundQuantity;

    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "总金额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "有效日期")
    private LocalDateTime effectiveDate;

    @ApiModelProperty(value = "备注")
    private String remark;
}
