package com.huanhong.wms.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.common.units.ThreadLocalUtil;
import com.huanhong.wms.bean.Constant;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.bean.enums.CommonStatusEnum;
import com.huanhong.wms.entity.SysRole;
import com.huanhong.wms.entity.SysRoleMenu;
import com.huanhong.wms.entity.param.SysRoleParam;
import com.huanhong.wms.mapper.SysRoleMapper;
import com.huanhong.wms.service.ISysRoleMenuService;
import com.huanhong.wms.service.ISysRoleService;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.service.ISysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统角色表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-20
 */
@Service
public class SysRoleServiceImpl extends SuperServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Autowired
    private ISysUserRoleService sysUserRoleService;
    @Autowired
    private ISysRoleMenuService sysRoleMenuService;

    @Override
    public List<Dict> dropDown() {

        List<Dict> dictList = CollectionUtil.newArrayList();
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        //如果当前登录用户不是超级管理员，则查询自己拥有的
        if (ThreadLocalUtil.getCurrentUser().getIsAdmin() != 1) {

            //查询自己拥有的
            List<String> loginUserRoleIds = ThreadLocalUtil.getCurrentUser().getLoginUserRoleIds();
            if (ObjectUtil.isEmpty(loginUserRoleIds)) {
                return dictList;
            }
            queryWrapper.in(SysRole::getId, loginUserRoleIds);
        }
        //只查询正常状态
        queryWrapper.eq(SysRole::getStatus, CommonStatusEnum.ENABLE.getCode());
        this.list(queryWrapper)
                .forEach(sysRole -> {
                    Dict dict = Dict.create();
                    dict.put(Constant.ID, sysRole.getId());
                    dict.put(Constant.CODE, sysRole.getCode());
                    dict.put(Constant.NAME, sysRole.getName());
                    dictList.add(dict);
                });
        return dictList;
    }

    @Override
    public List<Dict> getLoginRoles(Integer userId) {
        List<Dict> dictList = CollectionUtil.newArrayList();
        //获取用户角色id集合
        List<Integer> roleIdList = sysUserRoleService.getUserRoleIdList(userId);
        if (ObjectUtil.isNotEmpty(roleIdList)) {
            LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SysRole::getId, roleIdList).eq(SysRole::getStatus, CommonStatusEnum.ENABLE.getCode());
            //根据角色id集合查询并返回结果
            dictList = this.list(queryWrapper).stream().map(sysRole -> {
                Dict dict = Dict.create();
                dict.put(Constant.ID, sysRole.getId());
                dict.put(Constant.CODE, sysRole.getCode());
                dict.put(Constant.NAME, sysRole.getName());
                return dict;
            }).collect(Collectors.toList());
        }
        return dictList;
    }

    @Override
    public Result ownMenu(SysRoleParam sysRoleParam) {
        SysRole sysRole = this.getById(sysRoleParam.getId());
        if (null == sysRole) {
            return Result.failure("角色不存子啊");
        }
        return Result.success(sysRoleMenuService.getRoleMenuIdList(CollectionUtil.newArrayList(sysRole.getId())));
    }

}
