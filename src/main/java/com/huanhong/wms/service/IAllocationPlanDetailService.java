package com.huanhong.wms.service;

import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.AllocationPlan;
import com.huanhong.wms.entity.AllocationPlanDetail;
import com.huanhong.wms.entity.dto.AddAllocationPlanDetailDTO;
import com.huanhong.wms.entity.dto.UpdateAllocationPlanDetailDTO;

import java.util.List;

/**
 * <p>
 * 调拨计划明细表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-29
 */
public interface IAllocationPlanDetailService extends SuperService<AllocationPlanDetail> {

    /**
     * 新增调拨计划明细list
     * @param addAllocationPlanDetailDTOList
     * @return
     */
    Result addAllocationPlanDetails(List<AddAllocationPlanDetailDTO> addAllocationPlanDetailDTOList);

    /**
     * 更新采购计划明细表list
     * @param updateAllocationPlanDetailDTOList
     * @return
     */
    Result updateAllocationPlanDetails(List<UpdateAllocationPlanDetailDTO> updateAllocationPlanDetailDTOList);

    /**
     * 通过Id获取调拨计划明细单
     * @param id
     * @return
     */
    AllocationPlanDetail getAllocationPlanDetailById(Integer id);

    /**
     * 通过到货检验单据编号和仓库编号获取明细list
     * @param docNum
     * @return
     */
    List<AllocationPlanDetail> getAllocationPlanDetailsListByDocNum(String docNum);

}
