package com.huanhong.wms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.annotion.OperateLog;
import com.huanhong.common.enums.OperateType;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.SysRole;
import com.huanhong.wms.entity.param.SysRoleParam;
import com.huanhong.wms.service.ISysRoleMenuService;
import com.huanhong.wms.service.ISysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@ApiSort()
@Api(tags = "系统角色表")
@RestController
@RequestMapping("/v1/sysRole")
public class SysRoleController extends BaseController {

    @Resource
    private ISysRoleService sysRoleService;
    @Resource
    private ISysRoleMenuService sysRoleMenuService;

    @OperateLog(title = "系统角色分页_查询", type = OperateType.QUERY)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
            @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询系统角色表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<SysRole>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                      @RequestParam Map<String, Object> search) {
        QueryWrapper<SysRole> query = new QueryWrapper<>();
        query.orderByDesc("id");
        if (search.containsKey("search")) {
            String text = search.get("search").toString();
            if (StrUtil.isNotEmpty(text)) {
                query.and(qw -> qw.like("name", text)
                );
            }
        }
        return Result.success(sysRoleService.page(new Page<>(current, size), query));
    }

    @OperateLog(title = "系统角色_增加", type = OperateType.ADD)
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加系统角色表", notes = "生成代码")
    @PostMapping
    public Result add(@Valid @RequestBody SysRole sysRole) {
        return render(sysRoleService.save(sysRole));
    }

    @OperateLog(title = "系统角色_更新", type = OperateType.UPDATE)
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新系统角色表", notes = "生成代码")
    @PutMapping
    public Result update(@Valid @RequestBody SysRole sysRole) {
        return render(sysRoleService.updateById(sysRole));
    }

    @OperateLog(title = "系统角色_删除", type = OperateType.DELETE)
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除系统角色表", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return render(sysRoleService.removeById(id));
    }

    @OperateLog(title = "系统角色_详情", type = OperateType.DETAIL)
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "查看系统角色")
    @GetMapping("/{id}")
    public Result info(@PathVariable("id") Integer id) {
        return Result.success(sysRoleService.getById(id));
    }

    @OperateLog(title = "系统角色_下拉", type = OperateType.QUERY)
    @ApiOperation(value = "系统角色下拉（用于授权角色时选择）")
    @GetMapping("/dropDown")
    public Result dropDown() {
        return Result.success(sysRoleService.dropDown());
    }


    @ApiOperation(value = "授权菜单")
    @PostMapping("/grantMenu")
    public Result grantMenu(@RequestBody SysRoleParam sysRoleParam) {

        return sysRoleMenuService.grantMenu(sysRoleParam);
    }

    @ApiOperation(value = "系统角色_拥有菜单")
    @GetMapping("/ownMenu")
    public Result ownMenu(SysRoleParam sysRoleParam) {
        return sysRoleService.ownMenu(sysRoleParam);
    }
}

