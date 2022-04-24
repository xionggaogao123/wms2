package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@ApiModel(description = "新增入库DTO")
public class AddWarehousingRecordDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "原单据编号")
    private String documentNumber;

    @ApiModelProperty(value = "入库类型：1-采购入库 2-调拨入库")
    private Integer enterType;

    @NotBlank
    @ApiModelProperty(value = "库房ID")
    private String warehouseId;

    @NotBlank
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @NotBlank
    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @NotBlank
    @ApiModelProperty(value = "批次")
    private String batch;

    @Min(0)
    @ApiModelProperty(value = "入库数量")
    private Double enterQuantity;

    @Min(0)
    @ApiModelProperty(value = "单价(泰丰盛和)")
    private BigDecimal unitPrice;

    @Min(0)
    @ApiModelProperty(value = "单价(使用单位)")
    private BigDecimal salesUnitPrice;

    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;

    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "库管员（提交入库申请表单的用户）")
    private String warehouseManager;

}
