package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;

/**
 * @Author wang
 * @date 2022/5/27 20:37
 */
public interface InventoryDocumentService {
    /**
     * 查询清点主表和子表数据
     * @param id
     * @return
     */
    Result selectById(Integer id);
}
