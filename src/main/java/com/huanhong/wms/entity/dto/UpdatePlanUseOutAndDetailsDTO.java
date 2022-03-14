package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel(description = "更新出库单和明细单")
public class UpdatePlanUseOutAndDetailsDTO {
    @Valid
    @ApiModelProperty(value = "出库单主表")
    private UpdatePlanUseOutDTO updatePlanUseOutDTO;

    @Valid
    @ApiModelProperty(value = "出库单明细")
    private List<UpdatePlanUseOutDetailsDTO> updatePlanUseOutDetailsDTOList;
}

