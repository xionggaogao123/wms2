package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel("更新采购计划及其明细")
public class UpdateProcurementPlanAndDetailsDTO {
    @Valid
    @ApiModelProperty(value = "采购计划单主表")
    private UpdateProcurementPlanDTO updateProcurementPlanDTO;

    @Valid
    @ApiModelProperty(value = "采购计划明细表")
    private List<UpdateProcurementPlanDetailsDTO> updateProcurementPlanDetailsDTOList;
}