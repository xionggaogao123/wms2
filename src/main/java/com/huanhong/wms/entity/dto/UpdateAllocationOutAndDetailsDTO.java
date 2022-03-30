package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel(description="更新调拨计划出库主表及明细")
public class UpdateAllocationOutAndDetailsDTO {

    @Valid
    @ApiModelProperty(value = "调拨出库主表")
    private UpdateAllocationOutDTO updateAllocationOutDTO;

    @Valid
    @ApiModelProperty(value = "调拨出库明细list")
    private List<UpdateAllocationOutDetailsDTO> updateAllocationOutDetailsDTOS;
}
