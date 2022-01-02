package com.huanhong.wms.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.config.JudgeConfig;
import com.huanhong.wms.entity.InventoryInformation;
import com.huanhong.wms.mapper.InventoryInformationMapper;
import com.huanhong.wms.service.IInventoryInformationService;
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
import java.util.Map;

@RestController
@RequestMapping("/inventory-information")
@ApiSort()
@Api(tags = "库存表")
public class InventoryInformationController extends BaseController {

    @Resource
    private IInventoryInformationService inventoryInformationService;

    @Resource
    private InventoryInformationMapper inventoryInformationMapper;

    @Autowired
    private JudgeConfig judgeConfig;

    public static final Logger LOGGER = LoggerFactory.getLogger(MaterialController.class);

    /**
     * 分页查询
     *
     * @param current
     * @param size
     * @param search
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
            @ApiImplicitParam(name = "search", value = "聚合搜索（标题）"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询库存表", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<InventoryInformation>> page(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size,
                                                   @RequestParam Map<String, Object> search) {
        QueryWrapper<InventoryInformation> query = new QueryWrapper<>();
        query.orderByDesc("id");
        if (search.containsKey("search")) {
            String text = search.get("search").toString();
            if (StrUtil.isNotEmpty(text)) {
                query.and(qw -> qw.like("title", text).or()
                        .like("user_name", text)
                );
            }
        }
        return Result.success(inventoryInformationMapper.selectPage(new Page<>(current, size), query));
    }

    /**
     * 库存新增--因入库发生变动
     *
     * @param inventoryInformation
     * @return
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "库存新增", notes = "生成代码")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody InventoryInformation inventoryInformation) {

        /**
         * 判断是否有必填参数为空
         */
        try {
            /**
             * 实体类转为json
             */
            String inventoryInformationToJoStr = JSONObject.toJSONString(inventoryInformation);
            JSONObject inventoryInformationJo = JSONObject.parseObject(inventoryInformationToJoStr);
            /**
             * 不能为空的参数list
             * 配置于judge.properties
             */
            List<String> list = judgeConfig.getInventoryNotNullList();

            /**
             * 将InventoryNotNullList中的值当作key判断value是否为空
             */
            for (int i = 0; i < list.size(); i++) {
                String key = list.get(i);
                if (StringUtils.isBlank(inventoryInformationJo.getString(key)) || "null".equals(inventoryInformationJo.getString(key))) {
                    return Result.failure(ErrorCode.PARAM_FORMAT_ERROR, key + ": 不能为空");
                }
            }
        } catch (Exception e) {
            LOGGER.error("新增库存失败--判断参数空值出错,异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--判空失败，请稍后再试或联系管理员");
        }
        /**
         * 新增库存时应给予最大的自由度,库存应当是基于现实的映射，若系统过多限制，则与实际脱节，降低实用度
         * 1.必填字段不能为空
         */
        int insert = inventoryInformationMapper.insert(inventoryInformation);
        return render(insert > 0);
    }

    /**
     * 库存更新
     *
     * @param inventoryInformation
     * @return
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "库存更新", notes = "生成代码")
    @PutMapping
    public Result update(@Valid @RequestBody InventoryInformation inventoryInformation) {

        /**
         * 这里的库存更新接口用于非正常状态的库存变动，并没有通过线上出库流程。
         * 1.自然或人为原因导致的物料损毁或变质
         * 2.或因税率浮动导致单价变动等因素
         * 3.通过物料编码和批次操作
         */
        int update = inventoryInformationService.updateInventoryInformation(inventoryInformation);
        return render(update > 0);
    }

    /**
     * 根据物料编码和批次下架全部物料
     * @param meterialCode
     * @param batch
     * @return
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "物料下架", notes = "生成代码")
    @DeleteMapping("/deleteByMeterialCodeAndBatch/{MeterialCode}&{Batch}")
    public Result delete(@PathVariable("meterialCode") String meterialCode, @PathVariable("batch") String batch) {
        try {
            QueryWrapper<InventoryInformation> wrapper = new QueryWrapper<>();
            wrapper.eq("material_coding", meterialCode);
            wrapper.eq("batch", batch);
            int i = inventoryInformationMapper.delete(wrapper);
            LOGGER.info("物料: " + meterialCode + " 删除成功");
            return render(i > 0);
        } catch (Exception e) {
            LOGGER.error("删除物料信息出错--删除失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：物料删除失败，请稍后再试或联系管理员");
        }
    }
}

