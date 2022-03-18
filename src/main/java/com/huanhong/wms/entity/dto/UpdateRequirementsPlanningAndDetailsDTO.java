package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel("更新需求计划及其明细")
public class UpdateRequirementsPlanningAndDetailsDTO {

    @Valid
    @ApiModelProperty(value = "需求计划单主表")
    private UpdateRequirementsPlanningDTO updateRequirementsPlanningDTO;

    @Valid
    @ApiModelProperty(value = "需求计划名list")
    private List<UpdateRequiremetsPlanningDetailsDTO> updateRequiremetsPlanningDetailsDTOList;
}
