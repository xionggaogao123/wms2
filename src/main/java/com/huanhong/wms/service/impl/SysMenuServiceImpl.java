package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huanhong.common.units.ThreadLocalUtil;
import com.huanhong.wms.bean.enums.CommonStatusEnum;
import com.huanhong.wms.bean.enums.MenuTypeEnum;
import com.huanhong.wms.bean.enums.SymbolConstant;
import com.huanhong.wms.entity.SysMenu;
import com.huanhong.wms.entity.param.SysMenuParam;
import com.huanhong.wms.mapper.SysMenuMapper;
import com.huanhong.wms.service.ISysMenuService;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.service.ISysRoleMenuService;
import com.huanhong.wms.service.ISysUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 系统菜单表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-20
 */
@Service
public class SysMenuServiceImpl extends SuperServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Autowired
    private ISysUserRoleService sysUserRoleService;
    @Autowired
    private ISysRoleMenuService sysRoleMenuService;
    @Override
    public List<Tree<String>> tree(SysMenuParam sysMenuParam) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(sysMenuParam)) {
            //根据所属应用查询
            if (ObjectUtil.isNotEmpty(sysMenuParam.getApplication())) {
                queryWrapper.eq(SysMenu::getApplication, sysMenuParam.getApplication());
            }
            //根据菜单名称模糊查询
            if (ObjectUtil.isNotEmpty(sysMenuParam.getName())) {
                queryWrapper.like(SysMenu::getName, sysMenuParam.getName());
            }
        }
        queryWrapper.eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode());
        //根据排序升序排列，序号越小越在前
        queryWrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> sysMenuList = this.list(queryWrapper);
        //将结果集处理成树
        //配置
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        // 自定义属性名 都要默认值的
        treeNodeConfig.setWeightKey("sort");
        treeNodeConfig.setIdKey("id");
        // 最大递归深度
        treeNodeConfig.setDeep(3);

        //转换器
        List<Tree<String>> treeNodes = TreeUtil.build(sysMenuList, "0", treeNodeConfig,
                (treeNode, tree) -> {
                    BeanUtil.copyProperties(treeNode, tree);
                    tree.setId(treeNode.getId().toString());
                    tree.setParentId(treeNode.getPid().toString());
                    tree.setWeight(treeNode.getSort());
                    tree.setName(treeNode.getName());

                });
        return treeNodes;
    }

    @Override
    public List<Tree<String>> tree4Menu(SysMenuParam sysMenuParam) {

        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysMenu::getSort, SysMenu::getId, SysMenu::getName, SysMenu::getPid);
        if (ObjectUtil.isNotNull(sysMenuParam)) {
            if (ObjectUtil.isNotEmpty(sysMenuParam.getApplication())) {
                queryWrapper.eq(SysMenu::getApplication, sysMenuParam.getApplication());
            }
        }
        queryWrapper.eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode())
                .in(SysMenu::getType, CollectionUtil.newArrayList(MenuTypeEnum.DIR.getCode(), MenuTypeEnum.MENU.getCode()));
        //根据排序升序排列，序号越小越在前
        queryWrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> sysMenuList = this.list(queryWrapper);
        //将结果集处理成树
        //配置
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        // 自定义属性名 都要默认值的
        treeNodeConfig.setWeightKey("sort");
        treeNodeConfig.setIdKey("id");
        // 最大递归深度
        treeNodeConfig.setDeep(3);
        //转换器
        List<Tree<String>> treeNodes = TreeUtil.build(sysMenuList, "0", treeNodeConfig,
                (treeNode, tree) -> {
                    tree.setId(treeNode.getId().toString());
                    tree.setParentId(treeNode.getPid().toString());
                    tree.setWeight(treeNode.getSort());
                    tree.setName(treeNode.getName());
                });
        return treeNodes;
    }

    @Override
    public List<Tree<String>> tree4Grant(SysMenuParam sysMenuParam) {
        LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
        //根据应用查询
        if (ObjectUtil.isNotNull(sysMenuParam)) {
            if (ObjectUtil.isNotEmpty(sysMenuParam.getApplication())) {
                queryWrapper.eq(SysMenu::getApplication, sysMenuParam.getApplication());
            }
        }
        //如果是超级管理员给角色授权菜单时可选择所有菜单
        if (ThreadLocalUtil.getCurrentUser().getIsAdmin() == 1) {
            queryWrapper.eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode());
        } else {
            //非超级管理员则获取自己拥有的菜单，分配给人员，防止越级授权
            Integer userId = ThreadLocalUtil.getCurrentUser().getId();
            List<Integer> roleIdList = sysUserRoleService.getUserRoleIdList(userId);
            if (ObjectUtil.isNotEmpty(roleIdList)) {
                List<Integer> menuIdList = sysRoleMenuService.getRoleMenuIdList(roleIdList);
                if (ObjectUtil.isNotEmpty(menuIdList)) {
                    queryWrapper.in(SysMenu::getId, menuIdList)
                            .eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode());
                } else {
                    //如果角色的菜单为空，则查不到菜单
                    return CollectionUtil.newArrayList();
                }
            } else {
                //如果角色为空，则根本没菜单
                return CollectionUtil.newArrayList();
            }
        }
        //根据排序升序排列，序号越小越在前
        queryWrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> sysMenuList = this.list(queryWrapper);
        //将结果集处理成树
        //配置
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        // 自定义属性名 都要默认值的
        treeNodeConfig.setWeightKey("sort");
        treeNodeConfig.setIdKey("id");
        // 最大递归深度
        treeNodeConfig.setDeep(3);
        //转换器
        List<Tree<String>> treeNodes = TreeUtil.build(sysMenuList, "0", treeNodeConfig,
                (treeNode, tree) -> {
                    tree.setId(treeNode.getId().toString());
                    tree.setParentId(treeNode.getPid().toString());
                    tree.setWeight(treeNode.getSort());
                    tree.setName(treeNode.getName());
                });
        return treeNodes;
    }

    @Override
    public List<String> getLoginPermissions(Integer userId, List<Integer> menuIdList) {
        Set<String> permissions = CollectionUtil.newHashSet();
        if (ObjectUtil.isNotEmpty(menuIdList)) {
            LambdaQueryWrapper<SysMenu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SysMenu::getId, menuIdList).ne(SysMenu::getType, MenuTypeEnum.DIR.getCode())
                    .eq(SysMenu::getStatus, CommonStatusEnum.ENABLE.getCode());
            queryWrapper.orderByAsc(SysMenu::getSort);
            this.list(queryWrapper).forEach(sysMenu -> {
                if(MenuTypeEnum.BTN.getCode().equals(sysMenu.getType())) {
                    permissions.add(sysMenu.getPermission());
                } else {
                    String removePrefix = StrUtil.removePrefix(sysMenu.getRouter(), SymbolConstant.LEFT_DIVIDE);
                    String permission = removePrefix.replaceAll(SymbolConstant.LEFT_DIVIDE, SymbolConstant.COLON);
                    permissions.add(permission);
                }
            });
        }
        return CollectionUtil.newArrayList(permissions);
    }

    public static void main(String[] args) {
        // 构建node列表
        List<TreeNode<String>> nodeList = CollUtil.newArrayList();

        nodeList.add(new TreeNode<>("1", "0", "系统管理", 5));
        nodeList.add(new TreeNode<>("11", "1", "用户管理", 222222));
        nodeList.add(new TreeNode<>("111", "11", "用户添加", 0));
        nodeList.add(new TreeNode<>("2", "0", "店铺管理", 1));
        nodeList.add(new TreeNode<>("21", "2", "商品管理", 44));
        nodeList.add(new TreeNode<>("221", "2", "商品管理2", 2));
        //配置
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
// 自定义属性名 都要默认值的
        treeNodeConfig.setWeightKey("order");
        treeNodeConfig.setIdKey("rid");
// 最大递归深度
        treeNodeConfig.setDeep(3);

//转换器
        List<Tree<String>> treeNodes = TreeUtil.build(nodeList, "0", treeNodeConfig,
                (treeNode, tree) -> {
                    tree.setId(treeNode.getId());
                    tree.setParentId(treeNode.getParentId());
                    tree.setWeight(treeNode.getWeight());
                    tree.setName(treeNode.getName());
                    // 扩展属性 ...
                    tree.putExtra("extraField", 666);
                    tree.putExtra("other", new Object());
                });
        System.out.println(treeNodes);
    }

}
