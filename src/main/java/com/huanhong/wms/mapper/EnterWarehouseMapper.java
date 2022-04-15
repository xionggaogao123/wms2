package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanhong.wms.entity.EnterWarehouse;
import com.huanhong.wms.entity.param.MaterialPriceParam;
import com.huanhong.wms.entity.vo.MaterialPriceVO;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 采购入库单主表 Mapper 接口
 * </p>
 *
 * @author liudeyi
 * @since 2022-01-24
 */
public interface EnterWarehouseMapper extends BaseMapper<EnterWarehouse> {
    @MapKey("id")
    List<MaterialPriceVO> getMaterialPriceList(MaterialPriceParam param);
}
