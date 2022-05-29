package com.huanhong.wms.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.MakeInventory;
import com.huanhong.wms.entity.MakeInventoryReport;
import com.huanhong.wms.entity.vo.MakeInventoryVO;
import com.huanhong.wms.mapper.MakeInventoryReportMapper;
import com.huanhong.wms.service.MakeInventoryReportService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;

/**
 * @Author wang
 * @date 2022/5/29 22:29
 */
@Service
public class MakeInventoryReportServiceV1Impl implements MakeInventoryReportService {

    @Resource
    private MakeInventoryReportMapper makeInventoryReportMapper;

    @Override
    public Page<MakeInventoryReport> pageV1(Page<MakeInventoryReport> objectPage, MakeInventoryVO makeInventoryVO) {
        QueryWrapper<MakeInventoryReport> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (ObjectUtil.isEmpty(makeInventoryVO)) {
            return makeInventoryReportMapper.selectPage(objectPage, queryWrapper);
        }

        //盘点状态
        queryWrapper.eq(ObjectUtil.isNotNull(makeInventoryVO.getCheckStatus()), "check_status", makeInventoryVO.getCheckStatus());

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        /**
         * 盘点单创建时间区间查询
         */
        if (ObjectUtil.isNotEmpty(makeInventoryVO.getCreateDateStart()) && ObjectUtil.isNotEmpty(makeInventoryVO.getCreateDateEnd())) {

            String createDateStart = dtf1.format(makeInventoryVO.getCreateDateStart());

            String createDateEnd = dtf1.format(makeInventoryVO.getCreateDateEnd());

            queryWrapper.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");

        }

        return makeInventoryReportMapper.selectPage(objectPage, queryWrapper);
    }
}
