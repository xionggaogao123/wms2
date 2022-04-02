package com.huanhong.wms.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel(description="更新点验单主表及明细")
public class UpdateInventoryDocumentAndDetailsDTO {

    @Valid
    @ApiModelProperty(value = "点验单主表")
    private UpdateInventoryDocumentDTO updateInventoryDocumentDTO;

    @Valid
    @ApiModelProperty(value = "点验单明细list")
    private List<UpdateInventoryDocumentDetailsDTO> updateInventoryDocumentDetailsDTOList;

}
