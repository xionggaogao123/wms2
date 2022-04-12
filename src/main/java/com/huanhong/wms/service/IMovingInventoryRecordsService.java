package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.MovingInventoryRecords;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.OutboundRecord;
import com.huanhong.wms.entity.dto.AddMovingInventoryRecordsDTO;
import com.huanhong.wms.entity.dto.AddOutboundRecordDTO;
import com.huanhong.wms.entity.dto.UpdateMovingInventoryRecordsDTO;
import com.huanhong.wms.entity.dto.UpdateOutboundRecordDTO;
import com.huanhong.wms.entity.vo.MovingInventoryRecordsVO;
import com.huanhong.wms.entity.vo.OutboundRecordVO;

import java.util.List;

/**
 * <p>
 * 移动库存记录表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-04-11
 */
public interface IMovingInventoryRecordsService extends SuperService<MovingInventoryRecords> {

    /**
     * 分页查询
     * @param movingInventoryRecordsPage
     * @param movingInventoryRecordsVO
     * @return
     */
    Page<MovingInventoryRecords> pageFuzzyQuery(Page<MovingInventoryRecords> movingInventoryRecordsPage, MovingInventoryRecordsVO movingInventoryRecordsVO);


    /**
     * 通过list新增移库记录
     * @param addMovingInventoryRecordsDTOSList
     * @return
     */
    Result addMovingInventoryRecordsList(List<AddMovingInventoryRecordsDTO> addMovingInventoryRecordsDTOSList);


    /**
     * 通过单条数据新增移库记录
     * @param addMovingInventoryRecordsDTO
     * @return
     */
    Result addMovingInventoryRecords(AddMovingInventoryRecordsDTO addMovingInventoryRecordsDTO);


    /**
     * 更新移库记录
     * @param updateMovingInventoryRecordsDTOList
     * @return
     */
    Result updateMovingInventoryRecords(List<UpdateMovingInventoryRecordsDTO> updateMovingInventoryRecordsDTOList);


    /**
     * 根据ID获取移库记录
     * @param id
     * @return
     */
    MovingInventoryRecords getMovingInventoryRecordsById(Integer id);


}
