package com.huanhong.wms.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.RedisKey;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Dept;
import com.huanhong.wms.mapper.DeptMapper;
import com.huanhong.wms.service.IDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/dept")
@ApiSort(22)
@Api(tags = "部门管理 🏢")
public class DeptController extends BaseController {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private IDeptService deptService;
    @Resource
    private DeptMapper deptMapper;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "filterEmptyUser", value = "过滤部门下无用户的部门"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取部门树")
    @GetMapping("/tree")
    public Result<List<Dept>> tree(@RequestParam(required = false) boolean filterEmptyUser) {
        QueryWrapper<Dept> query = new QueryWrapper<>();
        query.orderByAsc("sort");
        if (filterEmptyUser) {
            query.gt("user_count", 0);
        }
        String redisKey = RedisKey.DEPT_TREE;
        List<Dept> data = (List<Dept>) redisTemplate.opsForValue().get(redisKey);

        if (CollectionUtil.isEmpty(data)) {
            data = deptService.getDeptTree(query).getData();

            redisTemplate.opsForValue().set(redisKey, data);
            redisTemplate.expire(redisKey, 2, TimeUnit.HOURS);
        }
        return Result.success(data);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "新增部门")
    @PostMapping
    public Result add(@Valid @RequestBody Dept dept) {
        LoginUser loginUser = this.getLoginUser();
//        if (loginUser.getPermissionLevel()) {
//            return Result.noAuthority();
//        }
        int insert = deptMapper.insert(dept);
        if(insert > 0) {
            redisTemplate.delete(RedisKey.DEPT_TREE);
            return Result.success();
        }

        return Result.failure("添加失败");
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新部门")
    @PutMapping
    public Result update(@Valid @RequestBody Dept dept) {
        LoginUser loginUser = this.getLoginUser();
//        if (loginUser.getPermissionLevel()) {
//            return Result.noAuthority();
//        }
        int update = deptMapper.updateById(dept);
        if(update > 0) {
            redisTemplate.delete(RedisKey.DEPT_TREE);
            return Result.success();
        }

        return Result.failure("更新失败");
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除部门")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        LoginUser loginUser = this.getLoginUser();
//        if (loginUser.getPermissionLevel()) {
//            return Result.noAuthority();
//        }
        return deptService.deleteDept(loginUser, id);
    }
}

