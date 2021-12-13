package com.huanhong.wms.service.impl;

import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.entity.Variable;
import com.huanhong.wms.mapper.VariableMapper;
import com.huanhong.wms.service.IVariableService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商户变量表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2020-01-09
 */
@Service
public class VariableServiceImpl extends SuperServiceImpl<VariableMapper, Variable> implements IVariableService {

    @Override
    public String getValueByKey(String key) {
        return this.baseMapper.getByKey(key).getValue();
    }

    @Override
    public Variable getByKey(String key) {
        return this.baseMapper.getByKey(key);
    }

    @Override
    public Variable setValueByKey(String key, String value) {
        this.baseMapper.setValueByKey(key, value);
        return this.baseMapper.getByKey(key);
    }

}
