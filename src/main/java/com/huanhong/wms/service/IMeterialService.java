package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.Meterial;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.vo.MeterialVO;

/**
 * <p>
 * 材料 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2021-11-22
 */
public interface IMeterialService extends SuperService<Meterial> {

    Meterial getMeterialByMeterialCode(String meterialCode);

    Meterial getMeterialByMeterialName(String meterialName);

    Page<Meterial> pageFuzzyQuery(Page<Meterial> meterialPage, MeterialVO meterialVO);
}
