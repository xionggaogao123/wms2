package com.huanhong.wms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huanhong.wms.entity.Variable;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 商户变量表 Mapper 接口
 * </p>
 *
 * @author liudeyi
 * @since 2020-01-09
 */
@Repository
public interface VariableMapper extends BaseMapper<Variable> {

    @Select("select `value`,gmt_update,remark from variable where del = 0 and `key` = #{key} limit 1")
    Variable getByKey(@Param("key") String key);

    @Update("update variable set `value` = #{value} where del = 0 and `key` = #{key}")
    Integer setValueByKey(@Param("key") String key, @Param("value") String value);

}
