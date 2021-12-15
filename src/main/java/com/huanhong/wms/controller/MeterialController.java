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
import com.huanhong.wms.entity.Meterial;
import com.huanhong.wms.entity.dto.AddMeterialDTO;
import com.huanhong.wms.entity.dto.UpdateMeterialDTO;
import com.huanhong.wms.mapper.MeterialMapper;
import com.huanhong.wms.service.IMeterialService;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/meterial")
@ApiSort()
@Api(tags = "物料基础信息")
public class MeterialController extends BaseController {

    @Resource
    private IMeterialService meterialService;

    @Resource
    private MeterialMapper meterialMapper;

    @Autowired
    private JudgeConfig judgeConfig;

    public static final Logger LOGGER = LoggerFactory.getLogger(MeterialController.class);


    /**
     * 模糊查询
     *
     * @param current
     * @param size
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询组合物料")
    @GetMapping("/pagingFuzzyQuery")
    public Result<Page<Meterial>> page(@RequestParam(defaultValue = "1") Integer current,
                                       @RequestParam(defaultValue = "10") Integer size,
                                       AddMeterialDTO addMeterialDTO//查询条件封装的对象
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<Meterial> pageResult = meterialService.pageFuzzyQuery(new Page<>(current, size), addMeterialDTO);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.failure("查询失败--异常：" + e);
        }
    }

    /**
     * 添加物料
     *
     * @param meterial
     * @return
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加物料")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody Meterial meterial) {

        /**
         * 判断是否有必填参数为空
         */
        try {
            /**
             * 实体类转为json
             */
            String meterialToJoStr = JSONObject.toJSONString(meterial);
            JSONObject meterialJo = JSONObject.parseObject(meterialToJoStr);
            /**
             * 不能为空的参数list
             * 配置于judge.properties
             */
            List<String> list = judgeConfig.getMeterialNotNullList();

            /**
             * 将MeterialNotNullList中的值当作key判断value是否为空
             */
            for (int i = 0; i < list.size(); i++) {
                String key = list.get(i);
                if (StringUtils.isBlank(meterialJo.getString(key)) || "null".equals(meterialJo.getString(key))) {
                    return Result.failure(ErrorCode.PARAM_FORMAT_ERROR, key + ": 不能为空");
                }
            }
        } catch (Exception e) {
            LOGGER.error("添加物料失败--判断参数空值出错,异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--判空失败，请稍后再试或联系管理员");
        }

