package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel(description = "新增到货检验单和明细单")
public class AddArrivalVerificationAndDetailsDTO {

    @Valid
    @ApiModelProperty(value = "检验单主表")
    private AddArrivalVerificationDTO addArrivalVerificationDTO;

    @Valid
    @ApiModelProperty(value = "检验单明细")
    private List<AddArrivalVerificationDetailsDTO> addArrivalVerificationDetailsDTOList;
}
