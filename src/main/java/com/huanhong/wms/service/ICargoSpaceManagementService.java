package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.CargoSpaceManagement;
import com.huanhong.wms.entity.ShelfManagement;
import com.huanhong.wms.entity.vo.ShelfVO;

import java.util.List;

/**
 * <p>
 * 货位管理 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-08
 */
public interface ICargoSpaceManagementService extends SuperService<CargoSpaceManagement> {
    //根据货架ID获取所属货架
    List<CargoSpaceManagement> getCargoSpaceListByWarehouseAreaId(String shlefId);

    //根据货位ID获取货架信息
    CargoSpaceManagement getShelfByShelfId(String shelfId);

    //组合分页模糊查询
    Page<ShelfManagement> pageFuzzyQuery(Page<ShelfManagement> shelfManagementPage, ShelfVO shelfVO);
}
