package com.huanhong.wms.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author wang
 * @date 2022/5/25 16:53
 */
@Data
public class TemporaryOutWarehouseDetailsRequest {

    @ApiModelProperty(value = "领用数量",required = true)
    private Double requisitionQuantity;

    @NotBlank(message = "物料编码不能为空")
    @ApiModelProperty(value = "物料编码", required = true)
    private String materialCoding;

    @NotBlank(message = "物料名称不能为空")
    @ApiModelProperty(value = "物料名称", required = true)
    private String materialName;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "备注")
    private String remark;

    @NotBlank(message = "库房ID不能为空")
    @ApiModelProperty(value = "仓库", required = true)
    private String warehouseId;
}
