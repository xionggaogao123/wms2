package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;
import com.huanhong.wms.dto.request.UpdateTemporaryEnterWarehouseRequest;

/**
 * @Author wang
 * @date 2022/5/25 13:48
 */
public interface TemporaryEnterWarehouseV1Service {
    /**
     * 根据id查询临时入库主表和子表数据
     * @param id 根据id查询
     * @return 返回值
     */
    Result findById(Long id);

    /**
     * 修改临时入库主表和子表数据
     * @param updateTemporaryEnterWarehouseRequest 主表子表数据
     * @return  返回值
     */
    Result updateTemporaryEnterWarehouse(UpdateTemporaryEnterWarehouseRequest updateTemporaryEnterWarehouseRequest);


    /**
     * 根据id删除临时入库主表和子表数据
     * @param id id
     * @return 返回值
     */
    Result deleteById(Long id);
}
