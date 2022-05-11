package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel(description="新增盘点单主表及明细")
public class AddMakeInventoryAndDetailsDTO {

    @Valid
    @ApiModelProperty(value = "盘点单主表")
    private AddMakeInventoryDTO addMakeInventoryDTO;

    @Valid
    @ApiModelProperty(value = "盘点单明细list")
    private List<AddMakeInventoryDetailsDTO> addMakeInventoryDetailsDTOList;

}
