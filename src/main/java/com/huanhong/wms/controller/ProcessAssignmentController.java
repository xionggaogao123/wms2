package com.huanhong.wms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.annotion.OperateLog;
import com.huanhong.common.enums.OperateType;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ProcessAssignment;
import com.huanhong.wms.entity.dto.UpPaStatus;
import com.huanhong.wms.entity.param.ApproveParam;
import com.huanhong.wms.entity.param.ProcessAssignmentParam;
import com.huanhong.wms.entity.param.StartProcessParam;
import com.huanhong.wms.mapper.ProcessAssignmentMapper;
import com.huanhong.wms.service.IProcessAssignmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/process-task")
@ApiSort()
@Api(tags = "流程任务表")
public class ProcessAssignmentController extends BaseController {

    @Resource
    private IProcessAssignmentService processAssignmentService;
    @Resource
    private ProcessAssignmentMapper processAssignmentMapper;

    @OperateLog(title = "分页查询流程任务表", type = OperateType.QUERY)
    @ApiOperation(value = "分页查询流程任务表")
    @ApiOperationSupport(ignoreParameters = {"countId", "records", "hitCount", "maxLimit", "optimizeCountSql", "orders", "searchCount"})
    @GetMapping("/page")
    public Result<Page<ProcessAssignment>> page(Page<ProcessAssignment> page, ProcessAssignmentParam param) {
        LoginUser loginUser = this.getLoginUser();
        param.setUserAccount(loginUser.getLoginName());
        return processAssignmentService.selectPage(page, param);
    }

    @OperateLog(title = "更新流程审批状态", type = OperateType.UPDATE)
    @ApiOperation(value = "更新流程审批状态")
    @PostMapping("/status")
    public Result<Integer> update(@Valid @RequestBody UpPaStatus up) {
        return processAssignmentService.updateProcessAssignmentStatusByParam(up);
    }

    @OperateLog(title = "删除流程任务表", type = OperateType.DELETE)
    @ApiOperation(value = "删除流程任务表", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        int i = processAssignmentMapper.deleteById(id);
        return render(i > 0);
    }

    @OperateLog(title = "审批", type = OperateType.APPROVE)
    @ApiOperation(value = "审批")
    @PostMapping("/approve")
    public Result<Integer> approve(@Valid @RequestBody ApproveParam param) {
        LoginUser loginUser = this.getLoginUser();
        param.setUserId(loginUser.getId());
        param.setName(loginUser.getUserName());
        return processAssignmentService.approveTaskByParam(param);
    }

    @OperateLog(title = "启动流程", type = OperateType.START)
    @ApiOperation(value = "启动流程")
    @PostMapping("/start")
    public Result<String> start(@Valid @RequestBody StartProcessParam param) {
        return processAssignmentService.start(param);
    }


}

