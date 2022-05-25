package com.huanhong.wms.dto.request;

import com.huanhong.wms.entity.TemporaryEnterWarehouse;
import com.huanhong.wms.entity.TemporaryEnterWarehouseDetails;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

/**
 * @Author wang
 * @date 2022/5/25 14:18
 */
@Data
public class UpdateTemporaryEnterWarehouseRequest {

    @Valid
    @ApiModelProperty(value = "临时入库主表")
    TemporaryEnterWarehouse temporaryEnterWarehouse;

    @Valid
    @ApiModelProperty(value = "临时入库子表")
    List<TemporaryEnterWarehouseDetails> temporaryEnterWarehouseDetails;
}
