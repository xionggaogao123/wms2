package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.WarehouseAreaManagement;
import com.huanhong.wms.entity.dto.AddWarehouseAreaDTO;
import com.huanhong.wms.entity.vo.WarehouseAreaVO;

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

    //查询某库区是否停用 0- 使用中  1- 停用
    int isStopUsing(String warehouseAreaId);

    //新增库区
    Result addWarehouseArea(AddWarehouseAreaDTO addWarehouseAreaDTO);

    /**
     *
     * @param parentCode
     * @param enable true = 随父级启用  false = 随父级停用
     * @return
     */
    int stopUsingByParentCode(String parentCode,boolean enable);
}
