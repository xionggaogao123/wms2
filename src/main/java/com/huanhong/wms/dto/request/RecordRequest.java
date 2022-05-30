package com.huanhong.wms.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author wang
 * @date 2022/5/30 20:29
 */
@Data
public class RecordRequest {


    @ApiModelProperty(value = "物料名称")
    private String materialName;


    @ApiModelProperty(value = "批次")
    private String batch;


    @ApiModelProperty(value = "物料编码")
    private String materialCoding;


}
