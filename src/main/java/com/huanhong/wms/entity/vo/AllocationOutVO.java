package com.huanhong.wms.entity.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@ApiModel(value = "调拨出库查询")
public class AllocationOutVO {

    @ApiModelProperty(value = "调拨出库单据编号")
    private String allocationOutNumber;

    @ApiModelProperty(value = "调拨计划单据编号")
    private String allocationNumber;

    @ApiModelProperty(value = "调出仓库")
    private String sendWarehouse;

    @ApiModelProperty(value = "调入仓库")
    private String enterWarehouse;

    @ApiModelProperty(value = "创建日期-起始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;

    @ApiModelProperty(value = "创建日期-终结")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;

}
