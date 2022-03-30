package com.huanhong.wms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Device;
import com.huanhong.wms.service.IDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@ApiSort()
@Api(tags = "设备表")
@RestController
@RequestMapping("/v1/device")
public class DeviceController extends BaseController {

    @Resource
    private IDeviceService deviceService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
            @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询设备表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<Device>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam Map<String, Object> search) {
        QueryWrapper<Device> query = new QueryWrapper<>();
        query.orderByDesc("id");
        if (search.containsKey("search")) {
            String text = search.get("search").toString();
            if (StrUtil.isNotEmpty(text)) {
                query.and(qw -> qw.like("title", text).or()
                        .like("device_name", text)
                );
            }
        }
        return Result.success(deviceService.page(new Page<>(current, size), query));
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加设备表", notes = "生成代码")
    @PostMapping
    public Result add(@Valid @RequestBody Device device) {
        return render(deviceService.save(device));
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新设备表", notes = "生成代码")
    @PutMapping
    public Result update(@Valid @RequestBody Device device) {
        return render(deviceService.updateById(device));
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除设备表", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return render(deviceService.removeById(id));
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据子库查询设备表")
    @GetMapping("/list")
    public Result<List<Device>> list(Device device) {
        QueryWrapper<Device> query = new QueryWrapper<>();
        query.orderByDesc("sort");

        List<Device> list = deviceService.list(Wrappers.<Device>lambdaQuery()
                .eq(null != device.getWarehouseId(), Device::getWarehouseId, device.getWarehouseId())
                .eq(null != device.getSublibraryId(), Device::getSublibraryId, device.getSublibraryId()));
        return Result.success(list);
    }
}

