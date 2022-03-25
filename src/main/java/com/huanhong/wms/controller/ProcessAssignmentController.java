package com.huanhong.wms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ProcessAssignment;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.entity.dto.UpPaStatus;
import com.huanhong.wms.entity.param.ApproveParam;
import com.huanhong.wms.entity.param.ProcessAssignmentParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import com.huanhong.wms.BaseController;
import com.huanhong.wms.mapper.ProcessAssignmentMapper;
import com.huanhong.wms.service.IProcessAssignmentService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/v1/process-task")
@ApiSort()
@Api(tags = "流程任务表")
public class ProcessAssignmentController extends BaseController {

    @Resource
    private IProcessAssignmentService processAssignmentService;
    @Resource
    private ProcessAssignmentMapper processAssignmentMapper;


    @ApiOperation(value = "分页查询流程任务表")
    @ApiOperationSupport(ignoreParameters = {"countId", "records", "hitCount", "maxLimit", "optimizeCountSql", "orders", "searchCount"})
    @GetMapping("/page")
    public Result<Page<ProcessAssignment>> page(Page<ProcessAssignment> page, ProcessAssignmentParam param) {
        LoginUser loginUser = this.getLoginUser();
        param.setUserAccount(loginUser.getLoginName());
        return processAssignmentService.selectPage(page, param);
    }


    @ApiOperation(value = "更新流程审批状态")
    @PostMapping("/status")
    public Result<Integer> update(@Valid @RequestBody UpPaStatus up) {
        return processAssignmentService.updateProcessAssignmentStatusByParam(up);
    }

    @ApiOperation(value = "删除流程任务表", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        int i = processAssignmentMapper.deleteById(id);
        return render(i > 0);
    }

    @ApiOperation(value = "审批")
    @PostMapping("/approve")
    public Result<Integer> approve(@Valid @RequestBody ApproveParam param) {
        LoginUser loginUser = this.getLoginUser();
        param.setUserId(loginUser.getId());
        param.setName(loginUser.getUserName());
        return processAssignmentService.approveTaskByParam(param);
    }



}

