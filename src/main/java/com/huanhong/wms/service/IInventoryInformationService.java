package com.huanhong.wms.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.Meterial;

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
}
