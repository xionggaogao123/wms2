package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.MakeInventory;
import com.huanhong.wms.entity.MakeInventoryReport;
import com.huanhong.wms.entity.vo.MakeInventoryVO;

/**
 * @Author wang
 * @date 2022/5/29 22:28
 */
public interface MakeInventoryReportService {
    /**
     * 分页查询
     * @param objectPage
     * @param makeInventoryVO
     */
    Page<MakeInventoryReport> pageV1(Page<MakeInventoryReport> objectPage, MakeInventoryVO makeInventoryVO);
}
