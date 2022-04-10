package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.EnterWarehouseDetails;
import com.huanhong.wms.entity.InventoryDocument;
import com.huanhong.wms.entity.InventoryDocumentDetails;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.dto.AddEnterWarehouseDetailsDTO;
import com.huanhong.wms.entity.dto.AddInventoryDocumentDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateEnterWarehouseDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryDocumentDetailsDTO;

import java.util.List;

/**
 * <p>
 * 清点单 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-04-02
 */
public interface IInventoryDocumentDetailsService extends SuperService<InventoryDocumentDetails> {

    /**
     * 新增点验单明细
     * @param addInventoryDocumentDetailsDTOList
     * @return
     */
    Result addInventoryDocumentDetailsLis(List<AddInventoryDocumentDetailsDTO> addInventoryDocumentDetailsDTOList);


    /**
     * 更新明细LIST
     *
     * @param updateInventoryDocumentDetailsDTOList
     * @return
     */
    Result updateInventoryDocumentDetailsList(List<UpdateInventoryDocumentDetailsDTO> updateInventoryDocumentDetailsDTOList);


    /**
     * 根据原单据编号和仓库获取明细list
     *
     * @param documentNumber
     * @param warehouse
     * @return
     */
    List<InventoryDocumentDetails> getInventoryDocumentDetailsListByDocNumberAndWarehosue(String documentNumber, String warehouse);


    /**
     * 根据明细ID获取明细信息
     *
     * @param id
     * @return
     */
    InventoryDocumentDetails getInventoryDocumentDetailsById(int id);


    /**
     * 根据清点单编号和仓库号获取明细数量
     * @param documentNumber
     * @param warehouseId
     * @param status 0-未完成 1-已完成
     * @return
     */
    Integer getCompleteNum(String documentNumber, String warehouseId,Integer status);


    /**
     * 根据物料编码获取未完成单据单据
     * @param materialCoding
     * @param warehouseId
     * @return
     */
    List<InventoryDocumentDetails> getInventoryDocumentDetailsListByMaterialCodeAndWarehouseId(String materialCoding,String warehouseId);


}
