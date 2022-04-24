package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;


@ApiModel(value = "入库查询VO")
@Data
public class WarehousingRecordVO {

    @ApiModelProperty(value = "原单据编号")
    private String documentNumber;

    @Min(1)
    @Max(2)
    @ApiModelProperty(value = "入库类型：1-采购入库 2-调拨入库")
    private Integer enterType;

    @ApiModelProperty(value = "库房ID")
    private String warehouseId;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @ApiModelProperty(value = "批次")
    private String batch;

    @Min(0)
    @Max(5)
    @ApiModelProperty(value = "货主 0-泰丰盛和  1-润中，2-雅店，3-蒋家河，4-下沟，5-精煤")
    private Integer consignor;

    @ApiModelProperty(value = "供应商")
    private String supplier;

    @ApiModelProperty(value = "库管员（提交入库申请表单的用户）")
    private String warehouseManager;

    @ApiModelProperty(value = "创建日期-起始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateStart;

    @ApiModelProperty(value = "创建日期-结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDateEnd;
}
