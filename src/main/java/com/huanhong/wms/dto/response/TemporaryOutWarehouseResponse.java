package com.huanhong.wms.dto.response;

import com.huanhong.wms.entity.TemporaryOutWarehouse;
import com.huanhong.wms.entity.TemporaryOutWarehouseDetails;
import lombok.Data;

import java.util.List;

/**
 * @Author wang
 * @date 2022/5/25 17:43
 */
@Data
public class TemporaryOutWarehouseResponse {

    TemporaryOutWarehouse temporaryOutWarehouse;

    List<TemporaryOutWarehouseDetails> warehouseDetails;

}
