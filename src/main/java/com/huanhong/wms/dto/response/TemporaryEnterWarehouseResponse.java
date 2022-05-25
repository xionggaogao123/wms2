package com.huanhong.wms.dto.response;

import com.huanhong.wms.entity.TemporaryEnterWarehouse;
import com.huanhong.wms.entity.TemporaryEnterWarehouseDetails;
import lombok.Data;

import java.util.List;

/**
 * @Author wang
 * @date 2022/5/25 14:00
 */
@Data
public class TemporaryEnterWarehouseResponse {

    TemporaryEnterWarehouse temporaryEnterWarehouse;

    List<TemporaryEnterWarehouseDetails> temporaryEnterWarehouseDetails;
}
