package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@ApiModel("新增领料出库单")
public class AddPlanUseOutDetailsDTO {

    private static final long serialVersionUID=1L;

    @NotBlank
    @ApiModelProperty(value = "原单据编号")
    private String usePlanningDocumentNumber;

    @NotBlank
    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @NotBlank
    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @NotNull
    @ApiModelProperty(value = "领用数量")
    private Double requisitionQuantity;


    @ApiModelProperty(value = "批准数量")
    private Double approvalsQuantity;


    @ApiModelProperty(value = "实出数量")
    private Double outboundQuantity;

    @NotEmpty
    @ApiModelProperty(value = "库房ID")
    private String warehouseId;

    @Min(0)
    @NotNull
    @ApiModelProperty(value = "库存数量")
    private Double inventoryCredit;

    @Min(0)
    @Max(2)
    @ApiModelProperty(value = "出库状态：0-未出库，1-部分出库，2-全部出库")
    private Integer outStatus;

    @ApiModelProperty(value = "使用地点")
    private String usePlace;

    @ApiModelProperty(value = "用途")
    private String purpose;

    @ApiModelProperty(value = "备注")
    private String remark;
}
