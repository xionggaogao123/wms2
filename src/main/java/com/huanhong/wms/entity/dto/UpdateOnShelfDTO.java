package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("更新上架单信息")
public class UpdateOnShelfDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "更新上架", required = true)
    private Integer id;


    @ApiModelProperty(value = "物料编码")
    private String materialCoding;


    @Min(0)
    @ApiModelProperty(value = "库存数量")
    @TableField("Inventory_credit")
    private Double inventoryCredit;


    @Min(0)
    @ApiModelProperty(value = "待上数量/已上数量")
    private Double waitingQuantity;


    @ApiModelProperty(value = "仓库")
    private String warehouse;


    @ApiModelProperty(value = "库区编号")
    private String warehouseAreaId;


    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;


    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "是否完成上架（0-未上架 1-已上架）")
    private Integer complete;

    @ApiModelProperty(value = "备注")
    private String remark;
}
