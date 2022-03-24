package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel(description = "更新到货检验单和明细单")
public class UpdateArrivalVerificationAndDetailsDTO {

    @Valid
    @ApiModelProperty(value = "检验单主表")
    private UpdateArrivalVerificationDTO updateArrivalVerificationDTO;

    @Valid
    @ApiModelProperty(value = "检验单明细")
    private List<UpdateArrivalVerificationDetailsDTO> updateArrivalVerificationDetailsDTOList;
}
