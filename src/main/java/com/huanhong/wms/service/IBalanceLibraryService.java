package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.BalanceLibrary;
import com.huanhong.wms.SuperService;

import java.util.List;

/**
 * <p>
 * 平衡利库表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-24
 */
public interface IBalanceLibraryService extends SuperService<BalanceLibrary> {

    Result importProcurementPlans(List<Integer> procurementPlanIds);

    /**
     * 详情
     * @param id
     * @return
     */
    Result detail(Integer id);
}
