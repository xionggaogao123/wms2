package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.AllocationEnterDetails;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.AllocationOutDetails;
import com.huanhong.wms.entity.dto.AddAllocationEnterDetailsDTO;
import com.huanhong.wms.entity.dto.AddAllocationOutDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateAllocationEnterDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateAllocationOutDetailsDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-31
 */
public interface IAllocationEnterDetailsService extends SuperService<AllocationEnterDetails> {

    /**
     * 新增调拨入库明细list
     * @param addAllocationEnterDetailsDTOList
     * @return
     */
    Result addAllocationEnterDetails(List<AddAllocationEnterDetailsDTO> addAllocationEnterDetailsDTOList);

    /**
     * 更新调拨入库明细list
     * @param updateAllocationEnterDetailsDTOList
     * @return
     */
    Result updateAllocationEnterDetails(List<UpdateAllocationEnterDetailsDTO> updateAllocationEnterDetailsDTOList);

    /**
     * 通过Id获取调拨入库明细
     * @param id
     * @return
     */
    AllocationEnterDetails getAllocationEnterDetailsById(Integer id);

    /**
     * 通过到调拨入库单据编号获取调拨入库明细List
     * @param docNum
     * @return
     */
    List<AllocationEnterDetails> getAllocationEnterDetailsListByDocNum(String docNum);
}
