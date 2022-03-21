package com.huanhong.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huanhong.wms.entity.SysUserRole;
import com.huanhong.wms.mapper.SysUserRoleMapper;
import com.huanhong.wms.service.ISysUserRoleService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统用户角色表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-20
 */
@Service
public class SysUserRoleServiceImpl extends SuperServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

    @Override
    public List<Integer> getUserRoleIdList(Integer userId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        return this.list(queryWrapper).stream().map(SysUserRole::getRoleId).collect(Collectors.toList());

    }
}
