package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.ShelfManagement;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.WarehouseAreaManagement;
import com.huanhong.wms.entity.vo.ShelfVO;
import com.huanhong.wms.entity.vo.WarehouseAreaVO;

import java.util.List;

/**
 * <p>
 * 货架管理 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-08
 */
public interface IShelfManagementService extends SuperService<ShelfManagement> {

    //根据子库ID获取所属货架
    List<ShelfManagement> getShelfListByWarehouseAreaId(String warehouseAreaId);

    //根据货架ID获取货架信息
    ShelfManagement getShelfByWarehouseAreaId(String shelfId);

    //组合分页模糊查询
    Page<ShelfManagement> pageFuzzyQuery(Page<ShelfManagement> shelfManagementPage, ShelfVO shelfVO);


}
