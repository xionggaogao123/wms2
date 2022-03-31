package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.AllocationEnter;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.AllocationOut;
import com.huanhong.wms.entity.dto.AddAllocationEnterDTO;
import com.huanhong.wms.entity.dto.AddAllocationOutDTO;
import com.huanhong.wms.entity.dto.UpdateAllocationEnterDTO;
import com.huanhong.wms.entity.dto.UpdateAllocationOutDTO;
import com.huanhong.wms.entity.vo.AllocationEnterVO;
import com.huanhong.wms.entity.vo.AllocationOutVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-31
 */
public interface IAllocationEnterService extends SuperService<AllocationEnter> {

    /**
     * 分页查询
     */
    Page<AllocationEnter> pageFuzzyQuery(Page<AllocationEnter> allocationEnterPage, AllocationEnterVO allocationEnterVO);


    /**
     * 新增调拨入库单
     * @param addAllocationEnterDTO
     * @return
     */
    Result addAllocationEnterDTO(AddAllocationEnterDTO addAllocationEnterDTO);


    /**
     * 更新调拨入库单
     * @param updateAllocationEnterDTO
     * @return
     */
    Result update(UpdateAllocationEnterDTO updateAllocationEnterDTO);


    /**
     * 根据单据编号获取调拨入库单
     * @param docNumber
     * @return
     */
    AllocationEnter getAllocationEnterByDocNumber(String docNumber);


    /**
     * 根据ID获取调调拨入库单
     * @param id
     * @return
     */
    AllocationEnter getAllocationEnterById(Integer id);


    /**
     * 根据流程Id获取调拨计划单
     * @param processInstanceId
     * @return
     */
    AllocationEnter getAllocationEnterByProcessInstanceId(String processInstanceId);
}
