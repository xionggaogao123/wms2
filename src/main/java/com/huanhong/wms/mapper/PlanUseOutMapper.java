package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanhong.wms.entity.PlanUseOut;
import com.huanhong.wms.entity.param.DeptMaterialParam;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 计划领用主表 Mapper 接口
 * </p>
 *
 * @author liudeyi
 * @since 2022-02-15
 */
public interface PlanUseOutMapper extends BaseMapper<PlanUseOut> {
    @MapKey("id")
    List<Map<String, Object>> getMaterialUseList(DeptMaterialParam param);
}
