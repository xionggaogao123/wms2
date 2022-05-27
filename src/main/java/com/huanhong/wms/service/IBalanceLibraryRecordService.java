package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.BalanceLibraryRecord;
import com.huanhong.wms.SuperService;

import java.util.List;

/**
 * <p>
 * 平衡利库记录 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-24
 */
public interface IBalanceLibraryRecordService extends SuperService<BalanceLibraryRecord> {
    /**
     * 添加平衡利库记录
     * @param balanceLibraryRecords
     * @return
     */
    Result add(List<BalanceLibraryRecord> balanceLibraryRecords);

}
