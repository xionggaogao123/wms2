package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.vo.InventoryInformationVO;

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
     * 分页查询
     */
    Page<InventoryInformation> pageFuzzyQuery(Page<InventoryInformation> inventoryInformationPage, InventoryInformationVO inventoryInformationVO);

    /**
     * 库存信息更新
     */
    int updateInventoryInformation(InventoryInformation inventoryInformation);


    /**
     * 根据货位编码查询其中存储的物料信息
     */
    List<InventoryInformation>  getInventoryInformationByCargoSpaceId(String cargoSpaceId);

    /**
     * 获取库存list
     * @param inventoryInformationVO
     * @return
     */
    List<InventoryInformation> getInventoryInformation(InventoryInformationVO inventoryInformationVO);

}
