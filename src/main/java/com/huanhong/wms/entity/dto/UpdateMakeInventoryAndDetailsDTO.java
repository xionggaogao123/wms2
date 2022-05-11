package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel(description="更新盘点单主表及明细")
public class UpdateMakeInventoryAndDetailsDTO {

    @Valid
    @ApiModelProperty(value = "盘点单主表")
    private UpdateMakeInventoryDTO updateMakeInventoryDTO;

    @Valid
    @ApiModelProperty(value = "盘点单明细list")
    private List<UpdateMakeInventoryDetailsDTO> updateMakeInventoryDetailsDTOList;

}
