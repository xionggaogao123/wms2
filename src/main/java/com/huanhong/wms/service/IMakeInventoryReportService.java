package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.MakeInventory;
import com.huanhong.wms.entity.MakeInventoryDetails;
import com.huanhong.wms.entity.MakeInventoryReport;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.dto.AddMakeInventoryDTO;
import com.huanhong.wms.entity.dto.AddMakeInventoryReportDTO;
import com.huanhong.wms.entity.dto.UpdateMakeInventoryDTO;
import com.huanhong.wms.entity.dto.UpdateMakeInventoryReportDTO;
import com.huanhong.wms.entity.vo.MakeInventoryReportVO;
import com.huanhong.wms.entity.vo.MakeInventoryVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-12
 */
public interface IMakeInventoryReportService extends SuperService<MakeInventoryReport> {

    /**
     * 分页查询
     * @param makeInventoryReportPage
     * @param makeInventoryReportVO
     * @return
     */
    Page<MakeInventoryReport> pageFuzzyQuery(Page<MakeInventoryReport> makeInventoryReportPage, MakeInventoryReportVO makeInventoryReportVO);


    /**
     * 新增盘点报告单
     *
     * @param addMakeInventoryReportDTO
     * @return
     */
    Result addMakeInventoryReport(AddMakeInventoryReportDTO addMakeInventoryReportDTO);


    /**
     * 更新盘点报告单
     *
     * @param updateMakeInventoryReportDTO
     * @return
     */
    Result updateMakeInventoryReport(UpdateMakeInventoryReportDTO updateMakeInventoryReportDTO);


    /**
     * 通过ID获取盘点单
     * @param id
     * @return
     */
    MakeInventoryReport getMakeInventoryReportById(Integer id);


    /**
     * 通过盘点计划单据编号和仓库号获取盘点单
     * @param docNum
     * @param warehouseId
     * @return
     */
    MakeInventoryReport getMakeInventoryReportByDocNumAndWarehouse(String docNum, String warehouseId);


    /**
     * 通过盘点单单据编号和仓库号获取盘点单
     * @param reportNum
     * @param warehouseId
     * @return
     */
    MakeInventoryReport  getMakeInventoryReportByReportNumAndWarehouse(String reportNum, String warehouseId);

}
