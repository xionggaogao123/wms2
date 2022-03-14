package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;

@Data
@ApiModel("PDA-出库单分页查询")
public class PageQueryPlanUseOutPdaVO {

    @Valid
    @ApiModelProperty(value = "出库单查询VO")
    private PlanUseOutVO planUseOutVO;

    @Valid
    @ApiModelProperty(value = "出库状态")
    private Integer status;
}
