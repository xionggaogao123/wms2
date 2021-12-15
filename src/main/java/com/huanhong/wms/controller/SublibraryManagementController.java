package com.huanhong.wms.controller;

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
import com.huanhong.wms.entity.SublibraryManagement;
import com.huanhong.wms.entity.vo.SublibraryVO;
import com.huanhong.wms.mapper.SublibraryManagementMapper;
import com.huanhong.wms.service.ISublibraryManagementService;
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
    @PostMapping
    public Result add(@Valid @RequestBody SublibraryManagement sublibraryMmanagement) {

        /**
         * 判断是否有必填参数为空
         */
        try {
            /**
             * 实体类转为json
             */
            String sublibraryManagementToJoStr = JSONObject.toJSONString(sublibraryMmanagement);
            JSONObject sublibraryManagementJo = JSONObject.parseObject(sublibraryManagementToJoStr);
            /**
             * 不能为空的参数list
             * 配置于judge.properties
             */
            List<String> list = judgeConfig.getSublibraryNotNullList();
            /**
             * 将NotNullList中的值当作key判断value是否为空
             */
            for (int i = 0; i < list.size(); i++) {
                String key = list.get(i);
                if (StringUtils.isBlank(sublibraryManagementJo.getString(key)) || "null".equals(sublibraryManagementJo.getString(key))) {
                    return Result.failure(ErrorCode.PARAM_FORMAT_ERROR, key + ": 不能为空");
                }
            }
        } catch (Exception e) {
            LOGGER.error("添子库失败--判断参数空值出错,异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--判空失败，请稍后再试或联系管理员");
        }
        /**
         * 在此处查重
         */
        try {
            SublibraryManagement sublibrary = sublibraryManagementService.getSublibraryBySublibraryId(sublibraryMmanagement.getSublibraryId());
            if (ObjectUtil.isNotEmpty(sublibrary)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "子库编号重复");
            }
            try {
                int insert = sublibraryManagementMapper.insert(sublibraryMmanagement);
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
    @PutMapping
    public Result update(@Valid @RequestBody SublibraryManagement sublibraryManagement) {
        UpdateWrapper updateWrapper = new UpdateWrapper<>();
        try {
            if (StringUtils.isBlank(sublibraryManagement.getWarehouseId())) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "库房编号为空");
            } else if (StringUtils.isBlank(sublibraryManagement.getSublibraryId())) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "子库编号为空");
            }
            SublibraryManagement sublibraryIsExist = sublibraryManagementService.getSublibraryBySublibraryId(sublibraryManagement.getSublibraryId());
            if (ObjectUtil.isEmpty(sublibraryIsExist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "无此子库编码");
            }
            updateWrapper.eq("warehouse_id", sublibraryManagement.getWarehouseId());
            updateWrapper.eq("sublibrary_id", sublibraryManagement.getSublibraryId());
            int update = sublibraryManagementMapper.update(sublibraryManagement, updateWrapper);
            return render(update > 0);
        } catch (Exception e) {
            LOGGER.error("更新子库信息出错--更新失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：子库更新失败，请稍后再试或联系管理员");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除子库管理", notes = "生成代码")
    @DeleteMapping("/{sublibraryId}")
    public Result delete(@PathVariable String sublibraryId) {
        QueryWrapper<SublibraryManagement> queryWrapper = new QueryWrapper<>();
        try {
            queryWrapper.eq("sublibrary_id", sublibraryId);
            SublibraryManagement sublibraryManagement = sublibraryManagementMapper.selectOne(queryWrapper);
            if (ObjectUtil.isEmpty(sublibraryManagement)) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "操作失败：子库编号不存在");
            }
            int i = sublibraryManagementMapper.delete(queryWrapper);
            LOGGER.info("子库:  " + sublibraryId + "删除成功");
            return render(i > 0);
        } catch (Exception e) {
            LOGGER.error("删除子库信息出错--删除失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：子库删除失败，请稍后再试或联系管理员");
        }
    }

}

