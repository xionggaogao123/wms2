package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "临库点验更新")
public class UpdateTemporaryLibraryInventoryDTO {

    private static final long serialVersionUID = 1L;

    @NotNull
    @ApiModelProperty(value = "ID", required = true)
    private Integer id;


    @ApiModelProperty(value = "送货单编号")
    private String deliveryNoteNumber;

    @ApiModelProperty(value = "申请单编号")
    private String rfqNumber;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "是否完成清点（0-未清点 1-已清点）")
    private Integer complete;

    @ApiModelProperty(value = "仓库编号")
    private String warehouseId;

    @ApiModelProperty(value = "备注")
    private String remark;
}
