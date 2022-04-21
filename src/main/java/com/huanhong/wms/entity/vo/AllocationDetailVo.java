package com.huanhong.wms.entity.vo;

import com.huanhong.wms.entity.AllocationPlan;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel(value = "调拨明细汇总表",description = "调拨明细汇总表")
@Data
public class AllocationDetailVo extends AllocationPlan {

    private Integer index;

    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @ApiModelProperty(value = "生产厂家")
    private String supplier2;


    @ApiModelProperty(value = "物料分类")
    private String typeCode;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private String consignorStr;
    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;

    @ApiModelProperty(value = "入库类型：1-采购入库 2-调拨入库")
    private String enterTypeStr;
    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;
    @ApiModelProperty(value = "库房名称")
    private String warehouseName;
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;
    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;
    @ApiModelProperty(value = "批次")
    private String batch;
    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "调出单位")
    private String sendCompany;

    @ApiModelProperty(value = "调入单位")
    private String receiveCompany;

    @ApiModelProperty(value = "调拨出库单据编号")
    private String allocationOutNumber;
    @ApiModelProperty(value = "实出数量 调出")
    private Double outboundQuantity;
    @ApiModelProperty(value = "实出数量 调入")
    private Double outboundQuantity3;
    @ApiModelProperty(value = "总金额 调出")
    private BigDecimal totalAmount2;
    @ApiModelProperty(value = "总金额 调入")
    private BigDecimal totalAmount3;
    @ApiModelProperty(value = "调出时间")
    private LocalDateTime createTime2;
    @ApiModelProperty(value = "调入时间")
    private LocalDateTime createTime3;
    @ApiModelProperty(value = "库管员 调出")
    private String librarian2;
    @ApiModelProperty(value = "库管员 调入")
    private String librarian3;

}
