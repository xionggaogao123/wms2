package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.param.InventoryInfoPage;
import com.huanhong.wms.entity.vo.InventoryInfoVo;

/**
 * <p>
 * 库存表 Mapper 接口
 * </p>
 *
 * @author liudeyi
 * @since 2021-11-25
 */
public interface InventoryInformationMapper extends BaseMapper<InventoryInformation> {


    Page<InventoryInfoVo> inventoryBill(InventoryInfoPage page);

    Page<InventoryInfoVo> deadGoods(InventoryInfoPage page);

    Page<InventoryInfoVo> deadGoodsSettle(InventoryInfoPage page);
}
