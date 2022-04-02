package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "新增点验单DTO")
public class AddInventoryDocumentDTO {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    @ApiModelProperty(value = "送货单编号")
    private String deliveryNoteNumber;

    @NotEmpty
    @ApiModelProperty(value = "询价单编号")
    private String rfqNumber;

    @Min(0)
    @Max(1)
    @ApiModelProperty(value = "清点 0-未清点(默认) 1-已清点")
    private  Integer complete;

    @NotEmpty
    @ApiModelProperty(value = "仓库")
    private String warehouse;

    @ApiModelProperty(value = "备注")
    private String remark;

}
