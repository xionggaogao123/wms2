package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(description="临库点验明细表")
public class AddAllocationEnterDetailsDTO {

    private static final long serialVersionUID=1L;


    @ApiModelProperty(value = "调拨入库单据编号-系统自动添加")
    private String allocationEnterNumber;

    @NotBlank
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @NotNull
    @ApiModelProperty(value = "应收数量")
    private Double calibrationQuantity;

    @NotNull
    @ApiModelProperty(value = "实收数量")
    private Double outboundQuantity;

    @NotBlank
    @ApiModelProperty(value = "批次")
    private String batch;

    @NotNull
    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;

    @NotNull
    @ApiModelProperty(value = "总金额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "有效日期")
    private LocalDateTime effectiveDate;

    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;

    @ApiModelProperty(value = "备注")
    private String remark;

}
