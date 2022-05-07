package com.huanhong.wms.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "临库库存更新")
public class UpdateTemporaryLibraryDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "ID", required = true)
    private Integer id;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @ApiModelProperty(value = "库房编号")
    private String warehouseId;

    @ApiModelProperty(value = "库存数量")
    @TableField("Inventory_credit")
    private Double inventoryCredit;

    @ApiModelProperty(value = "有效日期-失效日期")
    private LocalDateTime effectiveDate;

    @ApiModelProperty(value = "备注")
    private String remark;

}
