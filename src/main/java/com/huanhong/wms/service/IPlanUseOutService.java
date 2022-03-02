package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.PlanUseOut;
import com.huanhong.wms.entity.dto.AddPlanUseOutDTO;
import com.huanhong.wms.entity.dto.UpdatePlanUseOutDTO;
import com.huanhong.wms.entity.vo.PlanUseOutVO;

/**
 * <p>
 * 计划领用主表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-02-15
 */
public interface IPlanUseOutService extends SuperService<PlanUseOut> {

    /**
     * 分页查询
     * @param planUseOutPage
     * @param planUseOutVO
     * @return
     */
    Page<PlanUseOut> pageFuzzyQuery(Page<PlanUseOut> planUseOutPage, PlanUseOutVO planUseOutVO);


    /**
     * 新增领料出库单
     * @param addPlanUseOutDTO
     * @return
     */
    Result addPlanUseOut(AddPlanUseOutDTO addPlanUseOutDTO);


    /**
     * 更新领料出库
     * @param updatePlanUseOutDTO
     * @return
     */
    Result updatePlanUseOut(UpdatePlanUseOutDTO updatePlanUseOutDTO);


    /**
     * 根据id获取领料出库单信息
     * @param id
     * @return
     */
    PlanUseOut getPlanUseOutById(Integer id);

    /**
     * 根据单据编号和仓库ID获取单据信息
     * @param docNumber
     * @param WarhouseId
     * @return
     */
    PlanUseOut getPlanUseOutByDocNumAndWarhouseId(String docNumber,String warhouseId);

    /**
     * 根据流程ID获取单据ID
     * @param processInstanceId
     * @return
     */
    PlanUseOut getPlanUseOutByProcessInstanceId(String processInstanceId);

}
