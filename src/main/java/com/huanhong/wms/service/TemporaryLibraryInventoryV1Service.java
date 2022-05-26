package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.dto.AddTemporaryLibraryInventoryAndDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryLibraryInventoryAndDetailsDTO;

/**
 * @Author wang
 * @date 2022/5/25 9:42
 */
public interface TemporaryLibraryInventoryV1Service {

    /**
     * 新增临时清点主表和子表数据 自动生成临时入库数据
     * @param addTemporaryLibraryInventoryAndDetailsDTO
     * @return
     */
    Result addTemporaryMainAndSublistAndWarehouse(AddTemporaryLibraryInventoryAndDetailsDTO addTemporaryLibraryInventoryAndDetailsDTO);

    /**
     * 更新临时清点主表和子表数据 自动生成临时入库数据
     * @param update
     * @return
     */
    Result updateTemporaryMainAndSublistAndWarehouse(UpdateTemporaryLibraryInventoryAndDetailsDTO update);

    /**
     * id 查询
     * @param id
     * @return
     */
    Result selectById(Long id);
}
