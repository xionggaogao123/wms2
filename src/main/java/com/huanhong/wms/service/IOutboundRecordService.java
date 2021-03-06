package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.OutboundRecord;
import com.huanhong.wms.entity.dto.AddOutboundRecordDTO;
import com.huanhong.wms.entity.dto.UpdateOutboundRecordDTO;
import com.huanhong.wms.entity.param.MaterialOutInParam;
import com.huanhong.wms.entity.param.OutboundDetailPage;
import com.huanhong.wms.entity.vo.OutboundDetailVo;
import com.huanhong.wms.entity.vo.OutboundRecordVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
     *
     * @param outboundRecordPage
     * @param outboundRecordVO
     * @return
     */
    Page<OutboundRecord> pageFuzzyQuery(Page<OutboundRecord> outboundRecordPage, OutboundRecordVO outboundRecordVO);


    /**
     * 通过list新增
     *
     * @param addOutboundRecordDTOList
     * @return
     */
    Result addOutboundRecordList(List<AddOutboundRecordDTO> addOutboundRecordDTOList);


    /**
     * 通过单条数据新增出库记录
     *
     * @param addOutboundRecordDTO
     * @return
     */
    Result addOutboundRecord(AddOutboundRecordDTO addOutboundRecordDTO);

    /**
     * 更新出库记录
     *
     * @param updateOutboundRecordDTO
     * @return
     */
    Result updateOutboundRecord(UpdateOutboundRecordDTO updateOutboundRecordDTO);


    /**
     * 根据ID获取出库记录详情
     *
     * @return
     */
    OutboundRecord getOutboundRecordById(Integer id);


    /**
     * 根据原单据编号（出库单）和仓库编号获取出库记录
     *
     * @param docNum
     * @param warehouseId
     * @return
     */
    List<OutboundRecord> getOutboundRecordListByDocNumAndWarehouseId(String docNum, String warehouseId);

    /**
     * 根据原单据编号（出库单）和仓库编号和物料编码查询出库记录
     *
     * @param docNum
     * @param warehouseId
     * @param materialCoding
     * @return
     */
    List<OutboundRecord> getOutboundRecordByDocNumAndWarehouseIdAndMaterialCoding(String docNum, String warehouseId, String materialCoding);


    /**
     * 根据原单据编号 物料编码货位批次检索唯一一条出库记录
     * @param docNum
     * @param cargoSpace
     * @param materialCoding
     * @param batch
     * @return
     */
    OutboundRecord getOutboundRecordByDocNumAndCargoSpaceAndMaterialCodingAndBatch(String docNum, String cargoSpace, String materialCoding, String batch);

    /**
     * 领料出库明细表_查询
     *
     * @param page
     * @return
     */
    Result<Page<OutboundDetailVo>> outboundDetail(OutboundDetailPage page);

    /**
     * 领料出库明细表_导出
     *
     * @param page
     * @param request
     * @param response
     */
    void outboundDetailExport(OutboundDetailPage page, HttpServletRequest request, HttpServletResponse response);


    Result<Object> getTheTrendOfWarehouseInboundAndOutbound(MaterialOutInParam param);


    Result<Object> getStatisticalAnalysisOfInboundAndOutboundAmount(MaterialOutInParam param);
}
