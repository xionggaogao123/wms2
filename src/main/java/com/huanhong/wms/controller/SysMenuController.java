package com.huanhong.wms.controller;

import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.param.SysMenuParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.entity.SysMenu;
import com.huanhong.wms.mapper.SysMenuMapper;
import com.huanhong.wms.service.ISysMenuService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@ApiSort()
@Api(tags = "系统菜单表")
@RestController
@RequestMapping("/v1/sysMenu")
public class SysMenuController extends BaseController {

    @Resource
    private ISysMenuService sysMenuService;

    @ApiOperation(value = "系统菜单列表（树)")
    @GetMapping("/list")
    public Result<List<Tree<String>>> list(SysMenuParam sysMenuParam) {
        return Result.success(sysMenuService.tree(sysMenuParam));
    }


    @ApiOperation(value = "获取系统菜单树，用于新增，编辑时选择上级节点")
    @GetMapping("/tree")
    public Result<List<Tree<String>>> tree(SysMenuParam sysMenuParam) {
        return Result.success(sysMenuService.tree4Menu(sysMenuParam));
    }
    /**
     * 获取系统菜单树，用于给角色授权时选择
     *
     * @author xuyuxiang
     * @date 2020/4/5 15:00
     */
    @ApiOperation(value = "获取系统菜单树，用于给角色授权时选择")
    @GetMapping("/treeForGrant")
    public Result<List<Tree<String>>> treeForGrant(SysMenuParam sysMenuParam) {
        return Result.success(sysMenuService.tree4Grant(sysMenuParam));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
            @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询系统菜单表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<SysMenu>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                      @RequestParam Map<String, Object> search) {
        QueryWrapper<SysMenu> query = new QueryWrapper<>();
        query.orderByDesc("id");
        if (search.containsKey("search")) {
            String text = search.get("search").toString();
            if (StrUtil.isNotEmpty(text)) {
                query.and(qw -> qw.like("name", text)
                );
            }
        }
        return Result.success(sysMenuService.page(new Page<>(current, size), query));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "查看系统菜单表", notes = "生成代码")
    @GetMapping("/{id}")
    public Result add(@PathVariable("id") Integer id) {
        return Result.success(sysMenuService.getById(id));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加系统菜单表", notes = "生成代码")
    @PostMapping
    public Result add(@Valid @RequestBody SysMenu sysMenu) {
        return render(sysMenuService.save(sysMenu));
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新系统菜单表", notes = "生成代码")
    @PutMapping
    public Result update(@Valid @RequestBody SysMenu sysMenu) {
        return render(sysMenuService.updateById(sysMenu));
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除系统菜单表", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return render(sysMenuService.removeById(id));
    }


}

