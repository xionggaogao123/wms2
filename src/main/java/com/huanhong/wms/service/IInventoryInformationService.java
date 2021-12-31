package com.huanhong.wms.service;

import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.InventoryInformation;

import java.util.List;

/**
 * <p>
 * 库存表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2021-11-25
 */
public interface IInventoryInformationService extends SuperService<InventoryInformation> {


    /**
     * 库存信息更新
     */
    int updateInventoryInformation(InventoryInformation inventoryInformation);

    /**
     * 库存信息删除-下架
     */

    /**
     * 根据货位编码查询其中存储的物料信息
     */
    List<InventoryInformation>  getInventoryInformationByCargoSpaceId(String cargoSpaceId);
}
