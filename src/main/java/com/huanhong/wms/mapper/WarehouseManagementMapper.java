package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.wms.entity.WarehouseManagement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 仓库管理 Mapper 接口
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-08
 */
public interface WarehouseManagementMapper extends BaseMapper<WarehouseManagement> {

    List<WarehouseManagement> selectList(QueryWrapper<WarehouseManagement> wrapper);

    /**
     * 模糊查询
     *
     * @param field
     * @return
     */
    @Select("select ${field} from `warehouse_management` where del = 0  and LOCATE(#{value},${field})>0\"")
    List<String> fuzzyQuerySelectList(String field, String value);

}
