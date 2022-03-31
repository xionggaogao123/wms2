package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanhong.wms.entity.WarehouseManagement;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 仓库管理 Mapper 接口
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-08
 */
@Repository
public interface WarehouseManagementMapper extends BaseMapper<WarehouseManagement> {

    @MapKey("id")
    List<Map<String, Object>> countOccupationByCompany(@Param("companyId") Integer companyId);

    @MapKey("id")
    List<Map<String, Object>> selectMaterialByCompany(@Param("companyId") Integer companyId);

    @MapKey("id")
    List<Map<String, Object>> countWarehouseOccupationByWarehouse(@Param("warehouseId") String warehouseId);

    @MapKey("id")
    List<Map<String, Object>> selectMaterialByWarehouse(@Param("warehouseId") String warehouseId);
}
