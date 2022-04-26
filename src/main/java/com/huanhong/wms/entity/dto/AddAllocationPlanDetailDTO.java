package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@ApiModel(description="新增调拨计划明细表DTO")
public class AddAllocationPlanDetailDTO {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "调拨单编号")
    private String allocationNumber;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @Min(0)
    @ApiModelProperty(value = "请调数量")
    private Double requestQuantity;

    @Min(0)
    @ApiModelProperty(value = "准调数量")
    private Double calibrationQuantity;


    @ApiModelProperty(value = "批次")
    private String batch;

    @Min(0)
    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;

    @Min(0)
    @ApiModelProperty(value = "总金额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;

    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "备注")
    private String remark;


}
