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
import com.huanhong.wms.entity.WarehouseAreaManagement;
import com.huanhong.wms.entity.dto.AddWarehouseAreaDTO;
import com.huanhong.wms.entity.dto.UpdateWarehouseAreaDTO;
import com.huanhong.wms.entity.vo.WarehouseAreaVO;
import com.huanhong.wms.mapper.WarehouseAreaManagementMapper;
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
@RequestMapping("/v1/warehouse-area-management")
@ApiSort()
@Api(tags = "库房区域管理")
public class WarehouseAreaManagementController extends BaseController {

    @Resource
    private IWarehouseAreaManagementService warehouseAreaManagementService;

    @Resource
    private WarehouseAreaManagementMapper warehouseAreaManagementMapper;

    @Autowired
    private JudgeConfig judgeConfig;

    public static final Logger LOGGER = LoggerFactory.getLogger(SublibraryManagementController.class);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页组合查询库区")
    @GetMapping("/pagingFuzzyQuery")
    public Result<Page<WarehouseAreaManagement>> page(@RequestParam(defaultValue = "1") Integer current,
                                                      @RequestParam(defaultValue = "10") Integer size,
                                                      WarehouseAreaVO warehouseAreaVO //查询条件封装的对象
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<WarehouseAreaManagement> pageResult = warehouseAreaManagementService.pageFuzzyQuery(new Page<>(current, size), warehouseAreaVO);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.failure("查询失败--异常：" + e);
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加库房区域管理", notes = "生成代码")
    @PostMapping
    public Result add(@Valid @RequestBody AddWarehouseAreaDTO addWarehouseAreaDTO) {

        /**
         * 判断是否有必填参数为空
         */
        try {
            /**
             * 实体类转为json
             */
            String warehouseAreaManagementToJoStr = JSONObject.toJSONString(addWarehouseAreaDTO);
            JSONObject warehouseAreaManagementJo = JSONObject.parseObject(warehouseAreaManagementToJoStr);
            /**
             * 不能为空的参数list
             * 配置于judge.properties
             */
            List<String> list = judgeConfig.getWarehouseAreaNotNullList();
            /**
             * 将NotNullList中的值当作key判断value是否为空
             */
            for (int i = 0; i < list.size(); i++) {
                String key = list.get(i);
                if (StringUtils.isBlank(warehouseAreaManagementJo.getString(key)) || "null".equals(warehouseAreaManagementJo.getString(key))) {
                    return Result.failure(ErrorCode.PARAM_FORMAT_ERROR, key + ": 不能为空");
                }
            }
        } catch (Exception e) {
            LOGGER.error("添加库区失败--判断参数空值出错,异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--判空失败，请稍后再试或联系管理员");
        }
        /**
         * 在此处查重
         */
        try {
            WarehouseAreaManagement warehouseAreaIsExist = warehouseAreaManagementService.getWarehouseAreaByWarehouseAreaId(addWarehouseAreaDTO.getWarehouseAreaId());
            if (ObjectUtil.isNotEmpty(warehouseAreaIsExist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "库区编号重复,库区已存在");
            }
            try {
                WarehouseAreaManagement warehouseAreaManagement = new WarehouseAreaManagement();
                BeanUtil.copyProperties(addWarehouseAreaDTO, warehouseAreaManagement);
                int insert = warehouseAreaManagementMapper.insert(warehouseAreaManagement);
                if (insert > 0) {
                    LOGGER.info("添加库区成功");
                } else {
                    LOGGER.error("添加库区失败");
                }
                return render(insert > 0);
            } catch (Exception e) {
                LOGGER.error("添加库区错误--（插入数据）失败,异常：" + e);
                return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--插入数据失败，请稍后再试或联系管理员");
            }
        } catch (Exception e) {
            LOGGER.error("添加库区失败--处理（判断库区编码重复）失败,异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--判重失败，请稍后再试或联系管理员");
        }

    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新库房区域管理", notes = "生成代码")
    @PutMapping
    public Result update(@Valid @RequestBody UpdateWarehouseAreaDTO updateWarehouseAreaDTO) {
        UpdateWrapper updateWrapper = new UpdateWrapper<>();
        try {
            //防止传空值
            if (StringUtils.isBlank(updateWarehouseAreaDTO.getWarehouseAreaId())) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "库区为空");
            }
            //判断更新的库区是否存在
            WarehouseAreaManagement warehouseAreaIsExist = warehouseAreaManagementService.getWarehouseAreaByWarehouseAreaId(updateWarehouseAreaDTO.getWarehouseAreaId());
            if (ObjectUtil.isEmpty(warehouseAreaIsExist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "无此库区编码，库区不存在");
            }
            WarehouseAreaManagement warehouseAreaManagement = new WarehouseAreaManagement();
            BeanUtil.copyProperties(updateWarehouseAreaDTO,warehouseAreaManagement);
            updateWrapper.eq("warehouse_area_id", updateWarehouseAreaDTO.getWarehouseAreaId());
            int update = warehouseAreaManagementMapper.update(warehouseAreaManagement, updateWrapper);
            return render(update > 0);
        } catch (Exception e) {
            LOGGER.error("更新库区信息出错--更新失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：库区更新失败，请稍后再试或联系管理员");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除库房区域管理", notes = "生成代码")
    @DeleteMapping("/{warehouseAreaId}")
    public Result delete(@PathVariable String warehouseAreaId) {
        QueryWrapper<WarehouseAreaManagement> queryWrapper = new QueryWrapper<>();
        try {
            queryWrapper.eq("warehouse_area_id", warehouseAreaId);
            WarehouseAreaManagement warehouseAreaManagement = warehouseAreaManagementMapper.selectOne(queryWrapper);
            if (ObjectUtil.isEmpty(warehouseAreaManagement)) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "操作失败：库区编号不存在");
            }
            int i = warehouseAreaManagementMapper.delete(queryWrapper);
            LOGGER.info("库区:  " + warehouseAreaId + "删除成功");
            return render(i > 0);
        } catch (Exception e) {
            LOGGER.error("删除库区信息出错--删除失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：库区删除失败，请稍后再试或联系管理员");
        }
    }
}

