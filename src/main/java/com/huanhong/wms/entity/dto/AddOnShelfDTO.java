package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "新增上架单DTO", description = "新增上架单DTO")
public class AddOnShelfDTO {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @NotNull
    @Min(0)
    @ApiModelProperty(value = "库存数量")
    @TableField("Inventory_credit")
    private Double inventoryCredit;

    @NotNull
    @Min(0)
    @ApiModelProperty(value = "待上数量")
    private Double waitingQuantity;

    @NotEmpty
    @ApiModelProperty(value = "仓库")
    private String warehouse;

    @NotNull
    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "是否完成上架（0-未上架 1-已上架）")
    private Integer complete;

    @ApiModelProperty(value = "备注")
    private String remark;
}
