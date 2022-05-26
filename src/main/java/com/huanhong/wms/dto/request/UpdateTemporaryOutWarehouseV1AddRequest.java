package com.huanhong.wms.dto.request;

import com.huanhong.wms.entity.TemporaryOutWarehouse;
import com.huanhong.wms.entity.TemporaryOutWarehouseDetails;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author wang
 * @date 2022/5/26 9:59
 */
@Data
public class UpdateTemporaryOutWarehouseV1AddRequest {

    @Valid
    @ApiModelProperty(value = "临时出库主表")
    TemporaryOutWarehouse temporaryOutWarehouse;

    @Valid
    @ApiModelProperty(value = "临时出库主表")
    List<TemporaryOutWarehouseDetails> temporaryOutWarehouseDetails;
}
