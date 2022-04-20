package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanhong.wms.entity.ProcurementPlan;
import com.huanhong.wms.entity.param.DeptMaterialParam;
import com.huanhong.wms.entity.param.MaterialYearParam;
import com.huanhong.wms.entity.vo.MaterialYearVO;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 采购计划主表 Mapper 接口
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-21
 */
public interface ProcurementPlanMapper extends BaseMapper<ProcurementPlan> {
    @MapKey("id")
    List<Map<String, Object>> getProcurementPlanFrequencyAndQuantityByParam(DeptMaterialParam param);

    @MapKey("id")
    List<MaterialYearVO> getMaterialPurchasingAnalysisOnYearBasisByParam(MaterialYearParam param);
}
