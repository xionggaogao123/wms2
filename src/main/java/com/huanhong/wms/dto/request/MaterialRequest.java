package com.huanhong.wms.dto.request;

import com.huanhong.wms.entity.Material;
import com.huanhong.wms.entity.MaterialPrice;
import lombok.Data;

import java.util.List;

/**
 * @Author wang
 * @date 2022/5/31 1:01
 */
@Data
public class MaterialRequest {

    List<Material> materialslist;

    List<MaterialPrice> materialPrices;
}
