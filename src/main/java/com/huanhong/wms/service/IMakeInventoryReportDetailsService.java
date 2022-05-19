package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.InventoryDocumentDetails;
import com.huanhong.wms.entity.MakeInventoryReportDetails;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.dto.AddInventoryDocumentDetailsDTO;
import com.huanhong.wms.entity.dto.AddMakeInventoryReportDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateInventoryDocumentDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateMakeInventoryReportDetailsDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-12
 */
public interface IMakeInventoryReportDetailsService extends SuperService<MakeInventoryReportDetails> {

    /**
     * 新增盘点报告明细
     * @param addMakeInventoryReportDetailsDTOList
     * @return
     */
    Result addMakeInventoryReportDetailsList(List<AddMakeInventoryReportDetailsDTO> addMakeInventoryReportDetailsDTOList);


    /**
     * 更新盘点报告明细List
     * @param updateMakeInventoryReportDetailsDTOList
     * @return
     */
    Result updateMakeInventoryReportDetailsList(List<UpdateMakeInventoryReportDetailsDTO> updateMakeInventoryReportDetailsDTOList);


    /**
     * 根据原单据编号和仓库获取明细list
     * @param documentNumber
     * @param warehouseId
     * @return
     */
    List<MakeInventoryReportDetails> getMakeInventoryReportDetailsByDocNumberAndWarehosueId(String documentNumber, String warehouseId);


    /**
     * 根据明细ID获取明细信息
     *
     * @param id
     * @return
     */
    MakeInventoryReportDetails getMakeInventoryReportDetailsById(int id);


    /**
     * 根据物料编码、批次、货位编码获取盘点单明细
     * @param materialCoding
     * @param batch
     * @param cargoSpaceId
     * @return
     */
    MakeInventoryReportDetails getMakeInventoryReportDetailsByMaterialCodingAndBatchAndCargoSpaceId(String materialCoding,String batch,String cargoSpaceId);

    /**
     * 通过单据编号和仓库号获取未完成盘点单明细数量
     * @param docNum
     * @param warehouseId
     * @return
     */
    Integer getMakeInventoryReportDetailsByDocNumAndWarehouseIdNotComplete(String docNum, String warehouseId);


}
