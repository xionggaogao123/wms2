package com.huanhong.wms.entity.vo;

import com.huanhong.wms.entity.BalanceLibraryDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "平衡利库明细")
public class BalanceLibraryDetailVo extends BalanceLibraryDetail {

    @ApiModelProperty("平衡利库记录")
    private List<BalanceLibraryRecordVo> records;
    @ApiModelProperty("预调拨总数")
    private Double sumPreCalibrationQuantity;
    @ApiModelProperty("批准调拨总数")
    private Double sumCalibrationQuantity;
}
