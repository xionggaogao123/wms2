package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.CargoSpaceManagement;
import com.huanhong.wms.entity.vo.CargoSpaceVO;

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
    //根据货架ID获取所属货位
    List<CargoSpaceManagement> getCargoSpaceListByShelfId(String shelfId);

    //根据货位ID获取货架信息
    CargoSpaceManagement getCargoSpaceByCargoSpaceId(String cargoSpaceId);

    //组合分页模糊查询
    Page<CargoSpaceManagement> pageFuzzyQuery(Page<CargoSpaceManagement> cargoSpaceManagementPage, CargoSpaceVO cargoSpaceVO);
}
