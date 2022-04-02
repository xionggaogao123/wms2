package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "更新点验单明细DTO")
public class UpdateInventoryDocumentDetailsDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "点验单ID", required = true)
    private Integer id;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "应到数量")
    private Double receivableQuantity;

    @ApiModelProperty(value = "到货数量")
    private Double arrivalQuantity;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "仓库")
    private String warehouse;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "是否完成清点（0-未清点 1-已清点）")
    private Integer complete;

    @ApiModelProperty(value = "备注")
    private String remark;
}
