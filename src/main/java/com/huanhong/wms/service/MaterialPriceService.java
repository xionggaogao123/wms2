package com.huanhong.wms.service;

/**
 * @Author wang
 * @date 2022/5/30 12:18
 */
public interface MaterialPriceService {

    /**
     * 计算价格
     * @param materialCoding
     * @param materialName
     */
    void addMaterialPrice(String materialCoding,String materialName,String warehouseId);

    /**
     * 返回数据
     * @param materialCoding
     * @param warehouseId
     */
    void selectMaterialPrice(String materialCoding, String warehouseId);
}
