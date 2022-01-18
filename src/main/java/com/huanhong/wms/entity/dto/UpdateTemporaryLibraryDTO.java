package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(description = "临库库存更新")
public class UpdateTemporaryLibraryDTO {

    private static final long serialVersionUID = 1L;

    @NotBlank
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

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

    @ApiModelProperty(value = "库存数量")
    @TableField("Inventory_credit")
    private Double inventoryCredit;

    @ApiModelProperty(value = "备注")
    private String remark;

}
