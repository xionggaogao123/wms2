package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.BalanceLibraryDetail;
import com.huanhong.wms.SuperService;

/**
 * <p>
 * 平衡利库明细 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-24
 */
public interface IBalanceLibraryDetailService extends SuperService<BalanceLibraryDetail> {

    Result createProcurementPlan(BalanceLibraryDetail balanceLibraryDetail);
}
