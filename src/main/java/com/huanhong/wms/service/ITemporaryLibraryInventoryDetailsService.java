package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.InventoryDocumentDetails;
import com.huanhong.wms.entity.TemporaryLibraryInventoryDetails;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.dto.AddInventoryDocumentDetailsDTO;
import com.huanhong.wms.entity.dto.AddTemporaryLibraryInventoryDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryDocumentDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryLibraryInventoryDetailsDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-06
 */
public interface ITemporaryLibraryInventoryDetailsService extends SuperService<TemporaryLibraryInventoryDetails> {

    /**
     * 新增临库点验单明细
     * @param addTemporaryLibraryInventoryDetailsDTOList
     * @return
     */
    Result addInventoryDocumentDetailsList(List<AddTemporaryLibraryInventoryDetailsDTO> addTemporaryLibraryInventoryDetailsDTOList);


    /**
     * 更新明细LIST
     * @param updateTemporaryLibraryInventoryDetailsDTOList
     * @return
     */
    Result updateTemporaryLibraryInventoryDetailsList(List<UpdateTemporaryLibraryInventoryDetailsDTO> updateTemporaryLibraryInventoryDetailsDTOList);


    /**
     * 根据原单据编号和仓库获取明细list
     *
     * @param documentNumber
     * @param warehouseId
     * @return
     */
    List<TemporaryLibraryInventoryDetails> getTemporaryLibraryInventoryDetailsListByDocNumberAndWarehosueId(String documentNumber, String warehouseId);


    /**
     * 根据明细ID获取明细信息
     *
     * @param id
     * @return
     */
    TemporaryLibraryInventoryDetails getTemporaryLibraryInventoryDetailsById(int id);


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
    List<TemporaryLibraryInventoryDetails> getTemporaryLibraryInventoryDetailsListByMaterialCodeAndWarehouseId(String materialCoding,String warehouseId);

}
