package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.EnterWarehouse;
import com.huanhong.wms.entity.TemporaryEnterWarehouse;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.dto.AddEnterWarehouseDTO;
import com.huanhong.wms.entity.dto.AddTemporaryEnterWarehouseDTO;
import com.huanhong.wms.entity.dto.UpdateEnterWarehouseDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryEnterWarehouseDTO;
import com.huanhong.wms.entity.vo.EnterWarehouseVO;
import com.huanhong.wms.entity.vo.TemporaryEnterWarehouseVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-05
 */
public interface ITemporaryEnterWarehouseService extends SuperService<TemporaryEnterWarehouse> {

    /**
     * 分页查询
     */
    Page<TemporaryEnterWarehouse> pageFuzzyQuery(Page<TemporaryEnterWarehouse> temporaryEnterWarehousePage, TemporaryEnterWarehouseVO temporaryEnterWarehouseVO);


    /**
     * 更新临库入库单
     * @param updateTemporaryEnterWarehouseDTO
     * @return
     */
    Result updateTemporaryEnterWarehouse(UpdateTemporaryEnterWarehouseDTO updateTemporaryEnterWarehouseDTO);


    /**
     * 新增临库入库单
     * @param addTemporaryEnterWarehouseDTO
     * @return
     */
    Result addEnterWarehouse(AddTemporaryEnterWarehouseDTO addTemporaryEnterWarehouseDTO);


    /**
     * 根据单据编号和仓库ID获取临库入库单
     * @param docNumber
     * @param warhouseId
     * @return
     */
    TemporaryEnterWarehouse getTemporaryEnterWarehouseByDocNumberAndWarhouseId(String docNumber,String warhouseId);


    /**
     * 根据ID获取临库入库单信息
     * @param id
     * @return
     */
    TemporaryEnterWarehouse getTemporaryEnterWarehouseById(Integer id);


    /**
     * 根据流程Id获取入库单信息
     * @param processInstanceId
     * @return
     */
    TemporaryEnterWarehouse getTemporaryEnterWarehouseByProcessInstanceId(String processInstanceId);

}
