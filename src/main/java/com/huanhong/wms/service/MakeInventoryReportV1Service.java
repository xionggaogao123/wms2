package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;
import com.huanhong.wms.dto.request.UpdateMakeInventoryReportRequest;

import java.util.Map;

/**
 * @Author wang
 * @date 2022/5/28 19:50
 */
public interface MakeInventoryReportV1Service {

    /**
     * 更新盘点报告
     * @param request
     * @return
     */
    Result update(UpdateMakeInventoryReportRequest request);


    /**
     * 根据id查询
     * @param id
     * @return
     */
    Map selectById(Integer id);
}
