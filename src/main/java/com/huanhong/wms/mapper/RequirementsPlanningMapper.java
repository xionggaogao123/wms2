package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanhong.wms.entity.RequirementsPlanning;
import com.huanhong.wms.entity.param.DeptMaterialParam;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 需求计划表 Mapper 接口
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-16
 */
public interface RequirementsPlanningMapper extends BaseMapper<RequirementsPlanning> {
    @MapKey("id")
    List<Map<String, Object>> getMaterialNeedList(DeptMaterialParam param);

    @Update("update requirements_planning set is_imported=#{isImported},plan_number_imported='' where del = 0 and plan_number in (${planNumbers})")
    Integer updateIsImportedByPlanNumbers(@Param("isImported") Integer isImported, @Param("planNumbers") String planNumbers);
}
