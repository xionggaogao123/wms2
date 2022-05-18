package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.AllocationOut;
import com.huanhong.wms.entity.AllocationPlan;
import com.huanhong.wms.entity.dto.AddAllocationOutDTO;
import com.huanhong.wms.entity.dto.AddAllocationOutDTOAndDetails;
import com.huanhong.wms.entity.dto.UpdateAllocationOutDTO;
import com.huanhong.wms.entity.vo.AllocationOutVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-30
 */
public interface IAllocationOutService extends SuperService<AllocationOut> {

    /**
     * 分页查询
     */
    Page<AllocationOut> pageFuzzyQuery(Page<AllocationOut> allocationOutPage, AllocationOutVO allocationOutVO);


    /**
     * PDA端分页查询
     * @param allocationOutPage
     * @param allocationOutVO
     * @return
     */
    Page<AllocationOut>  pageFuzzyQueryPDA(Page<AllocationOut> allocationOutPage, AllocationOutVO allocationOutVO);



    /**
     * 新增调拨计划单
     * @param addAllocationOutDTO
     * @return
     */
    Result addAllocationOutDTO(AddAllocationOutDTO addAllocationOutDTO);


    /**
     * 更新调拨计划单
     * @param updateAllocationOutDTO
     * @return
     */
    Result update(UpdateAllocationOutDTO updateAllocationOutDTO);


    /**
     * 根据单据编号获取调拨出库单
     * @param docNumber
     * @return
     */
    AllocationOut getAllocationOutByDocNumber(String docNumber);


    /**
     * 根据ID获取调拨计划单
     * @param id
     * @return
     */
    AllocationOut getAllocationOutById(Integer id);


    /**
     * 根据流程Id获取调拨计划单
     * @param processInstanceId
     * @return
     */
    AllocationOut getAllocationOutByProcessInstanceId(String processInstanceId);


    Result add(AddAllocationOutDTOAndDetails addAllocationOutDTOAndDetails);

    Result allocationPlanToAllocationOut(AllocationPlan allocationPlan);
}
