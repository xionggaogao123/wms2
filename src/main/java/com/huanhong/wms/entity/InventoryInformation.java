package com.huanhong.wms.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import com.huanhong.wms.SuperEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "库存表")
public class InventoryInformation extends SuperEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "辅助单位")
    private String auxiliaryUnit;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "货位编号")
    private String cargoSpaceId;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "库存数量")
    private Double inventoryCredit;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "安全数量")
    private Double safeQuantity;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "批次")
    private String batch;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "有效日期-失效日期")
    private LocalDateTime effectiveDate;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "单价(泰丰盛和)")
    private BigDecimal unitPrice;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "管理费率(默认1.1)")
    private Double managementFeeRate;

    @TableField(updateStrategy = FieldStrategy.NOT_NULL)
    @ApiModelProperty(value = "单价(使用单位)")
    private BigDecimal salesUnitPrice;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "供应商")
    private String supplier;

    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "优先存放位置")
    private String priorityStorageLocation;

    @TableField(value = "create_time" ,fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @TableField(value = "last_update",fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "最后更新时间")
    private LocalDateTime lastUpdate;


    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    @ApiModelProperty(value = "备注")
    private String remark;

    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;

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

}
