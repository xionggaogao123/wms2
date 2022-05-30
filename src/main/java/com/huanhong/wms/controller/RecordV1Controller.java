package com.huanhong.wms.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.dto.request.RecordRequest;
import com.huanhong.wms.entity.Record;
import com.huanhong.wms.service.RecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @Author wang
 * @date 2022/5/30 20:32
 */
@RestController
@RequestMapping("/v1/RecordV1")
@ApiSort()
@Api(tags = "出入库明细")
public class RecordV1Controller {

    @Resource
    private RecordService recordService;

    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "查询物料出入库记录", notes = "生成代码")
    @GetMapping("/selectList")
    public Result add(@Valid @RequestBody RecordRequest recordRequest) {
        List<Record> records = recordService.getRecords(recordRequest);
        return Result.success(records);
    }
}
