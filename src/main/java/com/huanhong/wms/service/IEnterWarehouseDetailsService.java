package com.huanhong.wms.service;

import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.EnterWarehouseDetails;
import com.huanhong.wms.entity.dto.AddEnterWarehouseDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateEnterWarehouseDetailsDTO;

import java.util.List;

/**
 * <p>
 * 采购入库单明细表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-01-27
 */
public interface IEnterWarehouseDetailsService extends SuperService<EnterWarehouseDetails> {


    /**
     * 采购入库单明细新增
     *
     * @param listAddDto
     * @return
     */
    Result addEnterWarehouseDetails(List<AddEnterWarehouseDetailsDTO> listAddDto);


    /**
     * 采购入库单明细更新
     *
     * @param updateEnterWarehouseDetailsDTO
     * @return
     */
    Result updateEnterWarehouseDetails(UpdateEnterWarehouseDetailsDTO updateEnterWarehouseDetailsDTO);


    /**
     * 更新明细LIST
     * @param updateEnterWarehouseDetailsDTOList
     * @return
     */
    Result updateEnterWarehouseDetails(List<UpdateEnterWarehouseDetailsDTO> updateEnterWarehouseDetailsDTOList);


    /**
     * 根据原单据编号和仓库获取明细list
     *
     * @param documentNumber
     * @param Warehouse
     * @return
     */
    List<EnterWarehouseDetails> getListEnterWarehouseDetailsByDocNumberAndWarehosue(String documentNumber, String warehouse);

    /**
     * 根据明细ID获取明细信息
     *
     * @param id
     * @return
     */
    EnterWarehouseDetails getEnterWarehouseDetailsByDetailsID(int id);
}
