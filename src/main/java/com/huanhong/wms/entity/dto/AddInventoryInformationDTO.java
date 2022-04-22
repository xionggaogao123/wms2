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
import java.util.Date;

@Data
@ApiModel(description = "新增库存")
public class AddInventoryInformationDTO {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @NotEmpty
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @NotEmpty
    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;


    @ApiModelProperty(value = "辅助单位")
    private String auxiliaryUnit;

    @NotNull
    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "库存数量")
    private Double inventoryCredit;

    @Min(0)
    @ApiModelProperty(value = "安全数量")
    private Double safeQuantity;

    @NotEmpty
    @ApiModelProperty(value = "批次")
    private String batch;

    @Min(0)
    @Max(5)
    @NotNull
    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;


    @ApiModelProperty(value = "有效日期")
    private LocalDateTime effectiveDate;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "单价(泰丰盛和)")
    private BigDecimal unitPrice;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "管理费率(默认1.1)")
    private Double managementFeeRate;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "单价(使用单位)")
    private BigDecimal salesUnitPrice;

    @NotEmpty
    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "库房名称")
    private String warehouseName;

    @ApiModelProperty(value = "生产日期")
    private Date produceDate;

    @ApiModelProperty(value = "入库时间（泰丰）：入库单审批生效时间")
    private Date inDate;

    @ApiModelProperty(value = "采购入库单据编号（泰丰）")
    private String documentNumber;

    @ApiModelProperty(value = "最近一次库存数量更新时间")
    private LocalDateTime lastUpdateInventoryCredit;

    @ApiModelProperty(value = "入库时间（各单位）：入库单审批生效时间")
    private Date inDateOther;

    @ApiModelProperty(value = "采购入库单据编号（各单位）")
    private String documentNumberOther;

    @ApiModelProperty(value = "备注")
    private String remark;

}
