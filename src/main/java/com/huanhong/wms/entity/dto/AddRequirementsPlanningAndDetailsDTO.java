package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel("新增需求计划及其明细")
public class AddRequirementsPlanningAndDetailsDTO {

    @Valid
    @ApiModelProperty(value = "需求计划单主表")
    private AddRequirementsPlanningDTO addRequirementsPlanningDTO;

    @Valid
    @ApiModelProperty(value = "需求计划名list")
    private List<AddRequiremetsPlanningDetailsDTO> addRequiremetsPlanningDetailsDTOList;
}
