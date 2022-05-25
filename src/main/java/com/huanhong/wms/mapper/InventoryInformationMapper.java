package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.param.InventoryInfoPage;
import com.huanhong.wms.entity.param.MaterialProfitParam;
import com.huanhong.wms.entity.vo.InventoryInfoVo;
import com.huanhong.wms.entity.vo.PreExpirationInventoryInfoVo;
import com.huanhong.wms.entity.vo.SafeInventoryInfoVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

    @MapKey("id")
    List<Map<String, Object>> getMaterialProfitList(MaterialProfitParam param);

    List<SafeInventoryInfoVo> getBelowSafetyStockMaterialWarningByParam(@Param("warehouseId") String warehouseId);

    List<PreExpirationInventoryInfoVo> getPreExpirationWarningByParam(@Param("warehouseId") String warehouseId, @Param("days") Integer days);
    @Select("select COALESCE(sum(inventory_credit),0) from inventory_information where del = 0 and warehouse_id=#{warehouseId} and material_coding=#{materialCoding}")
    Double sumInventoryCreditByWarehouseMaterialCoding(@Param("warehouseId") String warehouseId, @Param("materialCoding") String materialCoding);

}
