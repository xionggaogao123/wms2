package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel(description="更新调拨入库主表及明细")
public class UpdateAllocationEnterAndDetailsDTO {

    @Valid
    @ApiModelProperty(value = "调拨入库主表")
    private UpdateAllocationEnterDTO updateAllocationEnterDTO;
    @Valid
    @ApiModelProperty(value = "调拨入库明细list")
    private List<UpdateAllocationEnterDetailsDTO> updateAllocationEnterDetailsDTOList;
}
