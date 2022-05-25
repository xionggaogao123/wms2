package com.huanhong.wms.entity.vo;

import com.huanhong.wms.entity.BalanceLibrary;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "平衡利库")
public class BalanceLibraryVo extends BalanceLibrary {

    @ApiModelProperty("明细")
    private List<BalanceLibraryDetailVo> details;
}
