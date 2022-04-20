package com.huanhong.wms.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huanhong.wms.entity.InventoryInformation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@ApiModel(description = "预过期物料预警结果")
@Data
public class PreExpirationInventoryInfoVo  {


    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "预过期数量")
    private Double inventoryCredit;

    @ApiModelProperty(value = "有效日期-失效日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date effectiveDate;


}
