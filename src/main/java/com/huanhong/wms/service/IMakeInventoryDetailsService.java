package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.MakeInventory;
import com.huanhong.wms.entity.MakeInventoryDetails;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.dto.AddMakeInventoryDTO;
import com.huanhong.wms.entity.dto.AddMakeInventoryDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateMakeInventoryDTO;
import com.huanhong.wms.entity.dto.UpdateMakeInventoryDetailsDTO;
import com.huanhong.wms.entity.vo.MakeInventoryVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-11
 */
public interface IMakeInventoryDetailsService extends SuperService<MakeInventoryDetails> {


    /**
     *  新增盘点明细单
     * @param addMakeInventoryDetailsDTOList
     * @return
     */
    Result addMakeInventoryDetails(List<AddMakeInventoryDetailsDTO> addMakeInventoryDetailsDTOList);


    /**
     * 更新盘点明细单
     * @param updateMakeInventoryDetailsDTOList
     * @return
     */
    Result updateMakeInventoryDetails(List<UpdateMakeInventoryDetailsDTO> updateMakeInventoryDetailsDTOList);


    /**
     * 通过ID获取盘点单
     * @param id
     * @return
     */
    MakeInventoryDetails getMakeInventoryDetailsById(Integer id);


    /**
     * 通过单据编号和仓库号获取盘点单
     * @param docNum
     * @param warehouse
     * @return
     */
    List<MakeInventoryDetails> getMakeInventoryDetailsByDocNumAndWarehouseId(String docNum, String warehouseId);

}
