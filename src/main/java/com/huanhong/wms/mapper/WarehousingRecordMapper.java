package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.WarehousingRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanhong.wms.entity.param.WarehousingDetailPage;
import com.huanhong.wms.entity.vo.WarehousingDetailVo;

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
}
