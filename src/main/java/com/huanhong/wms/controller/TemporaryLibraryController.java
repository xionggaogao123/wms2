package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.entity.Material;
import com.huanhong.wms.entity.dto.AddTemporaryLibraryDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryLibraryDTO;
import com.huanhong.wms.entity.vo.TemporaryLibraryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.entity.TemporaryLibrary;
import com.huanhong.wms.mapper.TemporaryLibraryMapper;
import com.huanhong.wms.service.ITemporaryLibraryService;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@ApiSort()
@Api(tags = "临库库存表")
@RestController
@RequestMapping("/v1/temporary-library")
public class TemporaryLibraryController extends BaseController {

    @Resource
    private ITemporaryLibraryService temporaryLibraryService;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数")
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询临库库存表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<TemporaryLibrary>> page(@RequestParam(defaultValue = "1") Integer current,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               TemporaryLibraryVO temporaryLibraryVO
                                               ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<TemporaryLibrary> pageResult = temporaryLibraryService.pageFuzzyQuery(new Page<>(current, size), temporaryLibraryVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到库存信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
        }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加临库库存表", notes = "生成代码")
        @PostMapping("/add")
        public Result add(@Valid @RequestBody AddTemporaryLibraryDTO addTemporaryLibraryDTO) {
            try {
                return temporaryLibraryService.addTemporaryLibrary(addTemporaryLibraryDTO);
            } catch (Exception e) {
                log.error("库存新增错误--（插入数据）失败,异常：", e);
                return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--插入数据失败，请稍后再试或联系管理员");
            }
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新临库库存表", notes = "生成代码")
        @PutMapping("/update")
        public Result update(@Valid @RequestBody UpdateTemporaryLibraryDTO updateTemporaryLibraryDTO) {
            try {
                return temporaryLibraryService.updateTemporaryLibrary(updateTemporaryLibraryDTO);
            } catch (Exception e) {
                log.error("库存更新失败，异常：", e);
                return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：更新失败，请稍后再试或联系管理员");
            }
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除临库库存表", notes = "生成代码")
        @DeleteMapping("deleteById/{id}")
        public Result delete(@PathVariable Integer id) {

        return render(temporaryLibraryService.removeById(id));

        }

        @ApiImplicitParams({
                @ApiImplicitParam(name = "id", value = "单据Id"),
        })
        @ApiOperationSupport(order = 5)
        @ApiOperation(value = "根据ID获取库存信息")
        @GetMapping("getTemporaryLibraryById/{id}")
        public Result getTemporaryLibraryById(@PathVariable Integer id) {
            try {
                TemporaryLibrary temporaryLibrary = temporaryLibraryService.getTemporaryLibraryById(id);
                return ObjectUtil.isNotNull(temporaryLibrary)? Result.success(temporaryLibrary):Result.failure("未查询到相关信息！");
            } catch (Exception e) {
                log.error("查询失败,异常：", e);
                return Result.failure("查询失败，系统异常！");
            }
        }

}

