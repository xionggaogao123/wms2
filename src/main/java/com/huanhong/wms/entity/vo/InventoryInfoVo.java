package com.huanhong.wms.entity.vo;

import cn.hutool.core.util.NumberUtil;
import com.huanhong.wms.entity.InventoryInformation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@ApiModel(value = "库存账",description = "库存账")
@Data
public class InventoryInfoVo extends InventoryInformation {

    private Integer index;

    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @ApiModelProperty(value = "生产厂家")
    private String supplier;


    @ApiModelProperty(value = "物料分类")
    private String typeCode;


    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "库龄 单位天")
    private Long inDay;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private String consignorStr;

    public BigDecimal getAmount() {
        return NumberUtil.mul(getUnitPrice(),getInventoryCredit());
    }
}
