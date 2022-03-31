package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel(description="新增调拨计划入库主表及明细")
public class AddAllocationEnterAndDetailsDTO {

    @Valid
    @ApiModelProperty(value = "调拨入库主表")
    private AddAllocationEnterDTO addAllocationEnterDTO;

    @Valid
    @ApiModelProperty(value = "调拨入库明细list")
    private List<AddAllocationEnterDetailsDTO> addAllocationEnterDetailsDTOList;
}
