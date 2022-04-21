package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.MakeInventory;
import com.huanhong.wms.entity.param.InventorySurplusLossPage;
import com.huanhong.wms.entity.vo.InventorySurplusLossVo;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author liudeyi
 * @since 2022-02-28
 */
public interface MakeInventoryMapper extends BaseMapper<MakeInventory> {

    Page<InventorySurplusLossVo> inventorySurplusLoss(InventorySurplusLossPage page);
}
