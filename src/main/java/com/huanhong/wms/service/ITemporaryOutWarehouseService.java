package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.PlanUseOut;
import com.huanhong.wms.entity.TemporaryOutWarehouse;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.entity.dto.AddPlanUseOutDTO;
import com.huanhong.wms.entity.dto.AddTemporaryOutWarehouseDTO;
import com.huanhong.wms.entity.dto.UpdatePlanUseOutDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryOutWarehouseDTO;
import com.huanhong.wms.entity.vo.PlanUseOutVO;
import com.huanhong.wms.entity.vo.TemporaryOutWarehouseVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-05
 */
public interface ITemporaryOutWarehouseService extends SuperService<TemporaryOutWarehouse> {

    /**
     * 分页查询
     * @param temporaryOutWarehousePage
     * @param temporaryOutWarehouseVO
     * @return
     */
    Page<TemporaryOutWarehouse> pageFuzzyQuery(Page<TemporaryOutWarehouse> temporaryOutWarehousePage, TemporaryOutWarehouseVO temporaryOutWarehouseVO);


    /**
     * 新增临库出库表
     * @param addTemporaryOutWarehouseDTO
     * @return
     */
    Result addTemporaryOutWarehouse(AddTemporaryOutWarehouseDTO addTemporaryOutWarehouseDTO);


    /**
     * 更新临库出库表
     * @param updateTemporaryOutWarehouseDTO
     * @return
     */
    Result updateTemporaryOutWarehouse(UpdateTemporaryOutWarehouseDTO updateTemporaryOutWarehouseDTO);


    /**
     * 根据id获取临库出库信息
     * @param id
     * @return
     */
     TemporaryOutWarehouse getTemporaryOutWarehouseById(Integer id);


    /**
     * 根据单据编号和仓库ID获取单据信息
     * @param docNumber
     * @param warhouseId
     * @return
     */
    TemporaryOutWarehouse getTemporaryOutWarehouseByDocNumAndWarhouseId(String docNumber,String warhouseId);

    /**
     * 根据流程ID获取单据ID
     * @param processInstanceId
     * @return
     */
    TemporaryOutWarehouse getTemporaryOutWarehouseByProcessInstanceId(String processInstanceId);


    Result addTemporaryRecordUpdateInventory(TemporaryOutWarehouse temporaryOutWarehouse, LoginUser loginUser);

//
//    /**
//     * 完整审批时-如果批准数量和应出数量不一致--回滚库存
//     * 出库明细单据已更新,需要根据批准数量-应出数量=出库数量回滚部分库存并更新出库记录
//     *
//     */
//    Result updateTemporaryRecordAndInventory(TemporaryOutWarehouse temporaryOutWarehouse);


    /**
     * 检查库存
     * @param planUseOut
     * @return
     */
    Result checkStock(PlanUseOut planUseOut);

}
