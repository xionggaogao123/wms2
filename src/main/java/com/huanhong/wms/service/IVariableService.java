package com.huanhong.wms.service;

import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.Variable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

/**
 * <p>
 * 商户变量表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2020-01-09
 */
public interface IVariableService extends SuperService<Variable> {

    @Cacheable(value = {"variable"}, key = "T(String).valueOf(#key)", unless = "#result == null")
    String getValueByKey(String key);

    @Cacheable(value = {"variable"}, key = "T(String).valueOf(#key).concat('-Object')", unless = "#result == null")
    Variable getByKey(String key);

    @CachePut(value = {"variable"}, key = "T(String).valueOf(#key).concat('-Object')", unless = "#result == null")
    Variable setValueByKey(String key, String value);

}
