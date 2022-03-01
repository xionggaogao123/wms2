package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.EnterWarehouseDetails;
import com.huanhong.wms.entity.dto.AddEnterWarehouseDetailsDTO;
import com.huanhong.wms.entity.dto.UpdateEnterWarehouseDetailsDTO;
import com.huanhong.wms.mapper.EnterWarehouseDetailsMapper;
import com.huanhong.wms.service.IEnterWarehouseDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1/enter-warehouse-details")
@ApiSort()
@Api(tags = "采购入库单明细表")
public class EnterWarehouseDetailsController extends BaseController {

    @Resource
    private IEnterWarehouseDetailsService enterWarehouseDetailsService;

    @Resource
    private EnterWarehouseDetailsMapper enterWarehouseDetailsMapper;

//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "current", value = "当前页码"),
//            @ApiImplicitParam(name = "size", value = "每页行数"),
//            @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
//    })
//    @ApiOperationSupport(order = 1)
//    @ApiOperation(value = "分页查询采购入库单明细表", notes = "生成代码")
//    @GetMapping("/page")
//    public Result<Page<EnterWarehouseDetails>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
//                                                    @RequestParam Map<String, Object> search) {
//        QueryWrapper<EnterWarehouseDetails> query = new QueryWrapper<>();
//        query.orderByDesc("id");
//        if (search.containsKey("search")) {
//            String text = search.get("search").toString();
//            if (StrUtil.isNotEmpty(text)) {
//                query.and(qw -> qw.like("title", text).or()
//                        .like("user_name", text)
//                );
//            }
//        }
//        return Result.success(enterWarehouseDetailsMapper.selectPage(new Page<>(current, size), query));
//    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加采购入库单明细表", notes = "生成代码")
    @PostMapping
    public Result add(@Valid @RequestBody AddEnterWarehouseDetailsDTO addEnterWarehouseDetailsDTO) {
        try {
            EnterWarehouseDetails enterWarehouseDetails = new EnterWarehouseDetails();
            BeanUtil.copyProperties(addEnterWarehouseDetailsDTO, enterWarehouseDetails);
            int insert = enterWarehouseDetailsMapper.insert(enterWarehouseDetails);
            return render(insert > 0);
        } catch (Exception e) {
            log.error("添加失败，系统异常：", e);
            return Result.failure("添加失败，系统异常：");
        }

    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新采购入库单明细表", notes = "生成代码")
    @PutMapping
    public Result update(@Valid @RequestBody UpdateEnterWarehouseDetailsDTO updateEnterWarehouseDetailsDTO) {
        try {
            EnterWarehouseDetails enterWarehouseDetails = new EnterWarehouseDetails();
            BeanUtil.copyProperties(updateEnterWarehouseDetailsDTO, enterWarehouseDetails);
            int update = enterWarehouseDetailsMapper.updateById(enterWarehouseDetails);
            return render(update > 0);
        } catch (Exception e) {
            log.error("更新失败，异常：", e);
            return Result.failure("更新失败，系统异常！");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除采购入库单明细表", notes = "生成代码")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        try {
            int i = enterWarehouseDetailsMapper.deleteById(id);
            return render(i > 0);
        } catch (Exception e) {
            log.error("删除失败，异常：", e);
            return Result.failure("删除失败，系统异常！");
        }
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据ID获取明细详细信息")
    @GetMapping("/getEnterWarehouseDetailsById/{id}")
    public EnterWarehouseDetails getEnterWarehouseDetailsById(@PathVariable Integer id) {
        return enterWarehouseDetailsService.getEnterWarehouseDetailsByDetailsID(id);
    }
}

