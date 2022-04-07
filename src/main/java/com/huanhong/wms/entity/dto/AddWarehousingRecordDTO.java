package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "新增入库DTO")
public class AddWarehousingRecordDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "原单据编号")
    private String documentNumber;

    @ApiModelProperty(value = "出库类型：1-采购入库 2-调拨入库")
    private Integer outType;

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
    private Double outQuantity;

}
