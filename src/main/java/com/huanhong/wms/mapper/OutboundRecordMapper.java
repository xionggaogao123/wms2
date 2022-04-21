package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.OutboundRecord;
import com.huanhong.wms.entity.param.MaterialOutInParam;
import com.huanhong.wms.entity.param.OutboundDetailPage;
import com.huanhong.wms.entity.vo.OutboundDetailVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 出库记录 Mapper 接口
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-07
 */
public interface OutboundRecordMapper extends BaseMapper<OutboundRecord> {

    @MapKey("id")
    List<Map<String, Object>> countOutboundRecordByWarehouse(@Param("warehouseId") String warehouseId);

    Page<OutboundDetailVo> outboundDetail(OutboundDetailPage page);

    @MapKey("id")
    List<Map<String, Object>> getTheTrendOfWarehouseOutboundByParam(MaterialOutInParam param);

    @MapKey("id")
    List<Map<String, Object>> getTheTotalMoneyOfOutboundByParam(MaterialOutInParam param);
    @Select("select sum(out_quantity) from outbound_record where del = 0 and material_coding={materialCoding} " +
            "and create_time between #{startTime} and #{endTime}")
    double sumNumber(@Param("materialCoding") String materialCoding, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
