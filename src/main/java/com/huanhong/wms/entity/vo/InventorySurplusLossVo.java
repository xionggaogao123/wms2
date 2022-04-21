package com.huanhong.wms.entity.vo;

import cn.hutool.core.util.NumberUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel(value = "盘点盈亏表",description = "盘点盈亏表")
@Data
public class InventorySurplusLossVo {

//    {{$fe: list t.index	t.measurementUnit	t.materialCoding	t.materialName
//    t.specificationModel	t.unitPrice	t.batch	t.supplier2	t.consignorStr	t.typeCode
//    t.brand	t.supplier	t.inventoryCredit	t.totalAmount	t.checkCredit
//    t.totalAmountReal	t.inTotal	t.outToatal	t.numSurplus	t.totalAmountSurplus
//    t.numLoss	t.totalAmountLoss	t.reason	t.createTime	t.userName	t.remark}}
    private Integer index;

    @ApiModelProperty(value = "物料编号")
    private String materialCoding;
    @ApiModelProperty(value = "物料名称")
    private String materialName;
    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;

    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;


    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "生产厂家")
    private String supplier2;

    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private String consignorStr;
    private Integer consignor;
    @ApiModelProperty(value = "物料分类")
    private String typeCode;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "账面数量")
    private Double inventoryCredit;

    @ApiModelProperty(value = "账面总价")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "实盘数量")
    private Double checkCredit;

    @ApiModelProperty(value = "实盘总价")
    private BigDecimal totalAmountReal;


    @ApiModelProperty(value = "入库")
    private Double inTotal;

    @ApiModelProperty(value = "出库")
    private Double outTotal;

    @ApiModelProperty(value = "盘盈数量")
    private Double numSurplus;

    @ApiModelProperty(value = "盘盈总价")
    private BigDecimal totalAmountSurplus;


    @ApiModelProperty(value = "盘亏数量")
    private Double numLoss;

    @ApiModelProperty(value = "盘亏总价")
    private BigDecimal totalAmountLoss;

    @ApiModelProperty(value = "差异原因")
    private String reason;

    @ApiModelProperty(value = "盘点时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "盘点人")
    private String userName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "盘点开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "盘点结束时间")
    private LocalDateTime endTime;

    public BigDecimal getTotalAmount() {
        return NumberUtil.mul(inventoryCredit,unitPrice);
    }

    public BigDecimal getTotalAmountReal() {
        return NumberUtil.mul(checkCredit,unitPrice);
    }

    public BigDecimal getTotalAmountLoss() {
        return NumberUtil.mul(numLoss,unitPrice);
    }

    public BigDecimal getTotalAmountSurplus() {
        return NumberUtil.mul(numSurplus,unitPrice);
    }
}
