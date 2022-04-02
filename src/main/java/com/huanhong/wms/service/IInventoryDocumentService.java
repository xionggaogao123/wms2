package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.InventoryDocument;
import com.huanhong.wms.entity.dto.AddInventoryDocumentDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryDocumentDTO;
import com.huanhong.wms.entity.vo.InventoryDocumentVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-02-21
 */
public interface IInventoryDocumentService extends SuperService<InventoryDocument> {

    /**
     * 分页查询
     *
     * @param inventoryDocumentPage
     * @param inventoryDocumentVO
     * @return
     */
    Page<InventoryDocument> pageFuzzyQuery(Page<InventoryDocument> inventoryDocumentPage, InventoryDocumentVO inventoryDocumentVO);


    /**
     * 新增清点单
     *
     * @param addInventoryDocumentDTO
     * @return
     */
    Result addInventoryDocument(AddInventoryDocumentDTO addInventoryDocumentDTO);


    /**
     * 更新清点单
     *
     * @param updateInventoryDocumentDTO
     * @return
     */
    Result updateInventoryDocument(UpdateInventoryDocumentDTO updateInventoryDocumentDTO);


    /**
     * 根据id获取清点单信息
     *
     * @param id
     * @return
     */
    InventoryDocument getInventoryDocumentById(Integer id);


    /**
     * 根据清点单编号和仓库号获取清点单
     *
     * @param documentNumber
     * @param warehouseId
     * @return
     */
    InventoryDocument getInventoryDocumentByDocumentNumberAndWarehouseId(String documentNumber, String warehouseId);




}
