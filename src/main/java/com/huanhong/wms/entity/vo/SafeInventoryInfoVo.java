package com.huanhong.wms.entity.vo;

import com.huanhong.wms.entity.InventoryInformation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@ApiModel(description = "安全库存")
@Data
public class SafeInventoryInfoVo {


    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "库存数量")
    private Double inventoryCredit;

    @ApiModelProperty(value = "安全数量")
    private Double safeQuantity;


}
