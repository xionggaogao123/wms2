package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.WarehouseManagement;
import com.huanhong.wms.entity.vo.WarehouseVo;

import java.util.List;

/**
 * <p>
 * 仓库管理 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-08
 */
public interface IWarehouseManagementService extends SuperService<WarehouseManagement> {


    //根据公司ID获取所属库房信息
    List<WarehouseManagement> getWarehouseByCompanyId(String CompanyId);

    //根据库房编号获取获取库房信息
    WarehouseManagement getWarehouseByWarehouseId(String WarehouseId);

//    //单个模糊查询
//    List<String> fuzzyQuerySelectList(String field,String value);

    //组合分页模糊查询
    Page<WarehouseManagement> pageFuzzyQuery(Page<WarehouseManagement> warehouseManagementPage, WarehouseVo warehouseVo);

}
