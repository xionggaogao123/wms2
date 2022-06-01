package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.common.exception.ServiceException;
import com.huanhong.common.units.JsonUtil;
import com.huanhong.wms.bean.Constant;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.dto.request.UpdateMakeInventoryReportOneRequest;
import com.huanhong.wms.dto.request.UpdateMakeInventoryReportRequest;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.mapper.InventoryInformationMapper;
import com.huanhong.wms.mapper.MakeInventoryMapper;
import com.huanhong.wms.mapper.MakeInventoryReportDetailsMapper;
import com.huanhong.wms.mapper.MakeInventoryReportMapper;
import com.huanhong.wms.service.MakeInventoryReportV1Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author wang
 * @date 2022/5/28 19:51
 */
@Slf4j
@Service
public class MakeInventoryReportV1ServiceImpl implements MakeInventoryReportV1Service {


    @Resource
    private MakeInventoryReportMapper makeInventoryReportMapper;

    @Resource
    private MakeInventoryMapper makeInventoryMapper;

    @Resource
    private MakeInventoryReportDetailsMapper makeInventoryReportDetailsMapper;

    @Resource
    private InventoryInformationMapper inventoryInformationMapper;

    @Override
    public Result update(UpdateMakeInventoryReportRequest request) {
        MakeInventoryReport makeInventoryReport1 = request.getMakeInventoryReport();
        QueryWrapper<MakeInventoryReport> reportQueryWrapper = new QueryWrapper<>();
        reportQueryWrapper.eq("document_number", makeInventoryReport1.getDocumentNumber());
        //盘点报告是否存在
        MakeInventoryReport makeInventoryReport = makeInventoryReportMapper.selectOne(reportQueryWrapper);
        if (makeInventoryReport == null) {
            log.error("PDA传入的数据为:{}", JsonUtil.obj2String(request));
            throw new ServiceException(500, "盘点报告不存在");
        }
        if (makeInventoryReport.getCheckStatus() == 1) {
            throw new ServiceException(500, "盘点以完成");
        }
        //判断盘点状态是否 盘点完成
        AtomicInteger number = new AtomicInteger();
        List<MakeInventoryReportDetails> makeInventoryReportDetails = request.getMakeInventoryReportDetails();
        log.info("参数为:{}", JsonUtil.obj2String(makeInventoryReportDetails));
        makeInventoryReportDetails.forEach(details -> {
            //盘点是否全部盘点完成 盘点状态: 0-待盘点，1-一致 ，2-盘盈 ，3-盘亏
            if (details.getCheckStatusDetails() == 0) {
                number.getAndIncrement();
            }
        });
        if (number.get() == 0) {
            makeInventoryReport1.setCheckStatus(1);
            makeInventoryReportMapper.updateById(makeInventoryReport1);
            String reportNumber = makeInventoryReport1.getDocumentNumber();
            QueryWrapper<MakeInventory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("document_number", reportNumber);
            MakeInventory makeInventory = makeInventoryMapper.selectOne(queryWrapper);
            makeInventory.setCheckStatus(1);
            makeInventoryMapper.updateById(makeInventory);
        }
        return Result.success("盘点报告更新成功");
    }

    @Override
    public Result updateOne(UpdateMakeInventoryReportOneRequest request) {
        //盘点数量
        BigDecimal checkCredit = new BigDecimal(request.getCheckCredit());
        //稽核数量
        BigDecimal auditCredit = new BigDecimal(request.getAuditCredit());
        //添加快照数量
        addSnapShoot(checkCredit, auditCredit, request);
        return Result.success("盘点报告子表数据更新成功");
    }

