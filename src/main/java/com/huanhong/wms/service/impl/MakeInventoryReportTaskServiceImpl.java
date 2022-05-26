package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.common.units.JsonUtil;
import com.huanhong.wms.bean.Constant;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.MakeInventory;
import com.huanhong.wms.entity.MakeInventoryDetails;
import com.huanhong.wms.entity.MakeInventoryReport;
import com.huanhong.wms.entity.dto.AddMakeInventoryReportDTO;
import com.huanhong.wms.entity.dto.AddMakeInventoryReportDetailsDTO;
import com.huanhong.wms.mapper.MakeInventoryMapper;
import com.huanhong.wms.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author wang
 * @date 2022/5/23 18:22
 * 报表生成service层的实现
 */
@Slf4j
@Service
public class MakeInventoryReportTaskServiceImpl implements MakeInventoryReportTaskService {

    @Resource
    private IMakeInventoryService makeInventoryService;

    @Resource
    private MakeInventoryMapper makeInventoryMapper;

    @Resource
    private IMakeInventoryReportService makeInventoryReportService;

    @Resource
    private IMakeInventoryDetailsService makeInventoryDetailService;

    @Resource
    private IMakeInventoryReportDetailsService makeInventoryReportDetailService;

    @Override
    public void makeInventoryReportCreate() {
        //查询盘点报告表中的数据（盘点状态为:待盘点0）
        QueryWrapper<MakeInventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("check_status", Constant.CHECK_STATUS_0);
        queryWrapper.eq("is_reported", 0);
        List<MakeInventory> makeInventoryList = makeInventoryMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(makeInventoryList)) {
            log.info("***** 盘点报告表中的没有待盘点数据 *****");
            return;
        }
        //遍历数据判断盘点时间(创建盘点计划的时候需要在当前时间之前)
        makeInventoryList.forEach(make -> {
            if (make.getStartTime().isBefore(LocalDateTime.now())) {
//                log.info("***** 开始修改盘点报告表盘点状态 *****");
                make.setIsReported(Constant.CHECK_STATUS_1);
                makeInventoryMapper.updateById(make);
                log.info("***** 修改修改完的盘点报表编号:{},报表状态:{} *****", make.getDocumentNumber(), make.getCheckStatus());
                log.info("***** 开始添加盘点报表主表 *****");
                Result result = addMakeInventoryReport(make);
                log.info("***** 开始添加盘点报表子表 *****");
                addMakeInventoryReportDetail(make, result);
            }
        });
    }

    /**
     * 添加盘点报表子表
     *
     * @param make 盘点计划数据
     */
    private void addMakeInventoryReportDetail(MakeInventory make, Result result) {
        //获取主表数据添加子表
        MakeInventoryReport makeInventoryReport = (MakeInventoryReport) result.getData();
        List<MakeInventoryDetails> details = makeInventoryDetailService.findByDocumentNumber(make.getDocumentNumber());
        log.info("***** 获取物料明细数据为:{} *****", JsonUtil.obj2String(details));
        List<AddMakeInventoryReportDetailsDTO> reportDetails = BeanUtil.copyToList(details, AddMakeInventoryReportDetailsDTO.class);
        reportDetails.forEach(inventoryReportDetail -> {
            inventoryReportDetail.setReportNumber(makeInventoryReport.getReportNumber());
            inventoryReportDetail.setWarehouseId(makeInventoryReport.getWarehouseId());
        });
        log.info("***** 添加盘点报表子表数据:{} *****", JsonUtil.obj2String(reportDetails));
        try {
            makeInventoryReportDetailService.addMakeInventoryReportDetailsList(reportDetails);
        } catch (Exception e) {
            log.error("***** 添加盘点报表子表异常:{} *****", e.getMessage());
        }
    }

    /**
     * 添加盘点报表主表
     *
     * @param make 盘点计划数据
     */
    private Result addMakeInventoryReport(MakeInventory make) {
        AddMakeInventoryReportDTO addMakeInventoryReportDTO = new AddMakeInventoryReportDTO();
        BeanUtil.copyProperties(make, addMakeInventoryReportDTO);
        log.info("***** 添加盘点报表主表数据:{} *****", JsonUtil.obj2String(addMakeInventoryReportDTO));
        try {
            return makeInventoryReportService.addMakeInventoryReport(addMakeInventoryReportDTO);
        } catch (Exception e) {
            log.error("***** 添加盘点报表主表异常:{} *****", e.getMessage());
        }
        return null;
    }
}
