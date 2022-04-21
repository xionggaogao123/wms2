package com.huanhong.wms.entity.vo;

import cn.hutool.core.util.NumberUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ApiModel(value = "库存流水账",description = "库存流水账")
@Data
public class InventoryRecordVo {


    private Integer index;

    @ApiModelProperty(value = "业务类型 出库，入库")
    private String objectType;
    @ApiModelProperty(value = "物料编号")
    private String materialCoding;
    @ApiModelProperty(value = "物料名称")
    private String materialName;
    @ApiModelProperty(value = "规格型号")
    private String specificationModel;
    @ApiModelProperty(value = "出货数量")
    private Double outQuantity;
    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;

    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "生产厂家")
    private String supplier2;


    @ApiModelProperty(value = "仓库名")
    private String warehouseName;
    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private String consignorStr;
    private Integer consignor;
    @ApiModelProperty(value = "物料分类")
    private String typeCode;


    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "供应商")
    private String supplier;
    @ApiModelProperty(value = "库管员")
    private String librarian;

    @ApiModelProperty(value = "业务时间")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    public BigDecimal getAmount() {
        return NumberUtil.mul(outQuantity,unitPrice);
    }
}
