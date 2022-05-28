package com.huanhong.wms.service.impl;

import com.huanhong.common.exception.ServiceException;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.dto.request.UpdateMakeInventoryReportRequest;
import com.huanhong.wms.entity.MakeInventoryReport;
import com.huanhong.wms.entity.MakeInventoryReportDetails;
import com.huanhong.wms.mapper.MakeInventoryReportDetailsMapper;
import com.huanhong.wms.mapper.MakeInventoryReportMapper;
import com.huanhong.wms.service.MakeInventoryReportV1Service;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author wang
 * @date 2022/5/28 19:51
 */
@Service
public class MakeInventoryReportV1ServiceImpl implements MakeInventoryReportV1Service {


    @Resource
    private MakeInventoryReportMapper makeInventoryReportMapper;

    @Resource
    private MakeInventoryReportDetailsMapper makeInventoryReportDetailsMapper;

    @Override
    public Result update(UpdateMakeInventoryReportRequest request) {
        MakeInventoryReport makeInventoryReport1 = request.getMakeInventoryReport();
        //盘点报告是否存在
        MakeInventoryReport makeInventoryReport = makeInventoryReportMapper.selectById(request.getMakeInventoryReport().getId());
        if (makeInventoryReport == null) {
            throw new ServiceException(500, "盘点报告不存在");
        }
        //判断盘点状态是否 盘点完成
        AtomicInteger number = new AtomicInteger();
        List<MakeInventoryReportDetails> makeInventoryReportDetails = request.getMakeInventoryReportDetails();
        makeInventoryReportDetails.forEach(details -> {
            //更新盘点数据
            makeInventoryReportDetailsMapper.updateById(details);
            //盘点是否全部盘点完成 盘点状态: 0-待盘点，1-一致 ，2-盘盈 ，3-盘亏
            if (details.getCheckStatusDetails() == 0) {
                number.getAndIncrement();
            }
        });
        if(number.get() == 0){
            makeInventoryReport1.setCheckStatus(1);
            makeInventoryReportMapper.updateById(makeInventoryReport1);
        }
        return Result.success("盘点报告更新成功");
    }
}
