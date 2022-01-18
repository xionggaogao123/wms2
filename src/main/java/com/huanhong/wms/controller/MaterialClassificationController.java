package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
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
import com.huanhong.wms.entity.Material;
import com.huanhong.wms.entity.MaterialClassification;
import com.huanhong.wms.entity.dto.AddMaterialClassificationDTO;
import com.huanhong.wms.entity.dto.UpdateMaterialClassificationDTO;
import com.huanhong.wms.entity.vo.MaterialClassficationVO;
import com.huanhong.wms.mapper.MaterialClassificationMapper;
import com.huanhong.wms.mapper.OssMapper;
import com.huanhong.wms.service.IMaterialClassificationService;
import com.huanhong.wms.service.IMaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/materialClassification")
@ApiSort()
@Api(tags = "物料分类")
public class MaterialClassificationController extends BaseController {

    @Resource
    private IMaterialClassificationService materialClassificationServicel;

    @Resource
    private IMaterialService materialService;

    @Resource
    private MaterialClassificationMapper materialClassificationMapper;

    @Resource
    private OssMapper ossMapper;

    @Resource
    private JudgeConfig judgeConfig;

    public static final Logger LOGGER = LoggerFactory.getLogger(MaterialController.class);

    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页码"),
            @ApiImplicitParam(name = "size", value = "每页行数"),
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询物料分类")
    @GetMapping("/pagingFuzzyQuery")
    public Result<Page<MaterialClassification>> page(@RequestParam(defaultValue = "1") Integer current,
                                                     @RequestParam(defaultValue = "10") Integer size,
                                                     MaterialClassficationVO materialClassficationVO
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<MaterialClassification> pageResult = materialClassificationServicel.pageFuzzyQuery(new Page<>(current, size), materialClassficationVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到相关物料分类信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.failure("查询失败--异常：" + e);
        }
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加物料分类")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddMaterialClassificationDTO addMaterialClassificationDTO) {

        //分类级别 0-大类 1-中类  2-小类
        Integer levelType = addMaterialClassificationDTO.getLevelType();


        //分类编码 0-大类（2位数字） 1-中类 （大类+2位数字=4位数字） 2-小类（大类+中类+5位数字=9位数字）
        //用户根据下拉菜单选择父类分类编码手动输入要新增的编码，由后端进行拼接生成完整的编码
        String typeCode = addMaterialClassificationDTO.getTypeCode();
        if (0 != addMaterialClassificationDTO.getLevelType()) {
            typeCode = addMaterialClassificationDTO.getParentCode() + addMaterialClassificationDTO.getTypeCode();
        }

        if (!StrUtils.isNumeric(typeCode)) {
            return Result.failure(ErrorCode.PARAM_FORMAT_ERROR, ": 分类编码只能是数字");
        }

        //过滤特殊字符
        int length = StrUtils.HandleData(typeCode).length();

        /**
         * 判断是否有必填参数为空
         */
        try {

            //大类父类编码默认为零
            if (!"0".equals(String.valueOf(addMaterialClassificationDTO.getLevelType()))) {
                if (StringUtils.isBlank(addMaterialClassificationDTO.getParentCode())) {
                    return Result.failure(ErrorCode.PARAM_FORMAT_ERROR, "中类、小类父类编码不能为空");
                }
            }

            /**
             * 实体类转为json
             */
            String meterialToJoStr = JSONObject.toJSONString(addMaterialClassificationDTO);
            JSONObject meterialJo = JSONObject.parseObject(meterialToJoStr);
            /**
             * 不能为空的参数list
             * 配置于judge.properties
             */
            List<String> list = judgeConfig.getMeterialClassificationNullList();

            /**
             * 将MeterialClassificationNullList中的值当作key判断value是否为空
             */
            for (int i = 0; i < list.size(); i++) {
                String key = list.get(i);
                if (StringUtils.isBlank(meterialJo.getString(key)) || "null".equals(meterialJo.getString(key))) {
                    return Result.failure(ErrorCode.PARAM_FORMAT_ERROR, key + ": 不能为空");
                }
            }

        } catch (Exception e) {
            LOGGER.error("添加物料分类失败--判断参数空值出错,异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--判空失败，请稍后再试或联系管理员");
        }


        /**
         * 判断父分类是否存在
         */
        try {
            String resultMessage;
            MaterialClassification meterialClassificationParent_exist = materialClassificationServicel.getMaterialClassificationByTypeCode(addMaterialClassificationDTO.getParentCode());
            //根据用户输入分类等级判断是否正确填写编码位数
            switch (levelType) {
                case 0:
                    /**
                     *新增物料分类为大类时，父类编码默认为0
                     */
                    addMaterialClassificationDTO.setParentCode("0");
                    if (length != 2) {
                        resultMessage = "物料分类-大类的分类编码为2位";
                        return Result.failure(ErrorCode.SYSTEM_ERROR, resultMessage);
                    }
                    break;
                case 1:
                    if (ObjectUtil.isEmpty(meterialClassificationParent_exist)) {
                        resultMessage = "父类编码不存在，请查验后重试";
                        return Result.failure(ErrorCode.SYSTEM_ERROR, resultMessage);
                    } else {
                        if (meterialClassificationParent_exist.getLevelType() + 1 == addMaterialClassificationDTO.getLevelType()) {
                            if (length != 4) {
                                resultMessage = "物料分类-中类的分类编码位4位";
                                return Result.failure(ErrorCode.SYSTEM_ERROR, resultMessage);
                            }
                        } else {
                            resultMessage = "物料分类-无法在分类编码" + addMaterialClassificationDTO.getParentCode() + "下添加此分类";
                            return Result.failure(ErrorCode.SYSTEM_ERROR, resultMessage);
                        }
                    }
                    break;
                case 2:
                    if (ObjectUtil.isEmpty(meterialClassificationParent_exist)) {
                        resultMessage = "父类编码不存在，请查验后重试";
                        return Result.failure(ErrorCode.SYSTEM_ERROR, resultMessage);
                    } else {
                        if (meterialClassificationParent_exist.getLevelType() + 1 == addMaterialClassificationDTO.getLevelType()) {
                            if (length != 6) {
                                resultMessage = "物料分类-小类的分类编码位6位";
                                return Result.failure(ErrorCode.SYSTEM_ERROR, resultMessage);
                            }
                        } else {
                            resultMessage = "物料分类-无法在分类编码" + addMaterialClassificationDTO.getParentCode() + "下添加此分类";
                            return Result.failure(ErrorCode.SYSTEM_ERROR, resultMessage);
                        }
                    }
                    break;
                default:
                    throw new IllegalStateException("未知的分类等级");
            }
        } catch (Exception e) {
            LOGGER.error("系统错误");
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统错误：" + e);
        }

        /**
         * 在此处查重
         */
        try {
            MaterialClassification meterialClassification_exist = materialClassificationServicel.getMaterialClassificationByTypeCode(typeCode);
            if (ObjectUtil.isNotEmpty(meterialClassification_exist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "物料分类已存在");
            }
            addMaterialClassificationDTO.setTypeCode(typeCode);
            MaterialClassification materialClassification = new MaterialClassification();
            BeanUtil.copyProperties(addMaterialClassificationDTO, materialClassification);
            int insert = materialClassificationMapper.insert(materialClassification);
            LOGGER.info("添加物料分类成功");
            return render(insert > 0);

        } catch (Exception e) {
            LOGGER.error("添加物料错误--（插入数据）失败,异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--插入数据失败，请稍后再试或联系管理员");
        }
    }


    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新物料分类")
    @PutMapping("/updateByTyCode")
    public Result update(@Valid @RequestBody UpdateMaterialClassificationDTO updateMaterialClassificationDTO) {

        //更新物料分类只能更新物料分类名称
        try {
            if (StringUtils.isBlank(updateMaterialClassificationDTO.getTypeCode())) {
                return Result.failure(ErrorCode.PARAM_FORMAT_ERROR, "类别编码为空");
            }
            //判断此物料分类是否存在
            MaterialClassification meterialClassification_exist = materialClassificationServicel.getMaterialClassificationByTypeCode(updateMaterialClassificationDTO.getTypeCode());
            if (meterialClassification_exist == null) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "无此物料分类");
            }
            UpdateWrapper<MaterialClassification> updateWrapper = new UpdateWrapper<>();
            MaterialClassification updateMaterialClassification = new MaterialClassification();
            BeanUtil.copyProperties(updateMaterialClassificationDTO, updateMaterialClassification);
            updateWrapper.eq("type_code", updateMaterialClassificationDTO.getTypeCode());
            int update = materialClassificationMapper.update(updateMaterialClassification, updateWrapper);
            LOGGER.info("物料分类" + updateMaterialClassificationDTO.getTypeCode() + " 更新成功");
            return render(update > 0);
        } catch (Exception e) {
            LOGGER.error("更新物料信息出错--更新失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：物料分类更新失败，请稍后再试或联系管理员");
        }
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "删除物料分类")
    @DeleteMapping("/deleteByTypeCode/{typeCode}")
    public Result delete(@PathVariable String typeCode) {
        try {
            //检查此物料分类是否存在
            MaterialClassification materialClassification_exist = materialClassificationServicel.getMaterialClassificationByTypeCode(typeCode);
            if (ObjectUtil.isEmpty(materialClassification_exist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "无此物料分类");
            }
            //检查此物料分类是否已被物料基础信息使用
            List<Material> listMaterial = materialService.listFuzzyQuery(typeCode);
            if (ObjectUtil.isNotEmpty(listMaterial)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "此分类已被使用,无法删除");
            }
            int i = materialClassificationServicel.deleteMaterialClassification(materialClassification_exist);
            LOGGER.info("物料分类: " + typeCode + " 删除成功");
            return render(i > 0);
        } catch (Exception e) {
            LOGGER.error("删除物料分类出错--删除失败，异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常：物料分类删除失败，请稍后再试或联系管理员");
        }
    }


    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "获取树状图")
    @GetMapping("/treeList")
    public Result treeList() {
        List<Map<String, Object>> treeList = materialClassificationServicel.getTreeNode();
        return Result.success(treeList);
    }


    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "获取分类详细信息")
    @GetMapping("/getMaterialClassificationByTypeCode/{typeCode}")
    public Result getMaterialClassificationByTypeCode(@PathVariable String typeCode) {
        try {
            MaterialClassification materialClassification = materialClassificationServicel.getMaterialClassificationByTypeCode(typeCode);
            if (ObjectUtil.isNotEmpty(materialClassification)) {
                return Result.success(materialClassification);
            }
            return Result.noDataError();
        } catch (Exception e) {
            LOGGER.error("获取物料分类详细信息出错,异常：" + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "查询失败,系统异常：" + e);
        }
    }
}

