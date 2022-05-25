package com.huanhong.wms.entity.dto;

import com.huanhong.wms.entity.TemporaryLibraryInventory;
import com.huanhong.wms.entity.TemporaryLibraryInventoryDetails;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
@ApiModel(description="更新临库清点主表及明细")
public class UpdateTemporaryLibraryInventoryAndDetailsDTO {

    @Valid
    @ApiModelProperty(value = "临库清点主表")
    private TemporaryLibraryInventory temporaryLibraryInventory;

    @Valid
    @ApiModelProperty(value = "临库清点明细list")
    private List<com.huanhong.wms.entity.TemporaryLibraryInventoryDetails> temporaryLibraryInventoryDetails;

}
