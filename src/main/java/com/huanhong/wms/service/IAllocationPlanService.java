package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.AllocationPlan;
import com.huanhong.wms.entity.dto.AddAllocationPlanAndDetailsDTO;
import com.huanhong.wms.entity.dto.AddAllocationPlanDTO;
import com.huanhong.wms.entity.dto.UpdateAllocationPlanDTO;
import com.huanhong.wms.entity.param.AllocationDetailPage;
import com.huanhong.wms.entity.vo.AllocationDetailVo;
import com.huanhong.wms.entity.vo.AllocationPlanVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 调拨计划主表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-17
 */
public interface IAllocationPlanService extends SuperService<AllocationPlan> {

    /**
     * 分页查询
     */
    Page<AllocationPlan> pageFuzzyQuery(Page<AllocationPlan> allocationPlanPage, AllocationPlanVO allocationPlanVO);


    /**
     * 新增调拨计划单
     * @param addAllocationPlanDTO
     * @return
     */
    Result addAllocationPlan(AddAllocationPlanDTO addAllocationPlanDTO);


    /**
     * 更新调拨计划单
     * @param updateAllocationPlanDTO
     * @return
     */
    Result updateAllocationPlan(UpdateAllocationPlanDTO updateAllocationPlanDTO);


    /**
     * 根据单据编号获取调拨计划单
     * @param docNumber
     * @return
     */
    AllocationPlan getAllocationPlanByDocNumber(String docNumber);


    /**
     * 根据ID获取调拨计划单
     * @param id
     * @return
     */
    AllocationPlan getAllocationPlanById(Integer id);


    /**
     * 根据流程Id获取调拨计划单
     * @param processInstanceId
     * @return
     */
    AllocationPlan getAllocationPlanByProcessInstanceId(String processInstanceId);

    Result addOutboundRecordUpdateInventory(AllocationPlan allocationPlan);

    Result checkStock(AllocationPlan allocationPlan);

    Result updateOutboundRecordAndInventory(AllocationPlan allocationPlan);

    /**
     * 调拨明细汇总表分页查询
     * @param page
     * @return
     */
    Result<Page<AllocationDetailVo>> allocationDetail(AllocationDetailPage page);

    /**
     * 调拨明细汇总表导出
     * @param page
     * @param request
     * @param response
     */
    void allocationDetailExport(AllocationDetailPage page, HttpServletRequest request, HttpServletResponse response);

    Result add(AddAllocationPlanAndDetailsDTO addAllocationPlanAndDetailsDTO);

    Result delete(Integer id);

    Result deleteByPlanNos(List<String> planNos);
}
