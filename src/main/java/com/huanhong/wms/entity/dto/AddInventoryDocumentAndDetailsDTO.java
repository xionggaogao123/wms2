package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel(description="新增点验单主表及明细")
public class AddInventoryDocumentAndDetailsDTO {

    @Valid
    @ApiModelProperty(value = "点验单主表")
    private AddInventoryDocumentDTO addInventoryDocumentDTO;

    @Valid
    @ApiModelProperty(value = "点验单明细list")
    private List<AddInventoryDocumentDetailsDTO> addInventoryDocumentDetailsDTOList;

}
