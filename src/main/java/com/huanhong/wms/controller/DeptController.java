package com.huanhong.wms.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.RedisKey;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Dept;
import com.huanhong.wms.entity.dto.DeptDTO;
import com.huanhong.wms.mapper.DeptMapper;
import com.huanhong.wms.service.IDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/dept")
@ApiSort(22)
@Api(tags = "部门管理 🏢")
public class DeptController extends BaseController {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private IDeptService deptService;
    @Resource
    private DeptMapper deptMapper;

    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取部门树")
    @GetMapping("/tree")
    public Result<List<Dept>> tree() {
        LoginUser loginUser = this.getLoginUser();
        QueryWrapper<Dept> query = new QueryWrapper<>();
        query.select("*")
//                .eq("company_id", loginUser.getCompanyId())
                .orderByAsc("sort");
//        String redisKey = RedisKey.DEPT_TREE;
//        List<Dept> data = (List<Dept>) redisTemplate.opsForValue().get(redisKey);
//        if (CollectionUtil.isEmpty(data)) {
        List<Dept> data = deptService.getDeptTree(query).getData();

//            redisTemplate.opsForValue().set(redisKey, data);
//            redisTemplate.expire(redisKey, 2, TimeUnit.HOURS);
//        }
        return Result.success(data);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "新增部门")
    @PostMapping
    public Result add(@Valid @RequestBody DeptDTO dto) {
        LoginUser loginUser = this.getLoginUser();
//        if (loginUser.getPermissionLevel()) {
//            return Result.noAuthority();
//        }
        Dept dept = new Dept();
        BeanUtil.copyProperties(dto, dept);

        dept.setCompanyId(loginUser.getCompanyId());
        dept.setParentCompanyId(loginUser.getParentCompanyId());

        int insert = deptMapper.insert(dept);
        if (insert > 0) {
            redisTemplate.delete(RedisKey.DEPT_TREE);
            return Result.success();
        }

        return Result.failure("添加失败");
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新部门")
    @PutMapping
    public Result update(@Valid @RequestBody DeptDTO dto) {
        LoginUser loginUser = this.getLoginUser();
//        if (loginUser.getPermissionLevel()) {
//            return Result.noAuthority();
//        }
        Dept dept = new Dept();
        BeanUtil.copyProperties(dto, dept);

        int update = deptMapper.updateById(dept);
        if (update > 0) {
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

