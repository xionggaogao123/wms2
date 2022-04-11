package com.huanhong.wms.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.annotion.OperateLog;
import com.huanhong.common.enums.OperateType;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.SysVisLog;
import com.huanhong.wms.service.ISysVisLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@ApiSort()
@Api(tags = "系统访问日志表")
@RestController
@RequestMapping("/sysVisLog")
public class SysVisLogController extends BaseController {

    @Resource
    private ISysVisLogService sysVisLogService;

    @OperateLog(title = "系统访问日志_分页", type = OperateType.QUERY)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
            @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询系统访问日志表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<SysVisLog>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                        @RequestParam Map<String, Object> search) {
        QueryWrapper<SysVisLog> query = new QueryWrapper<>();
        query.orderByDesc("id");
        if (search.containsKey("search")) {
            String text = search.get("search").toString();
            if (StrUtil.isNotEmpty(text)) {
                query.and(qw -> qw.like("name", text)
                );
            }
        }
        return Result.success(sysVisLogService.page(new Page<>(current, size), query));
    }



}

