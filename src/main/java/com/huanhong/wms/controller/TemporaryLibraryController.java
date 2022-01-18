package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.CargoSpaceManagement;
import com.huanhong.wms.entity.TemporaryLibrary;
import com.huanhong.wms.entity.dto.AddTemporaryLibraryDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryLibraryDTO;
import com.huanhong.wms.entity.vo.TemporaryLibraryVO;
import com.huanhong.wms.mapper.TemporaryLibraryMapper;
import com.huanhong.wms.service.ICargoSpaceManagementService;
import com.huanhong.wms.service.ITemporaryLibraryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/v1/temporary-library")
    @ApiSort(10)
    @Api(tags = "临库管理")
    public class TemporaryLibraryController extends BaseController {

    @Resource
    private ITemporaryLibraryService temporaryLibraryService;

    @Resource
    private TemporaryLibraryMapper temporaryLibraryMapper;

    @Resource
    private ICargoSpaceManagementService cargoSpaceManagementService;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/pagingFuzzyQuery")
    public Result<Page<TemporaryLibrary>> page(@RequestParam(defaultValue = "1") Integer current,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               TemporaryLibraryVO temporaryLibraryVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<TemporaryLibrary> pageResult = temporaryLibraryService.pageFuzzyQuery(new Page<>(current, size), temporaryLibraryVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到库存信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常",e);
            return Result.failure("查询失败--系统异常，请联系管理员");
            }
       }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加", notes = "生成代码")
        @PostMapping("/add")
        public Result add(@Valid @RequestBody AddTemporaryLibraryDTO addTemporaryLibraryDTO) {
            try {
                /**
                 * 判断货位是否存在
                 */
                CargoSpaceManagement cargoSpaceManagement = cargoSpaceManagementService.getCargoSpaceByCargoSpaceId(addTemporaryLibraryDTO.getCargoSpaceId());
                if (ObjectUtil.isEmpty(cargoSpaceManagement)) {
                    return Result.failure(ErrorCode.DATA_IS_NULL, "货位不存在！");
                }
                TemporaryLibrary temporaryLibrary = new TemporaryLibrary();
                BeanUtil.copyProperties(addTemporaryLibraryDTO,temporaryLibrary);
                int insert = temporaryLibraryMapper.insert(temporaryLibrary);
                return render(insert > 0);
            }catch (Exception e){
                log.error("临库新增插入数据失败，异常：",e);
                return Result.failure("临库添加数据失败--系统异常，请联系管理员");
            }
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新", notes = "生成代码")
        @PutMapping("/update")
        public Result update(@Valid @RequestBody UpdateTemporaryLibraryDTO updateTemporaryLibraryDTO) {
        try {
            TemporaryLibrary temporaryLibrary = new TemporaryLibrary();
            BeanUtil.copyProperties(updateTemporaryLibraryDTO,temporaryLibrary);
            int update = temporaryLibraryService.updateTemporaryLibrary(temporaryLibrary);
            return render(update > 0);
        }catch (Exception e){
            log.error("临库更新失败，异常：",e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：更新失败，请稍后再试或联系管理员");
        }
}

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除", notes = "生成代码")
        @DeleteMapping("/deleteByMaterialCodeAndBatch/{materialCode}&{batch}")
        public Result delete(@PathVariable("materialCode") String materialCode, @PathVariable("batch") String batch) {
            try {
                QueryWrapper<TemporaryLibrary> wrapper = new QueryWrapper<>();
                wrapper.eq("material_coding", materialCode);
                wrapper.eq("batch", batch);
                int i = temporaryLibraryMapper.delete(wrapper);
                return render(i > 0);
            } catch (Exception e) {
                log.error("物料下架出错--删除失败，异常：",e);
                return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：物料下架失败，请稍后再试或联系管理员");
            }
        }
}

