package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@ApiModel(description = "更新库存")
public class UpdateInventoryInformationDTO {

    private static final long serialVersionUID = 1L;


    @NotNull
    @ApiModelProperty(value = "库存ID", required = true)
    private Integer id;


//    @NotBlank
//    @ApiModelProperty(value = "物料编码")
//    private String materialCoding;
//
//
//    @NotBlank
//    @ApiModelProperty(value = "批次")
//    private String batch;


    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;

    @Min(0)
    @ApiModelProperty(value = "库存数量")
    private Double inventoryCredit;

    @Min(0)
    @ApiModelProperty(value = "安全数量")
    private Double safeQuantity;

    @Min(0)
    @Max(5)
    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "是否检验 0-未检验 1-已检验")
    private Integer isVerification;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "是否入库 0-未入库 1-已入库")
    private Integer isEnter;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "是否上架 0-未上架 1-已上架")
    private Integer isOnshelf;

    @ApiModelProperty(value = "有效日期")
    private LocalDateTime effectiveDate;

    @Min(0)
    @ApiModelProperty(value = "单价(泰丰盛和)")
    private BigDecimal unitPrice;

    @Min(0)
    @ApiModelProperty(value = "管理费率(默认1.1)")
    private Double managementFeeRate;

    @Min(0)
    @ApiModelProperty(value = "单价(使用单位)")
    private BigDecimal salesUnitPrice;

    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "优先存放位置")
    private String priorityStorageLocation;

    @ApiModelProperty(value = "备注")
    private String remark;

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
