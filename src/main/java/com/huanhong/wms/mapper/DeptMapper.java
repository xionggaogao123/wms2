package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanhong.wms.entity.Dept;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 部门表 Mapper 接口
 * </p>
 *
 * @author liudeyi
 * @since 2019-12-26
 */
@Repository
public interface DeptMapper extends BaseMapper<Dept> {

    @Select("SELECT GROUP_CONCAT(`name` ORDER BY id SEPARATOR '-') FROM dept WHERE id IN (${deptIds})")
    String selectNameByIds(@Param("deptIds") String deptIds);

    @Select("SELECT id FROM dept WHERE del = 0 and name = #{name}")
    Integer[] getIdByName(@Param("name") String name);

    @Select("select id, name, parent_id, level from dept where id = #{id}")
    Map<String, Object> getDeptById(Integer id);

    @Select("select id, code, level from dept where is_company = 1 and company_id = #{companyId}")
    Dept getParentCompanyDept(Integer companyId);

    /**
     * 获取部门上级目录
     *
     * @param id 部门ID
     * @return List<Dept>
     */
    List<Map<String, Object>> getDeptUp(String id);

    @Update("update dept set user_count = #{userCount} where id = #{id}")
    int updateDeptUserCount(@Param("id") Integer id, @Param("userCount") Integer userCount);

    @Update("update dept set del = 1 where company_id = #{companyId}")
    int deleteByCompanyId(Integer companyId);

}
