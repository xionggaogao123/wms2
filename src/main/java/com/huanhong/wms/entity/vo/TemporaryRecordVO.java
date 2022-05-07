package com.huanhong.wms.entity.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@ApiModel(value = "分页查询临库出入库记录")
public class TemporaryRecordVO {

    @ApiModelProperty(value = "单据编号")
    private String documentNumber;

    @ApiModelProperty(value = "需求计划单据编号")
    private String requirementsPlanningNumber;

    @Min(1)
    @Max(2)
    @ApiModelProperty(value = "记录类型：1-临时库入库 2-临时库出库")
    private Integer recordType;

    @ApiModelProperty(value = "库房ID")
    private String warehouseId;

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "批次")
    private String batch;

    @ApiModelProperty(value = "货位编码")
    private String cargoSpaceId;

    @ApiModelProperty(value = "库管员")
    private String warehouseManager;

    @ApiModelProperty(value = "领用人")
    private String recipient;

    @ApiModelProperty(value = "创建日期-起始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;

    @ApiModelProperty(value = "创建日期-终结")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;


}
