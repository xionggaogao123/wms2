package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.WarehouseAreaManagement;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.WarehouseManagement;
import com.huanhong.wms.entity.vo.WarehouseAreaVO;
import com.huanhong.wms.entity.vo.WarehouseVo;

import java.util.List;

/**
 * <p>
 * 库房区域管理 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-08
 */
public interface IWarehouseAreaManagementService extends SuperService<WarehouseAreaManagement> {

    //根据子库ID获取所属库区信息
    List<WarehouseAreaManagement> getWarehouseAreaListBySublibraryId(String sublibraryId);

    //根据库区编号获取获取库区信息
    WarehouseAreaManagement getWarehouseAreaByWarehouseAreaId(String WarehouseAreaId);

    //组合分页模糊查询
    Page<WarehouseAreaManagement> pageFuzzyQuery(Page<WarehouseAreaManagement> WarehouseAreaManagementPage, WarehouseAreaVO warehouseAreaVO);


}
