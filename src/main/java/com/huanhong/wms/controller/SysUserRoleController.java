package com.huanhong.wms.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.SysUserRole;
import com.huanhong.wms.service.ISysUserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@ApiSort()
@Api(tags = "系统用户角色表")
@RestController
@RequestMapping("/v1/sysUserRole")
public class SysUserRoleController extends BaseController {

    @Resource
    private ISysUserRoleService sysUserRoleService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
            @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询系统用户角色表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<SysUserRole>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam Map<String, Object> search) {
        QueryWrapper<SysUserRole> query = new QueryWrapper<>();
        query.orderByDesc("id");

        return Result.success(sysUserRoleService.page(new Page<>(current, size), query));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加系统用户角色表", notes = "生成代码")
    @PostMapping
    public Result add(@Valid @RequestBody SysUserRole sysUserRole) {
        return render(sysUserRoleService.save(sysUserRole));
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新系统用户角色表", notes = "生成代码")
    @PutMapping
    public Result update(@Valid @RequestBody SysUserRole sysUserRole) {
        return render(sysUserRoleService.updateById(sysUserRole));
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除系统用户角色表", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return render(sysUserRoleService.removeById(id));
    }



}

