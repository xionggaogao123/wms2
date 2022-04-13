package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "联合出库查询返回VO")
public class OutboundForPdaVO {

    /**
     * id  单据编号  出库状态  区分参数  领用单位 领用人 调入库房
     */
    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "单据编号")
    private String docNum;

    @ApiModelProperty(value = "区分参数 0-领料出库 1-调拨出库")
    private Integer docType;

    @ApiModelProperty(value = "出库状态：0-未完成  1-全部出库")
    private Integer outStatus;

    @ApiModelProperty(value = "领用单位")
    private String requisitioningUnit;

    @ApiModelProperty(value = "领用人")
    private String recipient;

    @ApiModelProperty(value = "调入仓库")
    private String enterWarehouse;

}
