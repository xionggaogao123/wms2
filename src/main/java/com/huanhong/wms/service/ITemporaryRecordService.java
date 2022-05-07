package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.OutboundRecord;
import com.huanhong.wms.entity.TemporaryRecord;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.dto.AddOutboundRecordDTO;
import com.huanhong.wms.entity.dto.AddTemporaryRecordDTO;
import com.huanhong.wms.entity.dto.UpdateOutboundRecordDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryRecordDTO;
import com.huanhong.wms.entity.vo.OutboundRecordVO;
import com.huanhong.wms.entity.vo.TemporaryRecordVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-05
 */
public interface ITemporaryRecordService extends SuperService<TemporaryRecord> {

    /**
     * 分页查询
     * @param temporaryRecordPage
     * @param temporaryRecordVO
     * @return
     */
    Page<TemporaryRecord> pageFuzzyQuery(Page<TemporaryRecord> temporaryRecordPage, TemporaryRecordVO temporaryRecordVO);


    /**
     * 通过list新增
     * @param addTemporaryRecordDTOList
     * @return
     */
    Result addTemporaryRecordList(List<AddTemporaryRecordDTO> addTemporaryRecordDTOList);


    /**
     * 通过单条数据新增出库记录
     * @param addTemporaryRecordDTO
     * @return
     */
    Result addTemporaryRecord(AddTemporaryRecordDTO addTemporaryRecordDTO);


    /**
     * 更新出库记录
     * @param updateTemporaryRecordDTO
     * @return
     */
    Result updateTemporaryRecord(UpdateTemporaryRecordDTO updateTemporaryRecordDTO);


    /**
     * 根据ID获取出库记录详情
     * @param id
     * @return
     */
    TemporaryRecord getTemporaryRecordById(Integer id);


    /**
     * 根据原单据编号（出库单）和仓库编号获取出库记录
     *
     * @param docNum
     * @param warehouseId
     * @return
     */
    List<TemporaryRecord> getTemporaryRecordListByDocNumAndWarehouseId(String docNum, String warehouseId);

    /**
     * 根据原单据编号（出库单）和仓库编号和物料编码查询出库记录
     *
     * @param docNum
     * @param warehouseId
     * @param materialCoding
     * @return
     */
    List<TemporaryRecord> getTemporaryRecordByDocNumAndWarehouseIdAndMaterialCoding(String docNum, String warehouseId, String materialCoding);


    /**
     * 根据原单据编号 物料编码货位批次检索唯一一条出库记录
     * @param docNum
     * @param cargoSpace
     * @param materialCoding
     * @param batch
     * @return
     */
    TemporaryRecord getTemporaryRecordByDocNumAndCargoSpaceAndMaterialCodingAndBatch(String docNum, String cargoSpace, String materialCoding, String batch);

}