        /**
         * 在此处查重
         */
        try {
            Meterial meterial_exist = meterialService.getMeterialByMeterialCode(meterial.getMaterialCoding());
            if (ObjectUtil.isNotEmpty(meterial_exist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "物料编码重复");
            }
            try {
                int insert = meterialMapper.insert(meterial);
                LOGGER.info("添加物料成功");
                return render(insert > 0);
            } catch (Exception e) {
                LOGGER.error("添加物料错误--（插入数据）失败,异常：" + e);
                return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--插入数据失败，请稍后再试或联系管理员");
            }
        } catch (Exception e) {
            LOGGER.error("添加物料失败--处理（判断物料编码重复）失败,异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--判重失败，请稍后再试或联系管理员");
        }
    }


    /**
     * 过物料编码更新物料信息
     * @param updateMeterialDTO
     * @return
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新物料")
    @PutMapping("/updateByMaterialCoding")
    public Result updateByMaterialCoding(@Valid @RequestBody UpdateMeterialDTO updateMeterialDTO) {

        /**
         * 物料编码不能为空，为更新依据
         */
        try {
            if (StringUtils.isBlank(updateMeterialDTO.getMaterialCoding())) {
                return Result.failure(ErrorCode.PARAM_FORMAT_ERROR, "物料编码为空");
            }
            //判断此物料是否存在
            Meterial meterial_exist = meterialService.getMeterialByMeterialCode(updateMeterialDTO.getMaterialCoding());
            if (meterial_exist == null) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "无此物料编码");
            }

            UpdateWrapper<Meterial> updateWrapper = new UpdateWrapper<>();
            Meterial updateMeterial = new Meterial();
            BeanUtil.copyProperties(updateMeterialDTO, updateMeterial );
            updateWrapper.eq("material_coding", updateMeterialDTO.getMaterialCoding());
            int i = meterialMapper.update(updateMeterial, updateWrapper);
            LOGGER.info("物料: " + updateMeterialDTO.getMaterialCoding() + " 更新成功");
            return render(i > 0);
        } catch (Exception e) {
            LOGGER.error("更新物料信息出错--更新失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：物料更新失败，请稍后再试或联系管理员");
        }
    }

    /**
     * 根据物料编号删除物料--逻辑删除
     *
     * @param meterialCode
     * @return
     */
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除物料")
    @DeleteMapping("/deleteByMeterialCode/{meterialCode}")
    public Result delete(@PathVariable String meterialCode) {

        try {
            Meterial meterial_exist = meterialService.getMeterialByMeterialCode(meterialCode);
            if (ObjectUtil.isEmpty(meterial_exist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "无此物料编码");
            }
            QueryWrapper<Meterial> wrapper = new QueryWrapper<>();
            wrapper.eq("material_coding", meterialCode);
            int i = meterialMapper.delete(wrapper);
            LOGGER.info("物料: " + meterialCode + " 删除成功");
            return render(i > 0);
        } catch (Exception e) {
            LOGGER.error("删除物料信息出错--删除失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：物料删除失败，请稍后再试或联系管理员");
        }
    }


    /**
     * @param meterialCode
     * @return
     */
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "根据物料编码查询物料信息")
    @GetMapping("/getMeterialByMeterialCode/{meterialCode}")
    public Result getMeterialByMeterialCode(@PathVariable("meterialCode") String meterialCode) {
        Meterial meterial = meterialService.getMeterialByMeterialCode(meterialCode);
        if (meterial != null) {
            return Result.success(meterial, "查询成功");
        }
        return Result.noDataError();
    }

    /**
     * 根据物料名称查询物料信息
     *
     * @param meterialName
     * @return
     */
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "根据物料名称查询物料信息")
    @GetMapping("/getMeterialByMeterialName/{meterialName}")
    public Result getMeterialByMeterialName(@PathVariable("meterialName") String meterialName) {
        QueryWrapper<Meterial> wrapper = new QueryWrapper<>();
        wrapper.eq("material_name", meterialName);
        ArrayList<Meterial> meterialList = (ArrayList<Meterial>) meterialMapper.selectList(wrapper);
        if (meterialList != null) {
            return Result.success(meterialList, "查询成功");
        }
        return Result.noDataError();
    }

    /**
     * 根据物料俗称查询物料信息
     *
     * @param slang
     * @return
     */
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "根据物料俗称查询物料信息")
    @GetMapping("/getMetrialByMeterialName/{slang}")
    public Result getMeterialBySlang(@PathVariable("slang") String slang) {
        QueryWrapper<Meterial> wrapper = new QueryWrapper<>();
        wrapper.eq("slang", slang);
        ArrayList<Meterial> meterialList = (ArrayList<Meterial>) meterialMapper.selectList(wrapper);
        if (meterialList != null) {
            return Result.success(meterialList, "查询成功");
        }
        return Result.noDataError();
    }


    /**
     * 物料基础信息模糊查询-暂时废弃
     *
     * @param key
     * @param value
     * @return
     */
//    @ApiOperationSupport(order = 8)
//    @ApiOperation(value = "模糊查询")
//    @GetMapping("/getMetrialByFuzzyQuery/{key}&{value}")
//    public Result getMeterialByFuzzy(@PathVariable("key") String key,
//                                     @PathVariable("value") String value) {
//        try {
//            String field = fuzzyQuery(key);
//            List<String> meterialList = meterialMapper.fuzzyQuerySelectList(field, value);
//            if (meterialList != null) {
//                return Result.success(meterialList, "查询成功");
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
//            //物料编码
//            case "materialCoding":
//                field = "material_coding";
//                break;
//            //物料名称
//            case "materialName":
//                field = "material_name";
//                break;
//            //物料俗称
//            case "slang":
//                field = "slang";
//                break;
//            //规格型号
//            case "specificationModel":
//                field = "specification_model";
//                break;
//            //计量单位
//            case "measurementUnit":
//                field = "measurement_unit";
//                break;
//            //材质
//            case "material":
//                field = "material";
//                break;
//            //执行标准
//            case "executiveStandard":
//                field = "executive_standard";
//                break;
//            //技术要求
//            case "skillsRequiremen":
//                field = "skills_requiremen";
//                break;
//            //图号
//            case "drawingNumber":
//                field = "drawing_number";
//                break;
//            //安全质量标准
//            case "safetyQualityStandards":
//                field = "safety_quality_standards";
//                break;
//            //生产厂家
//            case "supplier":
//                field = "supplier";
//                break;
//            case UNKNOWN:
//                throw new IllegalArgumentException("未知字段");
//            default:
//                throw new IllegalStateException("Unexpected value: " + key);
//        }
//        return field;
//    }
}