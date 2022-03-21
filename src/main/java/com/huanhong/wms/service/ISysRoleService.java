package com.huanhong.wms.service;

import cn.hutool.core.lang.Dict;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.SysRole;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.param.SysRoleParam;

import java.util.List;

/**
 * <p>
 * 系统角色表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-20
 */
public interface ISysRoleService extends SuperService<SysRole> {

    List<Dict> dropDown();

    List<Dict> getLoginRoles(Integer userId);

    Result ownMenu(SysRoleParam sysRoleParam);
}
