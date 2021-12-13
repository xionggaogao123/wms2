package com.huanhong.wms.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.config.JudgeConfig;
import com.huanhong.wms.entity.WarehouseManagement;
import com.huanhong.wms.entity.vo.WarehouseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.mapper.WarehouseManagementMapper;
import com.huanhong.wms.service.IWarehouseManagementService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/warehouse-management")
@ApiSort()
@Api(tags = "仓库管理")
public class WarehouseManagementController extends BaseController {

    @Resource
    private IWarehouseManagementService warehouseManagementService;

    @Resource
    private WarehouseManagementMapper warehouseManagementMapper;


    @Autowired
    private JudgeConfig judgeConfig;

    public static final Logger LOGGER = LoggerFactory.getLogger(WarehouseManagementController.class);


    /**
     * @param current
     * @param size
     * @param warehouseVo
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询组合库房")
    @GetMapping("/pagingFuzzyQuery")
    public Result<Page<WarehouseManagement>> page(@RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  WarehouseVo warehouseVo //查询条件封装的对象
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<WarehouseManagement> pageResult = warehouseManagementService.pageFuzzyQuery(new Page<>(current, size), warehouseVo);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.failure("查询失败--异常：" + e);
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加仓库")
    @PostMapping
    public Result add(@Valid @RequestBody WarehouseManagement warehouseManagement) {

        /**
         * 判断是否有必填参数为空
         */
        try {
            /**
             * 实体类转为json
             */
            String warehouseManagementToJoStr = JSONObject.toJSONString(warehouseManagement);
            JSONObject warehouseManagementJo = JSONObject.parseObject(warehouseManagementToJoStr);
            /**
             * 不能为空的参数list
             * 配置于judge.properties
             */
            List<String> list = judgeConfig.getWarehouseNotNullList();

            /**
             * 将NotNullList中的值当作key判断value是否为空
             */
            for (int i = 0; i < list.size(); i++) {
                String key = list.get(i);
                if (StringUtils.isBlank(warehouseManagementJo.getString(key)) || "null".equals(warehouseManagementJo.getString(key))) {
                    return Result.failure(ErrorCode.PARAM_FORMAT_ERROR, key + ": 不能为空");
                }
            }
        } catch (Exception e) {
            LOGGER.error("添加库房失败--判断参数空值出错,异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--判空失败，请稍后再试或联系管理员");
        }

        /**
         * 在此处查重
         */
        try {
            WarehouseManagement warehouse = warehouseManagementService.getWarehouseByWarehouseId(warehouseManagement.getWarehouseId());
            if (ObjectUtil.isNotEmpty(warehouse)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "库房编号重复");
            }
            try {
                int insert = warehouseManagementMapper.insert(warehouseManagement);
                LOGGER.info("添加库房成功");
                return render(insert > 0);
            } catch (Exception e) {
                LOGGER.error("添加库房错误--（插入数据）失败,异常：" + e);
                return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--插入数据失败，请稍后再试或联系管理员");
            }
        } catch (Exception e) {
            LOGGER.error("添加库房失败--处理（判断库房编码重复）失败,异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--判重失败，请稍后再试或联系管理员");
        }
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新仓库管理")
    @PutMapping
    public Result update(@Valid @RequestBody WarehouseManagement warehouseManagement) {
        UpdateWrapper updateWrapper = new UpdateWrapper<>();
        try {
            if (StringUtils.isBlank(warehouseManagement.getCompanyId())) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "公司编号为空");
            } else if (StringUtils.isBlank(warehouseManagement.getWarehouseId())) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "库房编号为空");
            }
            WarehouseManagement warehouseManagementIsExist = warehouseManagementService.getWarehouseByWarehouseId(warehouseManagement.getWarehouseId());
            if (ObjectUtil.isEmpty(warehouseManagementIsExist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "无此库房编码");
            }
            updateWrapper.eq("company_id", warehouseManagement.getCompanyId());
            updateWrapper.eq("warehouse_id", warehouseManagement.getWarehouseId());
            int update = warehouseManagementMapper.update(warehouseManagement, updateWrapper);
            return render(update > 0);
        } catch (Exception e) {
            LOGGER.error("更新库房信息出错--更新失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：库房更新失败，请稍后再试或联系管理员");
        }
    }


    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除仓库管理")
    @DeleteMapping("/{warehouseId}")
    public Result delete(@PathVariable String warehouseId) {
        QueryWrapper<WarehouseManagement> queryWrapper = new QueryWrapper<>();
        try {
            queryWrapper.eq("warehouse_id", warehouseId);
            WarehouseManagement warehouseManagement = warehouseManagementMapper.selectOne(queryWrapper);
            if (ObjectUtil.isEmpty(warehouseManagement)) {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "操作失败：库房编号不存在");
            }
            int i = warehouseManagementMapper.delete(queryWrapper);
            LOGGER.info("库房:  " + warehouseId + "删除成功");
            return render(i > 0);
        } catch (Exception e) {
            LOGGER.error("删除库房信息出错--删除失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：库房删除失败，请稍后再试或联系管理员");
        }
    }


    /** 模糊查询-暂时废弃
     * @param key
     * @param value
     * @return
     */
//    @ApiOperationSupport(order = 5)
//    @ApiOperation(value = "模糊查询")
//    @GetMapping("/getWareHouseByFuzzyQuery/{key}&{value}")
//    public Result getMeterialByFuzzy(@PathVariable("key") String key,
//                                     @PathVariable("value") String value) {
//        try {
//            String field = fuzzyQuery(key);
//            // List<String> warehouselList = warehouseManagementMapper.fuzzyQuerySelectList(field, value);
//            //QueryWrapper<WarehouseManagement> queryWrapper = new QueryWrapper<>();
//            //queryWrapper.select(field).like(field, value);
//            List<WarehouseManagement> warehouselList = warehouseManagementMapper.selectList(queryWrapper);
//            if (warehouselList != null) {
//                return Result.success(warehouselList, "查询成功");
//            }
//            return Result.noDataError();
//        } catch (Exception e) {
//            LOGGER.error("模糊查询出错--异常：" + e);
//            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：模糊查询错误，请稍后再试或联系管理员");
//        }
//    }

//    public String fuzzyQuery(String key) {
//        String field;
//        switch (key) {
//            //所属公司ID
//            case "companyId":
//                field = "company_id";
//                break;
//            //库房编号
//            case "warehouseId":
//                field = "warehouse_id";
//                break;
//            //库房名称
//            case "warehouseName":
//                field = "warehouse_name";
//                break;
//            //库房面积
//            case "warehouseAcreage":
//                field = "warehouse_acreage";
//                break;
//            //库房层数
//            case "warehouseLayers":
//                field = "warehouse_layers";
//                break;
//            //库房地址
//            case "warehouseAdress":
//                field = "warehouse_adress";
//                break;
//            //库房负责人
//            case "warehousePrincipal":
//                field = "warehouse_principal";
//                break;
//            //库房联系电话
//            case "warehouseContactNumber":
//                field = "warehouse_contact_number";
//                break;
//            case UNKNOWN:
//                throw new IllegalArgumentException("未知字段");
//            default:
//                throw new IllegalStateException("Unexpected value: " + key);
//        }
//        return field;
//    }

}

