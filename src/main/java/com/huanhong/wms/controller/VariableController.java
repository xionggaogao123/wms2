package com.huanhong.wms.controller;


import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Variable;
import com.huanhong.wms.mapper.VariableMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/variable")
@ApiSort(2)
@Api(tags = "参数变量 🔧")
public class VariableController extends BaseController {

    @Resource
    private VariableMapper variableMapper;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", value = "键", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isPrivate", value = "是否是私人变量", paramType = "query"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation("获取变量列表")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(@RequestParam String key,
                                                  @RequestParam(required = false) boolean isKeys,
                                                  @RequestParam(required = false) boolean isPrivate) {
        LoginUser loginUser = this.getLoginUser();
        QueryWrapper<Variable> wrapper = new QueryWrapper<>();
        wrapper.select("id", "`key`", "value", "extra", "remark", "create_time", "last_update")
                .orderByAsc("orders");
        if (isKeys) {
            String[] keys = key.split(",");
            wrapper.in("`key`", Convert.toList(String.class, keys));
        } else {
            wrapper.eq("`key`", key);
        }
        if (isPrivate) {
            wrapper.eq("user_id", loginUser.getId());
        }
        List<Map<String, Object>> maps = variableMapper.selectMaps(wrapper);
        return Result.success(maps);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("查询变量")
    @GetMapping("/{key}")
    public Result<Variable> info(@PathVariable String key) {
        QueryWrapper<Variable> query = new QueryWrapper<>();
        query.select("id", "`key`", "value", "extra", "remark")
                .eq("`key`", key);
        return Result.success(variableMapper.selectOne(query));
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("添加变量")
    @PostMapping
    public Result<Integer> add(@Valid @RequestBody Variable variable) {
        LoginUser loginUser = this.getLoginUser();
        variable.setCreatedBy(loginUser.getId());
        variableMapper.insert(variable);
        return Result.success();
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("更新变量")
    @PutMapping
    public Result<Integer> update(@Valid @RequestBody Variable variable) {
        int update = variableMapper.updateById(variable);
        return render(update > 0);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation("删除变量")
    @DeleteMapping("/{id}")
    public Result<Integer> delete(@PathVariable Integer id) {
        int i = variableMapper.deleteById(id);
        return render(i > 0);
    }

}