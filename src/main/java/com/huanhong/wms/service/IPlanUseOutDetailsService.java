package com.huanhong.wms.service;

import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.PlanUseOutDetails;
import com.huanhong.wms.entity.dto.AddPlanUseOutDetailsDTO;
import com.huanhong.wms.entity.dto.UpdatePlanUseOutDetailsDTO;

import java.util.List;

/**
 * <p>
 * 计划领用明细表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-02-15
 */
public interface IPlanUseOutDetailsService extends SuperService<PlanUseOutDetails> {

    /**
     * 领料出库单明细新增
     * @param listAddDto
     * @return
     */
    Result addPlanUseOutDetails(List<AddPlanUseOutDetailsDTO> listAddDto);


    /**
     * 领料出库单明细更新
     * @param updatePlanUseOutDetailsDTOList
     * @return
     */
    Result updatePlanUseOutDetails(List<UpdatePlanUseOutDetailsDTO> updatePlanUseOutDetailsDTOList);

    /**
     * 领料出库单明细更新
     * @param updatePlanUseOutDetailsDTO
     * @return
     */
    Result updatePlanUseOutDetails(UpdatePlanUseOutDetailsDTO updatePlanUseOutDetailsDTO);

    /**
     * 根据原单据编号和仓库获取明细list
     * @param documentNumber
     * @param warehouseId
     * @return
     */
    List<PlanUseOutDetails> getListPlanUseOutDetailsByDocNumberAndWarehosue(String documentNumber, String warehouseId);


    /**
     * 根据原单据编号和出库状态和仓库获取明细list
     * @param documentNumber
     * @param warehouseId
     * @return
     */
    List<PlanUseOutDetails> getListPlanUseOutDetailsByDocNumberAndWarehosueAndOutStatus(String documentNumber, String warehouseId,Integer outStatus);

    /**
     * 根据明细ID获取明细信息
     * @param id
     * @return
     */
    PlanUseOutDetails getPlanUseOutDetailsByDetailsId(int id);

}
