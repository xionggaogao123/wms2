package com.huanhong.wms.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ApiModel(description="部门物料采购同比结果")
public class MaterialYearVO {

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "部门ID")
    private String deptId;

    @ApiModelProperty(value = "年份")
    private Integer yearTime;

    @ApiModelProperty(value = "数量")
    private Integer amount;

    @ApiModelProperty(value = "月份")
    private Integer monthTime;


}
