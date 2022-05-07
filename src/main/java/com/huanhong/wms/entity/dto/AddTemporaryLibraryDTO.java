package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "临库库存新增")
public class AddTemporaryLibraryDTO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "临库出入库单编号")
    private String documentNumber;

    @NotBlank
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;


    @ApiModelProperty(value = "物料名称")
    private String materialName;


    @ApiModelProperty(value = "计量单位")
    private String measurementUnit;


    @ApiModelProperty(value = "辅助单位")
    private String auxiliaryUnit;

    @NotBlank
    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @NotBlank
    @ApiModelProperty(value = "批次")
    private String batch;

    @NotBlank
    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @NotNull
    @Min(0)
    @ApiModelProperty(value = "库存数量")
    @TableField("Inventory_credit")
    private Double inventoryCredit;


    @ApiModelProperty(value = "有效日期-失效日期")
    private LocalDateTime effectiveDate;


    @ApiModelProperty(value = "备注")
    private String remark;
}
