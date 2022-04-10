package com.huanhong.wms.service;

import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ArrivalVerificationDetails;
import com.huanhong.wms.entity.InventoryDocumentDetails;
import com.huanhong.wms.entity.dto.AddArrivalVerificationDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateArrivalVerificationDetailsDTO;

import java.util.List;

/**
 * <p>
 * 到货检验明细表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-23
 */
public interface IArrivalVerificationDetailsService extends SuperService<ArrivalVerificationDetails> {

    /**
     * 新增到货检验明细表
     * @param addArrivalVerificationDetailsDTOList
     * @return
     */
    Result addArrivalVerificationDetails(List<AddArrivalVerificationDetailsDTO> addArrivalVerificationDetailsDTOList);

    /**
     * 更新采购计划明细表
     * @param updateArrivalVerificationDetailsDTOList
     * @return
     */
    Result updateArrivalVerificationDetails(List<UpdateArrivalVerificationDetailsDTO> updateArrivalVerificationDetailsDTOList);

    /**
     * 通过Id获取到货检验单
     * @param id
     * @return
     */
    ArrivalVerificationDetails getArrivalVerificationDetailsById(Integer id);

    /**
     * 通过到货检验单据编号和仓库编号获取明细list
     * @param docNum
     * @param warehouseId
     * @return
     */
    List<ArrivalVerificationDetails> getArrivalVerificationDetailsByDocNumAndWarehouseId(String docNum, String warehouseId);


    /**
     * 根据物料编码获取未完成单据单据
     * @param materialCoding
     * @param warehouseId
     * @return
     */
    List<ArrivalVerificationDetails> getArrivalVerificationDetailsListByMaterialCodeAndWarehouseId(String materialCoding, String warehouseId);



}
