package com.huanhong.wms.entity.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@ApiModel(description="部门物料采购同比参数")
public class MaterialYearParam {

    @ApiModelProperty(value = "物料编码")
    private String materialCoding;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "部门ID")
    private String deptId;

    @ApiModelProperty(value = "年份")
    @JsonFormat(pattern = "yyyy")
    @DateTimeFormat(pattern = "yyyy")
    private Date yearTime;


}
