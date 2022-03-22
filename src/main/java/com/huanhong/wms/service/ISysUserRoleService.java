package com.huanhong.wms.service;

import com.huanhong.wms.entity.SysUserRole;
import com.huanhong.wms.SuperService;

import java.util.List;

/**
 * <p>
 * 系统用户角色表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-20
 */
public interface ISysUserRoleService extends SuperService<SysUserRole> {

    List<Integer> getUserRoleIdList(Integer userId);
}