    private void addSnapShoot(BigDecimal checkCredit, BigDecimal auditCredit, UpdateMakeInventoryReportOneRequest request) {
        //查询库存信息更新数据的快照
        QueryWrapper<InventoryInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("batch", request.getBatch());
        queryWrapper.eq("material_coding", request.getWarehouseId());
        queryWrapper.eq("warehouse_id", request.getWarehouseId());
        InventoryInformation inventoryInformation = inventoryInformationMapper.selectOne(queryWrapper);
        //有库存信息时更新快照
        if (inventoryInformation != null) {
            //库存数量
            BigDecimal inventoryCredit = new BigDecimal(inventoryInformation.getInventoryCredit());
            //单价
            BigDecimal unitPrice = inventoryInformation.getUnitPrice();
            //盘点数量不等于0
            if (!checkCredit.equals(BigDecimal.ZERO)) {
                //更新盘点快照信息
                request.setCheckSnapShoot(inventoryInformation.getInventoryCredit());
                request.setCheckSnapShootTime(LocalDateTime.now());
                MakeInventoryReportDetails makeInventoryReportDetails = new MakeInventoryReportDetails();
                BeanUtil.copyProperties(request, makeInventoryReportDetails);
                int i = inventoryCredit.compareTo(checkCredit);
                if (i == 0) {
                    //库存数量和盘点数据相等(一致)
                    request.setCheckStatusDetails(1);
                }
                if (i > 0) {
                    //库存数量大于盘点数据(盘盈)
                    request.setCheckStatusDetails(2);
                }
                if (i < 0) {
                    //库存数量小于盘点数据(盘亏)
                    request.setCheckStatusDetails(3);
                }
                makeInventoryReportDetailsMapper.updateById(makeInventoryReportDetails);
            }
            //稽核数量不等于0
            if (!auditCredit.equals(BigDecimal.ZERO)) {
                //更新稽核快照信息
                request.setAuditSnapShoot(inventoryInformation.getInventoryCredit());
                request.setAuditSnapShootTime(LocalDateTime.now());
                //盘点稽核数量和稽核快照数量大小比较
                int i = inventoryCredit.compareTo(auditCredit);
                if (i == 0) {
                    //库存数量和稽核数量相等(一致)
                    request.setCheckStatusDetails(1);
                    request.setFinalCredit(0.00);
                }
                if (i > 0) {
                    //库存数量大于稽核数量(盘盈)
                    request.setCheckStatusDetails(2);
                    //计算盘盈数量
                    BigDecimal finalCredit = inventoryCredit.subtract(auditCredit);
                    //盘盈金额
                    BigDecimal finalAmounts = finalCredit.multiply(unitPrice);
                    request.setFinalCredit(finalCredit.doubleValue());
                    request.setFinalAmounts(finalAmounts);
                }
                if (i < 0) {
                    //库存数量小于稽核数量(盘亏)
                    request.setCheckStatusDetails(3);
                    //计算盘亏数量
                    BigDecimal finalCredit = auditCredit.subtract(inventoryCredit);
                    //盘盈金额
                    BigDecimal finalAmounts = finalCredit.multiply(unitPrice);
                    request.setFinalCredit(finalCredit.doubleValue());
                    request.setFinalAmounts(finalAmounts);
                }
                MakeInventoryReportDetails makeInventoryReportDetails = new MakeInventoryReportDetails();
                BeanUtil.copyProperties(request, makeInventoryReportDetails);
                makeInventoryReportDetailsMapper.updateById(makeInventoryReportDetails);
            }
        } else {
            //无库存信息时新增快照
            //库存数量 为0.00
            //单价
            BigDecimal unitPrice = request.getUnitPrice();
            //盘点数量不等于0
            if (!checkCredit.equals(BigDecimal.ZERO)) {
                //更新盘点快照信息
                request.setCheckSnapShoot(0.00);
                request.setCheckSnapShootTime(LocalDateTime.now());
                MakeInventoryReportDetails makeInventoryReportDetails = new MakeInventoryReportDetails();
                BeanUtil.copyProperties(request, makeInventoryReportDetails);
                makeInventoryReportDetails.setCheckStatusDetails(2);
                makeInventoryReportDetailsMapper.updateById(makeInventoryReportDetails);
            }
            //稽核数量不等于0
            if (!auditCredit.equals(BigDecimal.ZERO)) {
                request.setAuditSnapShoot(0.00);
                request.setAuditSnapShootTime(LocalDateTime.now());
                MakeInventoryReportDetails makeInventoryReportDetails = new MakeInventoryReportDetails();
                BeanUtil.copyProperties(request, makeInventoryReportDetails);
                makeInventoryReportDetails.setCheckStatusDetails(2);
                //计算盘盈金额
                BigDecimal finalCredit = auditCredit.multiply(unitPrice);
                request.setFinalAmounts(finalCredit);
                //设置盘盈的数量
                request.setFinalCredit(auditCredit.doubleValue());
                makeInventoryReportDetailsMapper.updateById(makeInventoryReportDetails);
            }
        }
    }

    @Override
    public Map selectById(Integer id) {

        MakeInventoryReport makeInventoryReport = makeInventoryReportMapper.selectById(id);
        if (makeInventoryReport == null) {
            throw new ServiceException(500, "盘点报告不存在");
        }
        QueryWrapper<MakeInventoryReportDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("report_number", makeInventoryReport.getReportNumber());
        List<MakeInventoryReportDetails> makeInventoryReportDetails = makeInventoryReportDetailsMapper.selectList(queryWrapper);
        Map map = new HashMap();
        map.put("doc", makeInventoryReport);
        map.put("details", makeInventoryReportDetails);
        return map;
    }
}
