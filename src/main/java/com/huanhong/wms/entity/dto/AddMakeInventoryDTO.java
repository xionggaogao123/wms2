package com.huanhong.wms.entity.dto;


import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
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
@ApiModel("新增盘点单DTO")
public class AddMakeInventoryDTO {

    private static final long serialVersionUID=1L;

    @NotEmpty
    @ApiModelProperty(value = "子库编号")
    private String sublibraryId;

    @NotEmpty
    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;

    @NotEmpty
    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @NotEmpty
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @NotEmpty
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @NotEmpty
    @ApiModelProperty(value = "规格型号")
    private String specificationModel;

    @NotEmpty
    @ApiModelProperty(value = "批次")
    private String batch;

    @NotNull
    @Min(0)
    @ApiModelProperty(value = "库存数量")
    private Double inventoryCredit;

    @NotNull
    @Min(0)
    @ApiModelProperty(value = "实盘数量")
    private Double checkCredit;

    @NotEmpty
    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;

    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;

    @NotNull
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
