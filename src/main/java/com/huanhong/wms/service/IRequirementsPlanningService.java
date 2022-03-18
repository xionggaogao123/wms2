package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.RequirementsPlanning;
import com.huanhong.wms.entity.dto.AddRequirementsPlanningDTO;
import com.huanhong.wms.entity.dto.UpdateRequirementsPlanningDTO;
import com.huanhong.wms.entity.vo.RequirementsPlanningVO;

/**
 * <p>
 * 需求计划表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-16
 */
public interface IRequirementsPlanningService extends SuperService<RequirementsPlanning> {


    /**
     * 分页查询
     * @param requirementsPlanningPage
     * @param requirementsPlanningVO
     * @return
     */
    Page<RequirementsPlanning> pageFuzzyQuery(Page<RequirementsPlanning> requirementsPlanningPage, RequirementsPlanningVO requirementsPlanningVO);

    /**
     * 新增需求计划表
     * @param addRequirementsPlanningDTO
     * @return
     */
    Result addRequirementsPlanning(AddRequirementsPlanningDTO addRequirementsPlanningDTO);


    /**
     * 更新需求计划表
     * @param updateRequirementsPlanningDTO
     * @return
     */
    Result updateRequirementsPlanning(UpdateRequirementsPlanningDTO updateRequirementsPlanningDTO);

    /**
     * 根据id获取需求计划表信息
     * @param id
     * @return
     */
    RequirementsPlanning getRequirementsPlanningById(Integer id);

    /**
     * 根据单据编号和仓库编号获取需求计划
     * @param DocNum
     * @param warehouseId
     * @return
     */
    RequirementsPlanning getRequirementsPlanningByDocNumAndWarehouseId(String DocNum,String warehouseId);

    /**
     * 根据流程Id获取需求计划单
     * @param processInstanceId
     * @return
     */
    RequirementsPlanning getRequirementsPlanningByProcessInstanceId(String processInstanceId);
}
