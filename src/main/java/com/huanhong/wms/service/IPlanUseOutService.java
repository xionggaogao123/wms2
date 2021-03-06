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
     * PDA端分页查询
     * @param planUseOutPage
     * @param planUseOutVO
     * @return
     */
    Page<PlanUseOut> pageFuzzyQueryPDA(Page<PlanUseOut> planUseOutPage, PlanUseOutVO planUseOutVO);


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
     * @param warhouseId
     * @return
     */
    PlanUseOut getPlanUseOutByDocNumAndWarhouseId(String docNumber,String warhouseId);

    /**
     * 根据流程ID获取单据ID
     * @param processInstanceId
     * @return
     */
    PlanUseOut getPlanUseOutByProcessInstanceId(String processInstanceId);


    Result addOutboundRecordUpdateInventory(PlanUseOut planUseOut);

    /**
     * 检查库存
     * @param planUseOut
     * @return
     */
    Result checkStock(PlanUseOut planUseOut);
    /**
     * 完整审批时-如果批准数量和应出数量不一致--回滚库存
     * 出库明细单据已更新,需要根据批准数量-应出数量=出库数量回滚部分库存并更新出库记录
     *
     */
    Result updateOutboundRecordAndInventory(PlanUseOut planUseOut);

}
