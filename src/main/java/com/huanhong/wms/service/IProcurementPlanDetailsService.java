package com.huanhong.wms.service;

import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ProcurementPlanDetails;
import com.huanhong.wms.entity.dto.AddProcurementPlanDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateProcurementPlanDetailsDTO;

import java.util.List;

/**
 * <p>
 * 采购计划明细表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-21
 */
public interface IProcurementPlanDetailsService extends SuperService<ProcurementPlanDetails> {

    /**
     * 新增采购计划明细表
     * @param addProcurementPlanDetailsDTOList
     * @return
     */
    Result addProcurementPlanDetails(List<AddProcurementPlanDetailsDTO> addProcurementPlanDetailsDTOList);

    /**
     * 更新采购计划明细表
     * @param updateProcurementPlanDetailsDTOList
     * @return
     */
    Result updateProcurementPlanDetails(List<UpdateProcurementPlanDetailsDTO> updateProcurementPlanDetailsDTOList);

    /**
     * 通过Id获取采购计划明细表
     * @param id
     * @return
     */
    ProcurementPlanDetails getProcurementPlanDetailsById(Integer id);

    /**
     * 通过采购计划单据编号和仓库编号获取明细list
     * @param docNum
     * @param warehouseId
     * @return
     */
    List<ProcurementPlanDetails> getProcurementPlanDetailsByDocNumAndWarehouseId(String docNum, String warehouseId);

}
