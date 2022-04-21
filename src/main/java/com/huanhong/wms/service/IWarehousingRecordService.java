package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.WarehousingRecord;
import com.huanhong.wms.entity.dto.AddWarehousingRecordDTO;
import com.huanhong.wms.entity.dto.UpdateWarehousingRecordDTO;
import com.huanhong.wms.entity.param.InventoryRecordPage;
import com.huanhong.wms.entity.param.WarehousingDetailPage;
import com.huanhong.wms.entity.vo.InventoryRecordVo;
import com.huanhong.wms.entity.vo.WarehousingDetailVo;
import com.huanhong.wms.entity.vo.WarehousingRecordVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    /**
     * 入库明细表
     * @param page
     * @return
     */
    Result<Page<WarehousingDetailVo>> warehousingDetail(WarehousingDetailPage page);

    /**
     * 入库明细表导出
     * @param page
     * @param request
     * @param response
     */
    void warehousingDetailExport(WarehousingDetailPage page, HttpServletRequest request, HttpServletResponse response);

    /**
     * 库存流水账分页
     * @param page
     * @return
     */
    Result<Page<InventoryRecordVo>> inventoryRecord(InventoryRecordPage page);

    /**
     * 库存流水账导出
     * @param page
     * @param request
     * @param response
     */
    void inventoryRecordExport(InventoryRecordPage page, HttpServletRequest request, HttpServletResponse response);
}
