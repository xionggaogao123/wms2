package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.dto.request.TemporaryOutWarehouseV1AddRequest;
import com.huanhong.wms.entity.TemporaryOutWarehouse;
import com.huanhong.wms.entity.vo.TemporaryOutWarehouseVO;

/**
 * @Author wang
 * @date 2022/5/25 17:01
 */
public interface TemporaryOutWarehouseV1Service {

    /**
     * 添加临时出库主表和子表数据
     * @param request 主表子表数据
     * @return 返回值
     */
    Result addMasterAndSublist(TemporaryOutWarehouseV1AddRequest request);

    /**
     * 根据id删除临时出库主表子表数据
     * @param id id
     * @return 返回值
     */
    Result selectById(Long id);

    /**
     * 根据id删除临时出库主表子表信息
     * @param id
     * @return
     */
    Result deleteById(Long id);

    /**
     * 分页查询临时出库数据
     * @param objectPage 分页信息
     * @param temporaryOutWarehouseVO 模糊查询条件
     * @return 返回值
     */
    Page<TemporaryOutWarehouse> pageFuzzyQuery(Page<TemporaryOutWarehouse> objectPage, TemporaryOutWarehouseVO temporaryOutWarehouseVO);

    /**
     * 查询所有临时库存信息
     * @return
     */
    Result selectAll();
}
