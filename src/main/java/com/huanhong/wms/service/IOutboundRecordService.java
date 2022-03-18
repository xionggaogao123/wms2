package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.OutboundRecord;
import com.huanhong.wms.entity.dto.AddOutboundRecordDTO;
import com.huanhong.wms.entity.dto.UpdateOutboundRecordDTO;
import com.huanhong.wms.entity.vo.OutboundRecordVO;

import java.util.List;

/**
 * <p>
 * 出库记录 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-07
 */
public interface IOutboundRecordService extends SuperService<OutboundRecord> {


    /**
     * 分页查询
     * @param outboundRecordPage
     * @param outboundRecordVO
     * @return
     */
    Page<OutboundRecord> pageFuzzyQuery(Page<OutboundRecord> outboundRecordPage, OutboundRecordVO outboundRecordVO);


    /**
     * 通过list新增
     * @param addOutboundRecordDTOList
     * @return
     */
    Result addOutboundRecordList(List<AddOutboundRecordDTO> addOutboundRecordDTOList);


    /**
     * 通过单条数据新增出库记录
     * @param addOutboundRecordDTO
     * @return
     */
    Result addOutboundRecord(AddOutboundRecordDTO addOutboundRecordDTO);

    /**
     * 更新出库记录
     * @param updateOutboundRecordDTO
     * @return
     */
    Result updateOutboundRecord(UpdateOutboundRecordDTO updateOutboundRecordDTO);


    /**
     * 根据ID获取出库记录详情
     * @return
     */
    OutboundRecord getOutboundRecordById(Integer id);


    /**
     * 根据原单据编号（出库单）和仓库编号获取出库记录
     * @param docNum
     * @param warehouseId
     * @return
     */
    List<OutboundRecord> getOutboundRecordListByDocNumAndWarehouseId(String docNum,String warehouseId);

    /**
     * 根据原单据编号（出库单）和仓库编号和物料编码查询无苦记录
     * @param docNum
     * @param warehouseId
     * @param materialCoding
     * @return
     */
    OutboundRecord  getOutboundRecordByDocNumAndWarehouseIdAndMaterialCoding(String docNum,String warehouseId,String materialCoding);

}
