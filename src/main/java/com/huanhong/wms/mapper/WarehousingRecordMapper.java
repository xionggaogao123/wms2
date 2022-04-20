package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.WarehousingRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanhong.wms.entity.param.MaterialOutInParam;
import com.huanhong.wms.entity.param.WarehousingDetailPage;
import com.huanhong.wms.entity.vo.WarehousingDetailVo;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author liudeyi
 * @since 2022-04-07
 */
public interface WarehousingRecordMapper extends BaseMapper<WarehousingRecord> {

    Page<WarehousingDetailVo> warehousingDetail(WarehousingDetailPage page);

    @MapKey("id")
    List<Map<String, Object>> getTheTrendOfWarehouseInboundByParam(MaterialOutInParam param);
}
