package com.huanhong.wms.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.SysRole;
import com.huanhong.wms.entity.SysRoleMenu;
import com.huanhong.wms.entity.param.SysRoleParam;
import com.huanhong.wms.mapper.SysRoleMapper;
import com.huanhong.wms.mapper.SysRoleMenuMapper;
import com.huanhong.wms.service.ISysRoleMenuService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统角色菜单表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-20
 */
@Service
public class SysRoleMenuServiceImpl extends SuperServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements ISysRoleMenuService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Override
    public List<Integer> getRoleMenuIdList(List<Integer> roleIdList) {
        if (ObjectUtil.isNotEmpty(roleIdList)) {
            LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SysRoleMenu::getRoleId, roleIdList);
            return this.list(queryWrapper).stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
        }
        return CollectionUtil.newArrayList();
    }

    @Override
    public Result grantMenu(SysRoleParam sysRoleParam) {
        SysRole sysRole = sysRoleMapper.selectById(sysRoleParam.getId());
        if (ObjectUtil.isNull(sysRole)) {
            return Result.failure("角色不存在");
        }
        Integer roleId = sysRoleParam.getId();
        //删除所拥有菜单
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        this.remove(queryWrapper);
        //授权菜单
        sysRoleParam.getGrantMenuIdList().forEach(menuId -> {
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenu.setMenuId(menuId);
            this.save(sysRoleMenu);
        });
        return Result.success();
    }
}
