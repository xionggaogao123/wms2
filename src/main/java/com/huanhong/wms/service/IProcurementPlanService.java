package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ProcurementPlan;
import com.huanhong.wms.entity.dto.AddProcurementPlanDTO;
import com.huanhong.wms.entity.dto.UpdateProcurementPlanDTO;
import com.huanhong.wms.entity.param.DeptMaterialParam;
import com.huanhong.wms.entity.vo.ProcurementPlanVO;

/**
 * <p>
 * 采购计划主表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-21
 */
public interface IProcurementPlanService extends SuperService<ProcurementPlan> {

    /**
     * 分页查询
     * @param procurementPlanPage
     * @param procurementPlanVO
     * @return
     */
    Page<ProcurementPlan> pageFuzzyQuery(Page<ProcurementPlan> procurementPlanPage, ProcurementPlanVO procurementPlanVO);

    /**
     * 新增采购计划表
     * @param addProcurementPlanDTO
     * @return
     */
    Result addProcurementPlan(AddProcurementPlanDTO addProcurementPlanDTO);


    /**
     * 更新采购计划表
     * @param updateProcurementPlanDTO
     * @return
     */
    Result updateProcurementPlan(UpdateProcurementPlanDTO updateProcurementPlanDTO);

    /**
     * 根据id获取采购计划表信息
     * @param id
     * @return
     */
    ProcurementPlan getProcurementPlanById(Integer id);

    /**
     * 根据单据编号和仓库编号获取采购计划
     * @param DocNum
     * @param warehouseId
     * @return
     */
    ProcurementPlan getProcurementPlanByDocNumAndWarehouseId(String DocNum,String warehouseId);

    /**
     * 根据流程Id获取采购计划单
     * @param processInstanceId
     * @return
     */
    ProcurementPlan getProcurementPlanByProcessInstanceId(String processInstanceId);

    Result<Object> getProcurementPlanFrequencyAndQuantity(DeptMaterialParam param);
}
