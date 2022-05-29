package com.huanhong.wms.dto.request;

import com.huanhong.wms.entity.MakeInventoryReport;
import com.huanhong.wms.entity.MakeInventoryReportDetails;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author wang
 * @date 2022/5/28 19:48
 */
@Data
public class UpdateMakeInventoryReportRequest {
    @Valid
    @ApiModelProperty(value = "盘点单主表")
    private MakeInventoryReport makeInventoryReport;

    @Valid
    @ApiModelProperty(value = "盘点单主表")
    private List<MakeInventoryReportDetails> makeInventoryReportDetails;
}
