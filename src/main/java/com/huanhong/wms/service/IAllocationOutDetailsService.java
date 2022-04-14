package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.AllocationOutDetails;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.AllocationPlanDetail;
import com.huanhong.wms.entity.PlanUseOutDetails;
import com.huanhong.wms.entity.dto.AddAllocationOutDetailsDTO;
import com.huanhong.wms.entity.dto.AddAllocationPlanDetailDTO;
import com.huanhong.wms.entity.dto.UpdateAllocationOutDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateAllocationPlanDetailDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-30
 */
public interface IAllocationOutDetailsService extends SuperService<AllocationOutDetails> {

    /**
     * 新增调拨出库明细list
     * @param addAllocationOutDetailsDTOList
     * @return
     */
    Result addAllocationOutDetails(List<AddAllocationOutDetailsDTO> addAllocationOutDetailsDTOList);

    /**
     * 更新调拨出库明细list
     * @param updateAllocationOutDetailsDTOList
     * @return
     */
    Result updateAllocationOutDetails(List<UpdateAllocationOutDetailsDTO> updateAllocationOutDetailsDTOList);

    /**
     * 通过Id获取调拨出库明细
     * @param id
     * @return
     */
    AllocationOutDetails getAllocationOutDetailsById(Integer id);

    /**
     * 通过到货检验单据编号调拨出库明细List
     * @param docNum
     * @return
     */
    List<AllocationOutDetails> getAllocationOutDetailsListByDocNum(String docNum);


    /**
     * 根据原单据编号和出库状态和仓库获取明细list
     * @param documentNumber
     * @param outStatus
     * @return
     */
    List<AllocationOutDetails> getListAllocationOutDetailsByDocNumberAndOutStatus(String documentNumber, Integer outStatus);

}
