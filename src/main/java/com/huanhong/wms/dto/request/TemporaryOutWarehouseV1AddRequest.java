package com.huanhong.wms.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author wang
 * @date 2022/5/25 16:35
 */
@Data
public class TemporaryOutWarehouseV1AddRequest {

    @Valid
    @ApiModelProperty(value = "临时出库主表")
    TemporaryOutWarehouseRequest temporaryOutWarehouseRequest;

    @Valid
    @ApiModelProperty(value = "临时出库子表")
    List<TemporaryOutWarehouseDetailsRequest> temporaryOutWarehouseDetailsRequest;
}
