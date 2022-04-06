package com.huanhong.wms.service;

import cn.hutool.core.lang.tree.Tree;
import com.huanhong.wms.entity.SysMenu;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.param.SysMenuParam;

import java.util.List;

/**
 * <p>
 * 系统菜单表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-20
 */
public interface ISysMenuService extends SuperService<SysMenu> {

    List<Tree<String>> tree(SysMenuParam sysMenuParam);

    List<Tree<String>> tree4Menu(SysMenuParam sysMenuParam);

    List<Tree<String>> tree4Grant(SysMenuParam sysMenuParam);

    List<String> getLoginPermissions(Integer userId, List<Integer> menuIdList);
}
