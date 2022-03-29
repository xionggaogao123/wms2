package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel(description = "更新调拨计划单和明细")
public class UpdateAllocationPlanAndDetailsDTO {

    @Valid
    @ApiModelProperty(value = "调拨计划单主表")
    private UpdateAllocationPlanDTO updateAllocationPlanDTO;

    @Valid
    @ApiModelProperty(value = "调拨计划单明细")
    private List<UpdateAllocationPlanDetailDTO> updateAllocationPlanDetailDTOList;

}
