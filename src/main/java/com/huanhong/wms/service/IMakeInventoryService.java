package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.MakeInventory;
import com.huanhong.wms.entity.dto.AddMakeInventoryDTO;
import com.huanhong.wms.entity.dto.UpdateMakeInventoryDTO;
import com.huanhong.wms.entity.vo.MakeInventoryVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-02-28
 */
public interface IMakeInventoryService extends SuperService<MakeInventory> {


    /**
     * 分页查询
     * @param makeInventoryPage
     * @param makeInventoryVO
     * @return
     */
    Page<MakeInventory> pageFuzzyQuery(Page<MakeInventory> makeInventoryPage, MakeInventoryVO makeInventoryVO);


    /**
     * 新增盘点单
     * @param addMakeInventoryDTO
     * @return
     */
    Result addMakeInventory(AddMakeInventoryDTO addMakeInventoryDTO);


    /**
     * 更新盘点单
     * @param updateMakeInventoryDTO
     * @return
     */
    Result updateMakeInventory(UpdateMakeInventoryDTO updateMakeInventoryDTO);


    /**
     * 通过ID获取盘点单
     * @param id
     * @return
     */
    MakeInventory getMakeInventoryById(Integer id);


    /**
     * 通过单据编号和仓库号获取盘点单
     * @param docNum
     * @param warehouse
     * @return
     */
    MakeInventory getMakeInventoryByDocNumAndWarehouse(String docNum,String warehouse);

}
