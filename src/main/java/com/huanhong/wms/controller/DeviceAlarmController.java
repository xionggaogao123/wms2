package com.huanhong.wms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.entity.DeviceAlarm;
import com.huanhong.wms.mapper.DeviceAlarmMapper;
import com.huanhong.wms.service.IDeviceAlarmService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@ApiSort()
@Api(tags = "设备报警表")
@RestController
@RequestMapping("/device-alarm")
public class DeviceAlarmController extends BaseController {

    @Resource
    private IDeviceAlarmService deviceAlarmService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
            @ApiImplicitParam(name = "deviceNo", value = "设备号"),
            @ApiImplicitParam(name = "sublibraryId", value = "子库编号"),
            @ApiImplicitParam(name = "warehouseId", value = "库区编号"),
            @ApiImplicitParam(name = "gmtStart", value = "开始时间"),
            @ApiImplicitParam(name = "gmtEnd", value = "结束时间"),
            @ApiImplicitParam(name = "deviceName", value = "设备名"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询设备报警表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<DeviceAlarm>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam Map<String, Object> search) {
        QueryWrapper<DeviceAlarm> query = new QueryWrapper<>();
        query.orderByDesc("id");
        if (search.containsKey("deviceName")) {
            String text = search.get("deviceName").toString();
            if (StrUtil.isNotEmpty(text)) {
                query.and(qw -> qw.like("device_name", text)
                );
            }
        }
        if (search.containsKey("deviceNo")) {
            String text = search.get("deviceNo").toString();
            if (StrUtil.isNotEmpty(text)) {
                query.and(qw -> qw.eq("device_no", text)
                );
            }
        }
        if (search.containsKey("sublibraryId")) {
            String text = search.get("sublibraryId").toString();
            if (StrUtil.isNotEmpty(text)) {
                query.and(qw -> qw.eq("sublibrary_id", text)
                );
            }
        }
        if (search.containsKey("warehouseId")) {
            String text = search.get("warehouseId").toString();
            if (StrUtil.isNotEmpty(text)) {
                query.and(qw -> qw.eq("warehouse_id", text)
                );
            }
        }
        if (search.containsKey("gmtStart")) {
            String text = search.get("gmtStart").toString();
            if (StrUtil.isNotEmpty(text)) {
                query.and(qw -> qw.ge("create_time", text)
                );
            }
        }
        if (search.containsKey("gmtEnd")) {
            String text = search.get("gmtEnd").toString();
            if (StrUtil.isNotEmpty(text)) {
                query.and(qw -> qw.le("create_time", text)
                );
            }
        }


        return Result.success(deviceAlarmService.page(new Page<>(current, size), query));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加设备报警表", notes = "生成代码")
    @PostMapping
    public Result add(@Valid @RequestBody DeviceAlarm deviceAlarm) {
        return render(deviceAlarmService.save(deviceAlarm));
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新设备报警表", notes = "生成代码")
    @PutMapping
    public Result update(@Valid @RequestBody DeviceAlarm deviceAlarm) {
        return render(deviceAlarmService.updateById(deviceAlarm));
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除设备报警表", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return render(deviceAlarmService.removeById(id));
    }


}

