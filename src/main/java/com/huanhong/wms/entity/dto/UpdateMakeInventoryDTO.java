package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel("更新盘点单DTO")
public class UpdateMakeInventoryDTO {

    private static final long serialVersionUID=1L;

    @NotNull
    @ApiModelProperty(value = "盘点单ID", required = true)
    private Integer id;

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

    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @ApiModelProperty(value = "批次")
    private String batch;

    @Min(0)
    @ApiModelProperty(value = "库存数量")
    private Double inventoryCredit;

    @Min(0)
    @ApiModelProperty(value = "实盘数量")
    private Double checkCredit;

    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "状态: 0-待盘点，1-已盘点")
    private Integer checkStatus;

    @ApiModelProperty(value = "单价(泰丰盛和)")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "单价(使用单位)")
    private BigDecimal salesUnitPrice;

    @ApiModelProperty(value = "盘点开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty(value = "盘点结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(value = "差异原因")
    private String reason;

    @ApiModelProperty(value = "盘点人id")
    private Integer userId;

    @ApiModelProperty(value = "盘点人名字")
    private String userName;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "备注")
    private String remark;

}
