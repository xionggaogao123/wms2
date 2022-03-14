package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel(description = "新增出库单和明细单")
public class AddPlanUseOutAndDetails {

    @Valid
    @ApiModelProperty(value = "出库单主表")
    private AddPlanUseOutDTO addPlanUseOutDTO;

    @Valid
    @ApiModelProperty(value = "出库单明细")
    private List<AddPlanUseOutDetailsDTO> addPlanUseOutDetailsDTOList;
}
