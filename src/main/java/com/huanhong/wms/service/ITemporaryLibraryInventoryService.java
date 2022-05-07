package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.InventoryDocument;
import com.huanhong.wms.entity.TemporaryLibraryInventory;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.InventoryDocumentVO;
import com.huanhong.wms.entity.vo.TemporaryLibraryInventoryVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-06
 */
public interface ITemporaryLibraryInventoryService extends SuperService<TemporaryLibraryInventory> {

    /**
     * 分页查询
     * @param temporaryLibraryInventoryPage
     * @param temporaryLibraryInventoryVO
     * @return
     */
    Page<TemporaryLibraryInventory> pageFuzzyQuery(Page<TemporaryLibraryInventory> temporaryLibraryInventoryPage, TemporaryLibraryInventoryVO temporaryLibraryInventoryVO);


    /**
     * 新增清点单
     * @param addTemporaryLibraryInventoryDTO
     * @return
     */
    Result addTemporaryLibraryInventory(AddTemporaryLibraryInventoryDTO addTemporaryLibraryInventoryDTO);


    /**
     * 更新清点单
     * @param updateTemporaryLibraryInventoryDTO
     * @return
     */
    Result updateTemporaryLibraryInventory(UpdateTemporaryLibraryInventoryDTO updateTemporaryLibraryInventoryDTO);


    /**
     * 根据id获取清点单信息
     * @param id
     * @return
     */
    TemporaryLibraryInventory getTemporaryLibraryInventoryById(Integer id);


    /**
     * 根据清点单编号和仓库号获取清点单
     * @param documentNumber
     * @param warehouseId
     * @return
     */
    TemporaryLibraryInventory getTemporaryLibraryInventoryByDocumentNumberAndWarehouseId(String documentNumber, String warehouseId);


}
