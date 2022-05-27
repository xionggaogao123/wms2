package com.huanhong.wms.entity.vo;

import com.huanhong.wms.entity.BalanceLibraryRecord;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "平衡利库记录")
public class BalanceLibraryRecordVo extends BalanceLibraryRecord {
    @ApiModelProperty("库存数量")
    private Double inventoryCredit;

}
