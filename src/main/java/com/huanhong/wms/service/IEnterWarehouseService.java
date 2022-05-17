package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ArrivalVerification;
import com.huanhong.wms.entity.EnterWarehouse;
import com.huanhong.wms.entity.dto.AddEnterWarehouseAndDetails;
import com.huanhong.wms.entity.dto.AddEnterWarehouseDTO;
import com.huanhong.wms.entity.dto.UpdateEnterWarehouseDTO;
import com.huanhong.wms.entity.param.MaterialPriceParam;
import com.huanhong.wms.entity.vo.EnterWarehouseVO;

/**
 * <p>
 * 采购入库单主表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-01-24
 */
public interface IEnterWarehouseService extends SuperService<EnterWarehouse> {

    /**
     * 分页查询
     */
    Page<EnterWarehouse> pageFuzzyQuery(Page<EnterWarehouse> enterWarehousePage, EnterWarehouseVO enterWarehouseVO);


    /**
     * 采购入库单更新
     * @param updateEnterWarehouseDTO
     * @return
     */
    Result updateEnterWarehouse(UpdateEnterWarehouseDTO updateEnterWarehouseDTO);


    /**
     * 采购入库单新增
     * @param addEnterWarehouseDTO
     * @return
     */
    Result addEnterWarehouse(AddEnterWarehouseDTO addEnterWarehouseDTO);


    /**
     * 根据单据编号获取采购入库单
     * @param docNumber
     * @param warhouse
     * @return
     */
    EnterWarehouse getEnterWarehouseByDocNumberAndWarhouse(String docNumber,String warhouse);


    /**
     * 根据ID获取入库单信息
     * @param id
     * @return
     */
    EnterWarehouse getEnterWarehouseById(Integer id);


    /**
     * 根据流程Id获取入库单信息
     * @param processInstanceId
     * @return
     */
    EnterWarehouse getEnterWarehouseByProcessInstanceId(String processInstanceId);


    Result<Object> getMaterialPriceByParam(MaterialPriceParam param);

    Result add(AddEnterWarehouseAndDetails addEnterWarehouseAndDetails);

    Result arrivalVerificationToEnterWarehouse(Integer userId, ArrivalVerification arrivalVerification);
}
