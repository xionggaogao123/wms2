package com.huanhong.wms.entity.vo;

import cn.hutool.core.util.NumberUtil;
import com.huanhong.wms.entity.WarehousingRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@ApiModel(value = "入库明细表",description = "入库明细表")
@Data
public class WarehousingDetailVo extends WarehousingRecord {

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

    @ApiModelProperty(value = "入库类型：1-采购入库 2-调拨入库")
    private String enterTypeStr;
    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;
    @ApiModelProperty(value = "库房名称")
    private String warehouseName;
    @ApiModelProperty(value = "物料名称")
    private String materialName;
    public BigDecimal getAmount() {
        return NumberUtil.mul(getUnitPrice(),getEnterQuantity());
    }
}
