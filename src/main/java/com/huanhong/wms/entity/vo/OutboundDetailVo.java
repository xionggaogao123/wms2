package com.huanhong.wms.entity.vo;

import cn.hutool.core.util.NumberUtil;
import com.huanhong.wms.entity.OutboundRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@ApiModel(value = "领料出库明细表",description = "领料出库明细表")
@Data
public class OutboundDetailVo extends OutboundRecord {

    private Integer index;

    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @ApiModelProperty(value = "生产厂家")
    private String supplier;


    @ApiModelProperty(value = "物料分类")
    private String typeCode;


    @ApiModelProperty(value = "品牌")
    private String brand;


    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private String consignorStr;

    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;
    @ApiModelProperty(value = "库房名称")
    private String warehouseName;
    @ApiModelProperty(value = "物料名称")
    private String materialName;
    @ApiModelProperty(value = "领用单位")
    private String requisitioningUnit;
    @ApiModelProperty(value = "领用人")
    private String recipient;
    @ApiModelProperty(value = "费用项目")
    private String expenseItem;
    @ApiModelProperty(value = "费用承担单位")
    private String costBearingUnit;
    @ApiModelProperty(value = "物资用途")
    private String materialUse;
    @ApiModelProperty(value = "领用用途")
    private String requisitionUse;
    @ApiModelProperty(value = "库管员")
    private String librarian;
    public BigDecimal getAmount() {
        return NumberUtil.mul(getSalesUnitPrice(),getOutQuantity());
    }
}
