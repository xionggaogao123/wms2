package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.SysRoleMenu;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.param.SysRoleParam;

import java.util.List;

/**
 * <p>
 * 系统角色菜单表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-20
 */
public interface ISysRoleMenuService extends SuperService<SysRoleMenu> {

    List<Integer> getRoleMenuIdList(List<Integer> roleIdList);

    Result grantMenu(SysRoleParam sysRoleParam);
}
