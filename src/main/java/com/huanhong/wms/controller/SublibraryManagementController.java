package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.units.StrUtils;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.config.JudgeConfig;
import com.huanhong.wms.entity.SublibraryManagement;
import com.huanhong.wms.entity.WarehouseAreaManagement;
import com.huanhong.wms.entity.WarehouseManagement;
import com.huanhong.wms.entity.dto.AddSubliraryDTO;
import com.huanhong.wms.entity.dto.UpdateSubliraryDTO;
import com.huanhong.wms.entity.vo.SublibraryVO;
import com.huanhong.wms.mapper.SublibraryManagementMapper;
import com.huanhong.wms.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/sublibrary-management")
@ApiSort()
@Api(tags = "子库管理")
public class SublibraryManagementController extends BaseController {

    @Resource
    private ISublibraryManagementService sublibraryManagementService;

    @Resource
    private SublibraryManagementMapper sublibraryManagementMapper;

    @Resource
    private IWarehouseManagementService warehouseManagementService;

    @Resource
    private IWarehouseAreaManagementService warehouseAreaManagementService;

    @Resource
    private IShelfManagementService shelfManagementService;

    @Resource
    private ICargoSpaceManagementService cargoSpaceManagementService;

    @Autowired
    private JudgeConfig judgeConfig;

    public static final Logger LOGGER = LoggerFactory.getLogger(SublibraryManagementController.class);


    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询组合子库")
    @GetMapping("/pagingFuzzyQuery")
    public Result<Page<SublibraryManagement>> page(@RequestParam(defaultValue = "1") Integer current,
                                                   @RequestParam(defaultValue = "10") Integer size,
                                                   SublibraryVO sublibraryVO //查询条件封装的对象
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<SublibraryManagement> pageResult = sublibraryManagementService.pageFuzzyQuery(new Page<>(current, size), sublibraryVO);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.failure("查询失败--异常：" + e);
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加子库管理", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddSubliraryDTO addSubliraryDTO) {


        /**
         * 在此处查重
         */
        try {

            /**
             * 查看子库是否存在
             */
            WarehouseManagement warehouseManagement = warehouseManagementService.getWarehouseByWarehouseId(addSubliraryDTO.getWarehouseId());
            if (ObjectUtil.isEmpty(warehouseManagement)) {
                return Result.failure(ErrorCode.DATA_IS_NULL, "仓库不存在，无法添加子库");
            }

            /**
             * 查看子库是否停用
             */
            if (warehouseManagementService.isStopUsing(addSubliraryDTO.getWarehouseId())!=0){
                return Result.failure(ErrorCode.DATA_IS_NULL, "仓库停用，无法添加子库");
            }

            /**
             * 判断子库编号是否是两位数字
             */
            if (addSubliraryDTO.getSublibraryId().length() != 2 || !StrUtils.isNumeric(addSubliraryDTO.getSublibraryId())) {
                return Result.failure(ErrorCode.DATA_IS_NULL, "子库号为两位数字!");
            }

            /**
             *组合仓库编号和子库编号 为完整子库编号
             */
            addSubliraryDTO.setSublibraryId(addSubliraryDTO.getWarehouseId()+addSubliraryDTO.getSublibraryId());

            SublibraryManagement sublibrary = sublibraryManagementService.getSublibraryBySublibraryId(addSubliraryDTO.getSublibraryId());
            if (ObjectUtil.isNotEmpty(sublibrary)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "子库编号重复");
            }
            try {
                SublibraryManagement sublibraryManagement = new SublibraryManagement();
                BeanUtil.copyProperties(addSubliraryDTO, sublibraryManagement);
                int insert = sublibraryManagementMapper.insert(sublibraryManagement);
                if (insert > 0) {
                    LOGGER.info("添加子库成功");
                } else {
                    LOGGER.error("添加子库失败");
                }
                return render(insert > 0);
            } catch (Exception e) {
                LOGGER.error("添加子库错误--（插入数据）失败,异常：" + e);
                return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--插入数据失败，请稍后再试或联系管理员");
            }
        } catch (Exception e) {
            LOGGER.error("添加子库失败--处理（判断子库编码重复）失败,异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--判重失败，请稍后再试或联系管理员");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新子库管理", notes = "生成代码")
    @PutMapping("/update")
    public Result update(@Valid @RequestBody UpdateSubliraryDTO updateSubliraryDTO) {
        UpdateWrapper updateWrapper = new UpdateWrapper<>();
        try {
            if (StringUtils.isBlank(updateSubliraryDTO.getSublibraryId())) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "子库编号为空");
            }
            SublibraryManagement sublibraryIsExist = sublibraryManagementService.getSublibraryBySublibraryId(updateSubliraryDTO.getSublibraryId());
            if (ObjectUtil.isEmpty(sublibraryIsExist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "无此子库编码");
            }

            /**
             * 查询库区是否停用 0-使用中  1-单独停用
             * 若改为0则允许更新  反之则拒绝
             */
            //父级停用无法手动单独启用
            SublibraryManagement querySub = sublibraryManagementService.getSublibraryBySublibraryId(updateSubliraryDTO.getSublibraryId());
            if (warehouseManagementService.isStopUsing(querySub.getWarehouseId())==1){
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "仓库已停用,子库无法编辑！");
            }

