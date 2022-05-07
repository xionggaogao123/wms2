package com.huanhong.wms.entity.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel(description="新增临库清点主表及明细")
public class AddTemporaryLibraryInventoryAndDetailsDTO {

    @Valid
    @ApiModelProperty(value = "临库清点主表")
    private AddTemporaryLibraryInventoryDTO addTemporaryLibraryInventoryDTO;

    @Valid
    @ApiModelProperty(value = "临库清点明细list")
    private List<AddTemporaryLibraryInventoryDetailsDTO> addTemporaryLibraryInventoryDetailsDTOList;

}
