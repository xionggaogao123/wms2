package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.PlanUseOut;
import com.huanhong.wms.entity.WarehouseManager;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.dto.AddPlanUseOutDTO;
import com.huanhong.wms.entity.dto.AddWarehouseManagerDTO;
import com.huanhong.wms.entity.dto.UpdatePlanUseOutDTO;
import com.huanhong.wms.entity.dto.UpdateWarehouseManagerDTO;
import com.huanhong.wms.entity.vo.PlanUseOutVO;
import com.huanhong.wms.entity.vo.WarehouseManagerVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-04-13
 */
public interface IWarehouseManagerService extends SuperService<WarehouseManager> {


    /**
     * 分页查询
     * @param warehouseManagertPage
     * @param warehouseManagerVO
     * @return
     */
    Page<WarehouseManager> pageFuzzyQuery(Page<WarehouseManager> warehouseManagertPage, WarehouseManagerVO warehouseManagerVO);

    /**
     * 新增仓库管理员
     * @param addWarehouseManagerDTO
     * @return
     */
    Result addWarehouseManager(AddWarehouseManagerDTO addWarehouseManagerDTO);


    /**
     * 更新仓库管理员
     * @param updateWarehouseManagerDTO
     * @return
     */
    Result updateWarehouseManager(UpdateWarehouseManagerDTO updateWarehouseManagerDTO);


    /**
     * 根据id获取仓库管理员信息
     * @param id
     * @return
     */
    WarehouseManager getWarehouseManagerById(Integer id);

    /**
     * 根据登录账号获取此用户管理的仓库
     * @param loginName
     * @return
     */
    List<WarehouseManager> getWarehouseManagerListByLoginName(String loginName);

}
