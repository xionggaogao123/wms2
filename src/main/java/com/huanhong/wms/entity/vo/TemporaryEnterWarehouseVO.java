package com.huanhong.wms.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@ApiModel(value = "分页查询临库入库")
public class TemporaryEnterWarehouseVO {

    @ApiModelProperty(value = "临时库入库单据编号")
    private String documentNumber;

    @Min(1)
    @Max(4)
    @ApiModelProperty(value = "状态:1.草拟 2.审批中 3.审批生效 4.作废")
    private Integer state;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "仓库编号")
    private String warehouseId;

    @ApiModelProperty(value = "创建日期-起始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;

    @ApiModelProperty(value = "创建日期-终结")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;

}
