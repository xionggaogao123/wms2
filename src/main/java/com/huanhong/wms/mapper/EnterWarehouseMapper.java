package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanhong.wms.entity.EnterWarehouse;
import com.huanhong.wms.entity.param.MaterialPriceParam;
import com.huanhong.wms.entity.vo.MaterialPriceVO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

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

    @Select("select sum(enter_quantity) from warehousing_record where del = 0 and material_coding={materialCoding} " +
            "and create_time between #{startTime} and #{endTime}")
    double sumNumber(@Param("materialCoding") String materialCoding, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

}
