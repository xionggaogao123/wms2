package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.PlanUseOut;
import com.huanhong.wms.entity.WarehousingRecord;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.dto.AddPlanUseOutDTO;
import com.huanhong.wms.entity.dto.AddWarehousingRecordDTO;
import com.huanhong.wms.entity.dto.UpdatePlanUseOutDTO;
import com.huanhong.wms.entity.dto.UpdateWarehousingRecordDTO;
import com.huanhong.wms.entity.vo.PlanUseOutVO;
import com.huanhong.wms.entity.vo.WarehousingRecordVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-04-07
 */
public interface IWarehousingRecordService extends SuperService<WarehousingRecord> {

    /**
     * 分页查询
     * @param warehousingRecordPage
     * @param warehousingRecordVO
     * @return
     */
    Page<WarehousingRecord> pageFuzzyQuery(Page<WarehousingRecord> warehousingRecordPage, WarehousingRecordVO warehousingRecordVO);


    /**
     * 新增入库记录单
     * @param addWarehousingRecordDTO
     * @return
     */
    Result addWarehousingRecord(AddWarehousingRecordDTO addWarehousingRecordDTO);


    /**
     * 更新入库记录单
     * @param updateWarehousingRecordDTO
     * @return
     */
    Result updateWarehousingRecord(UpdateWarehousingRecordDTO updateWarehousingRecordDTO);



    /**
     * 根据id获取入库记录单信息
     * @param id
     * @return
     */
    WarehousingRecord getWarehousingRecordById(Integer id);


    /**
     * 根据单据编号和仓库ID获取单据信息
     * @param docNumber
     * @param warhouseId
     * @return
     */
    List<WarehousingRecord> getWarehousingRecordByDocNumAndWarhouseId(String docNumber, String warehouseId);


}
