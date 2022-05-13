package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@ApiModel(description = "新增盘点报告明细")
public class AddMakeInventoryReportDetailsDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "盘点报告编号")
    private String reportNumber;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;

    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "批次")
    private String batch;

    @Min(0)
    @Max(2)
    @ApiModelProperty(value = "库存类型：0-正式库存 1-暂存库存 2-临时库存")
    private Integer inventoryType;

    @ApiModelProperty(value = "库存数量")
    private Double inventoryCredit;

    @ApiModelProperty(value = "实盘数量")
    private Double checkCredit;

    @ApiModelProperty(value = "盈亏数量")
    private Double finalCredit;

    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;

    @Min(0)
    @Max(3)
    @ApiModelProperty(value = "盘点状态: 0-待盘点，1-一致 ，2-盘盈 ，3-盘亏")
    private Integer checkStatusDetails;

    @ApiModelProperty(value = "单价(泰丰盛和)")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "单价(使用单位)")
    private BigDecimal salesUnitPrice;

    @ApiModelProperty(value = "盈亏金额")
    private BigDecimal finalAmounts;

    @ApiModelProperty(value = "差异原因")
    private String reason;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;

}
