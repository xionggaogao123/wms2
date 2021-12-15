package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.config.JudgeConfig;
import com.huanhong.wms.entity.ShelfManagement;
import com.huanhong.wms.entity.WarehouseAreaManagement;
import com.huanhong.wms.entity.dto.UpdateShelfDTO;
import com.huanhong.wms.entity.vo.ShelfVO;
import com.huanhong.wms.mapper.ShelfManagementMapper;
import com.huanhong.wms.service.IShelfManagementService;
import com.huanhong.wms.service.IWarehouseAreaManagementService;
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
@RequestMapping("/v1/shelf-management")
@ApiSort()
@Api(tags = "货架管理")
public class ShelfManagementController extends BaseController {

    @Resource
    private IShelfManagementService shelfManagementService;

    @Resource
    private IWarehouseAreaManagementService warehouseAreaManagementService;

    @Resource
    private ShelfManagementMapper shelfManagementMapper;

    @Autowired
    private JudgeConfig judgeConfig;

    public static final Logger LOGGER = LoggerFactory.getLogger(SublibraryManagementController.class);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页组合查询货架")
    @GetMapping("/pagingFuzzyQuery")
    public Result<Page<ShelfManagement>> page(@RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer size,
                                              ShelfVO shelfVO//查询条件封装的对象
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<ShelfManagement> pageResult = shelfManagementService.pageFuzzyQuery(new Page<>(current, size), shelfVO);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.failure("查询失败--异常：" + e);
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加货架管理")
    @PostMapping
    public Result add(@Valid @RequestBody ShelfManagement shelfManagement) {

        /**
         * 判断是否有必填参数为空
         */
        try {
            /**
             * 实体类转为json
             */
            String shelfManagementToJoStr = JSONObject.toJSONString(shelfManagement);
            JSONObject shelfManagementJo = JSONObject.parseObject(shelfManagementToJoStr);
            /**
             * 不能为空的参数list
             * 配置于judge.properties
             */
            List<String> list = judgeConfig.getShelfNotNullList();
            /**
             * 将NotNullList中的值当作key判断value是否为空
             */
            for (int i = 0; i < list.size(); i++) {
                String key = list.get(i);
                if (StringUtils.isBlank(shelfManagementJo.getString(key)) || "null".equals(shelfManagementJo.getString(key))) {
                    return Result.failure(ErrorCode.PARAM_FORMAT_ERROR, key + ": 不能为空");
                }
            }
        } catch (Exception e) {
            LOGGER.error("添加货架失败--判断参数空值出错,异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--判空失败，请稍后再试或联系管理员");
        }
        /**
         * 在此处查重
         */
        try {
            ShelfManagement shelfManagementIsExist = shelfManagementService.getShelfByShelfId(shelfManagement.getShelfId());
            WarehouseAreaManagement warehouseAreaManagementIsExist = warehouseAreaManagementService.getWarehouseAreaByWarehouseAreaId(shelfManagement.getWarehouseAreaId());
            //判断库区是否存在
            if (ObjectUtil.isEmpty(warehouseAreaManagementIsExist)) {
                return Result.failure(ErrorCode.DATA_IS_NULL, "库区不存在,无法添加货架");
            }
            //货架编号重复判定
            if (ObjectUtil.isNotEmpty(shelfManagementIsExist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "货架编号重复,货架已存在");
            }
            try {
                int insert = shelfManagementMapper.insert(shelfManagement);
                if (insert > 0) {
                    LOGGER.info("添加货架成功");
                } else {
                    LOGGER.error("添加货架失败");
                }
                return render(insert > 0);
            } catch (Exception e) {
                LOGGER.error("添加货架错误--（插入数据）失败,异常：" + e);
                return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--插入数据失败，请稍后再试或联系管理员");
            }
        } catch (Exception e) {
            LOGGER.error("添加货架失败--处理（判断库区编码重复）失败,异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--判重失败，请稍后再试或联系管理员");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新货架管理", notes = "生成代码")
    @PutMapping
    public Result updateShelfByShelfId(@Valid @RequestBody UpdateShelfDTO updateShelfDTO) {
        UpdateWrapper<ShelfManagement> updateWrapper = new UpdateWrapper<>();
        try {
            //空值判断
            if (StringUtils.isBlank(updateShelfDTO.getShelfId())) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "货架编码不能为空");
            }
            //判断更新的货架是否存在
            ShelfManagement shelfManagementIsExist = shelfManagementService.getShelfByShelfId(updateShelfDTO.getShelfId());
            if (ObjectUtil.isEmpty(shelfManagementIsExist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "无此货架编码，货架不存在");
            }
            updateWrapper.eq("shelf_id", updateShelfDTO.getShelfId());
            ShelfManagement updateShlef = new ShelfManagement();
            BeanUtil.copyProperties(updateShelfDTO, updateShlef);
            int update = shelfManagementMapper.update(updateShlef, updateWrapper);
            return render(update > 0);
        } catch (Exception e) {
            LOGGER.error("更新库区信息出错--更新失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：库区更新失败，请稍后再试或联系管理员");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除货架管理", notes = "生成代码")
    @DeleteMapping("/{shelfId}")
    public Result delete(@PathVariable String shelfId) {
        try {
            ShelfManagement shelfIsExist = shelfManagementService.getShelfByShelfId(shelfId);
            if (ObjectUtil.isEmpty(shelfIsExist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "无此货架编码");
            }
            QueryWrapper<ShelfManagement> wrapper = new QueryWrapper<>();
            wrapper.eq("shelf_id", shelfId);
            int i = shelfManagementMapper.delete(wrapper);
            LOGGER.info("货架: " + shelfId + " 删除成功");
            return render(i > 0);
        } catch (Exception e) {
            LOGGER.error("删除货架信息出错--删除失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：货架删除失败，请稍后再试或联系管理员");
        }
    }
}

