package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.param.InventoryInfoPage;
import com.huanhong.wms.entity.param.MaterialProfitParam;
import com.huanhong.wms.entity.vo.InventoryInfoVo;

import java.util.List;
import java.util.Map;

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

    List<Map<String, Object>> getMaterialProfitList(MaterialProfitParam param);
}
