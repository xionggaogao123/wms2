package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel(description="新增调拨计划出库主表及明细")
public class AddAllocationOutDTOAndDetails {

    @Valid
    @ApiModelProperty(value = "调拨出库主表")
    private AddAllocationOutDTO addAllocationOutDTO;

    @Valid
    @ApiModelProperty(value = "调拨出库明细list")
    private List<AddAllocationOutDetailsDTO> addAllocationOutDetailsDTOS;

}