            //父级可用可以手动修改更新为启用状态
            if (sublibraryManagementService.isStopUsing(updateSubliraryDTO.getSublibraryId())==1){
                if (updateSubliraryDTO.getStopUsing()!=0){
                    return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "子库已停用,无法编辑！");
                }
            }


            SublibraryManagement sublibraryManagement = new SublibraryManagement();
            BeanUtil.copyProperties(updateSubliraryDTO, sublibraryManagement);
            updateWrapper.eq("sublibrary_id", updateSubliraryDTO.getSublibraryId());
            int update = sublibraryManagementMapper.update(sublibraryManagement, updateWrapper);
            String parentCode = sublibraryManagement.getSublibraryId();

            if (update > 0&&ObjectUtil.isNotNull(sublibraryManagement.getStopUsing())) {
                //如果子库更新成功 判断此次更新子库是否处于启用状态
                if (sublibraryManagement.getStopUsing()==0){
                warehouseAreaManagementService.stopUsingByParentCode(parentCode,true);
                shelfManagementService.stopUsingByParentCode(parentCode,true);
                cargoSpaceManagementService.stopUsingByParentCode(parentCode,true);
                } else {
                //若是停用状态 则将停用状态为 0-启用 的子级全部停用
                warehouseAreaManagementService.stopUsingByParentCode(parentCode,false);
                shelfManagementService.stopUsingByParentCode(parentCode,false);
                cargoSpaceManagementService.stopUsingByParentCode(parentCode,false);
               }
                return Result.success("操作成功");
            }
            return Result.failure("操作失败");
        } catch (Exception e) {
            LOGGER.error("更新子库信息出错--更新失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：子库更新失败，请稍后再试或联系管理员");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除子库管理", notes = "生成代码")
    @DeleteMapping("delete/{sublibraryId}")
    public Result delete(@PathVariable String sublibraryId) {
        QueryWrapper<SublibraryManagement> queryWrapper = new QueryWrapper<>();
        try {

            List<WarehouseAreaManagement> warehouseAreaManagementList = warehouseAreaManagementService.getWarehouseAreaListBySublibraryId(sublibraryId);
            if (ObjectUtil.isNotEmpty(warehouseAreaManagementList)) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "此子库下存在库区，请先删除库区");
            }

            queryWrapper.eq("sublibrary_id", sublibraryId);
            SublibraryManagement sublibraryManagement = sublibraryManagementMapper.selectOne(queryWrapper);
            if (ObjectUtil.isEmpty(sublibraryManagement)) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "操作失败：子库编号不存在");
            }

            /**
             * 判断子库是否停用 若停用是否已经将stopUsing字段改为 0 -使用中
             * 若改为0则允许更新  反之则拒绝
             */
            if (sublibraryManagementService.isStopUsing(sublibraryId)!=0){
                    return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "子库已停用,无法删除！");
            }

            int i = sublibraryManagementMapper.delete(queryWrapper);
            LOGGER.info("子库:  " + sublibraryId + "删除成功");
            return render(i > 0);
        } catch (Exception e) {
            LOGGER.error("删除子库信息出错--删除失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：子库删除失败，请稍后再试或联系管理员");
        }
    }

    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "获取子库详细信息")
    @GetMapping("/getSubliraryBySubliraryId/{subliraryId}")
    public Result getSublirary(@PathVariable String subliraryId) {
        try {
            SublibraryManagement sublibraryManagement = sublibraryManagementService.getSublibraryBySublibraryId(subliraryId);
            return Result.success(sublibraryManagement);
        } catch (Exception e) {
            return Result.failure(ErrorCode.SYSTEM_ERROR, "获取子库信息失败");
        }
    }

    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "获取对应子库的所有库区")
    @GetMapping("/getWarehouseAreaBySubliraryId/{subliraryId}")
    public Result getAllWarehouseArea(@PathVariable String subliraryId) {
        try {
            List<WarehouseAreaManagement> warehouseAreaManagementList = warehouseAreaManagementService.getWarehouseAreaListBySublibraryId(subliraryId);
            return Result.success(warehouseAreaManagementList);
        } catch (Exception e) {
            return Result.failure(ErrorCode.SYSTEM_ERROR, "获取库区信息失败");
        }
    }

}

