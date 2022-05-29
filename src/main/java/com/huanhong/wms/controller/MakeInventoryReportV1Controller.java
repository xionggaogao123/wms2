package com.huanhong.wms.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.dto.request.UpdateMakeInventoryReportRequest;
import com.huanhong.wms.entity.dto.UpdateMakeInventoryReportAndDetailsDTO;
import com.huanhong.wms.service.MakeInventoryReportV1Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * @Author wang
 * @date 2022/5/28 19:47
 */
@ApiSort()
@Slf4j
@Validated
@Api(tags = "盘点报告管理V1")
@RestController
@RequestMapping("/v1/make-inventory-report-v1")
public class MakeInventoryReportV1Controller {

    @Resource
    private MakeInventoryReportV1Service makeInventoryReportService;

    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "更新", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateMakeInventoryReportRequest request) {
        try {
           return makeInventoryReportService.update(request);
        } catch (Exception e) {
            log.error("系统异常：更新盘点报告主表及明细失败！",e);
            return Result.failure("系统异常，更新失败");
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "更新", notes = "生成代码")
    @PutMapping("/selectById/{id}")
    public Result selectById(@PathVariable("id") Integer id) {
        try {
            Map map = makeInventoryReportService.selectById(id);
            return Result.success(map);
        } catch (Exception e) {
            log.error("系统异常：更新盘点报告主表及明细失败！:{}",e.getMessage());
            return Result.failure("系统异常，更新失败");
        }
    }
}
