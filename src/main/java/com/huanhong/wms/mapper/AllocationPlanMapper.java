package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.entity.AllocationPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanhong.wms.entity.param.AllocationDetailPage;
import com.huanhong.wms.entity.vo.AllocationDetailVo;

/**
 * <p>
 * 调拨计划主表 Mapper 接口
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-17
 */
public interface AllocationPlanMapper extends BaseMapper<AllocationPlan> {


    Page<AllocationDetailVo> allocationDetail(AllocationDetailPage page);
}
