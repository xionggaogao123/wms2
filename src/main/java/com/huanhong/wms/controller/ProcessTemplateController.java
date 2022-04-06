package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ProcessTemplate;
import com.huanhong.wms.entity.dto.AddProcessTemplateDTO;
import com.huanhong.wms.entity.dto.UpdateProcessTemplateDTO;
import com.huanhong.wms.entity.vo.ProcessTemplateVO;
import com.huanhong.wms.mapper.ProcessTemplateMapper;
import com.huanhong.wms.service.IProcessTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1//process-template")
@Validated
@ApiSort()
@Api(tags = "流程预设管理")
public class ProcessTemplateController extends BaseController {

    @Resource
    private IProcessTemplateService processTemplateService;
    @Resource
    private ProcessTemplateMapper processTemplateMapper;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<ProcessTemplate>> page(@RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer size,
                                              ProcessTemplateVO processTemplateVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<ProcessTemplate> pageResult = processTemplateService.pageFuzzyQuery(new Page<>(current, size), processTemplateVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到流程预设信息！");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }


    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@RequestBody @Valid List<AddProcessTemplateDTO> listAddProcessTemplateDTO) {
        return processTemplateService.addProcessTemplate(listAddProcessTemplateDTO);
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateProcessTemplateDTO updateProcessTemplateDTO) {
        return processTemplateService.updateProcessTemplate(updateProcessTemplateDTO);
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        int i = processTemplateMapper.deleteById(id);
        return render(i > 0);
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据Id查询流程预设信息-单条")
    @GetMapping("/getProcessTemplateById")
    public Result getProcessTemplateById(@RequestParam Integer id) {
        ProcessTemplate processTemplate = processTemplateService.getProcessTemplateById(id);
        return ObjectUtil.isNotNull(processTemplate) ? Result.success(processTemplate) : Result.failure("未查询到相关信息！");
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "templateType", value = "模版类型 1.审批人 2.抄送人"),
            @ApiImplicitParam(name = "deptId", value = "部门 id"),
            @ApiImplicitParam(name = "processCode", value = "流程代码"),
            @ApiImplicitParam(name = "warhouseId", value = "仓库编号")
    })
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "根据流程代码和库房编号获取此流程预设的完整数据")
    @GetMapping("/getProcessTemplateByProcessCodeAndWarhouseId")
    public Result getProcessTemplateByProcessCodeAndWarhouseId(
            @RequestParam String processCode,
            @RequestParam Integer templateType,
            @RequestParam Integer deptId,
            @RequestParam String warhouseId) {
        List<ProcessTemplate> processTemplateList = processTemplateService.getProcessTemplateListByProcessCodeAndWarhouseId(processCode, warhouseId, templateType, deptId);
        return ObjectUtil.isNotNull(processTemplateList) ? Result.success(processTemplateList) : Result.failure("查询到相关信息！");
    }
}

