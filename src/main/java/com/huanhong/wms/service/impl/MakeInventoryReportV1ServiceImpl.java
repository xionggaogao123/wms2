package com.huanhong.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.common.exception.ServiceException;
import com.huanhong.common.units.JsonUtil;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.dto.request.UpdateMakeInventoryReportRequest;
import com.huanhong.wms.entity.MakeInventory;
import com.huanhong.wms.entity.MakeInventoryReport;
import com.huanhong.wms.entity.MakeInventoryReportDetails;
import com.huanhong.wms.mapper.MakeInventoryMapper;
import com.huanhong.wms.mapper.MakeInventoryReportDetailsMapper;
import com.huanhong.wms.mapper.MakeInventoryReportMapper;
import com.huanhong.wms.service.MakeInventoryReportV1Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
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

    @Override
    public Result update(UpdateMakeInventoryReportRequest request) {
        MakeInventoryReport makeInventoryReport1 = request.getMakeInventoryReport();
        QueryWrapper<MakeInventoryReport> reportQueryWrapper = new QueryWrapper<>();
        reportQueryWrapper.eq("document_number",makeInventoryReport1.getDocumentNumber());
        //盘点报告是否存在
        MakeInventoryReport makeInventoryReport = makeInventoryReportMapper.selectOne(reportQueryWrapper);
        if (makeInventoryReport == null) {
            log.error("PDA传入的数据为:{}",JsonUtil.obj2String(request));
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
            //盈亏金额 亏损数量 计算
            BigDecimal bigDecimal1 = new BigDecimal(details.getCheckCredit());
            BigDecimal bigDecimal2 = new BigDecimal(details.getInventoryCredit());
            BigDecimal subtract = bigDecimal1.subtract(bigDecimal2);
            BigDecimal multiply = subtract.multiply(details.getUnitPrice());
            details.setFinalAmounts(multiply);
            details.setFinalCredit(subtract.doubleValue());
            //更新盘点数据
            makeInventoryReportDetailsMapper.updateById(details);
        });
        if (number.get() == 0) {
            makeInventoryReport1.setCheckStatus(1);
            makeInventoryReportMapper.updateById(makeInventoryReport1);
            String reportNumber = makeInventoryReport1.getDocumentNumber();
            QueryWrapper<MakeInventory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("document_number",reportNumber);
            MakeInventory makeInventory = makeInventoryMapper.selectOne(queryWrapper);
            makeInventory.setCheckStatus(1);
            makeInventoryMapper.updateById(makeInventory);
        }
        return Result.success("盘点报告更新成功");
    }

    @Override
    public Map selectById(Integer id) {

        MakeInventoryReport makeInventoryReport = makeInventoryReportMapper.selectById(id);
        if(makeInventoryReport == null){
            throw new ServiceException(500,"盘点报告不存在");
        }
        QueryWrapper<MakeInventoryReportDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("report_number",makeInventoryReport.getReportNumber());
        List<MakeInventoryReportDetails> makeInventoryReportDetails = makeInventoryReportDetailsMapper.selectList(queryWrapper);
        Map map = new HashMap();
        map.put("doc", makeInventoryReport);
        map.put("details", makeInventoryReportDetails);
        return map;
    }
}
