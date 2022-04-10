package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.AddMaterialDTO;
import com.huanhong.wms.entity.dto.UpdateMaterialDTO;
import com.huanhong.wms.entity.vo.MaterialDetailsVO;
import com.huanhong.wms.entity.vo.MaterialVO;
import com.huanhong.wms.entity.vo.OssVo;
import com.huanhong.wms.mapper.InventoryInformationMapper;
import com.huanhong.wms.mapper.MaterialClassificationMapper;
import com.huanhong.wms.mapper.MaterialMapper;
import com.huanhong.wms.mapper.OssMapper;
import com.huanhong.wms.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.codec.IonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/v1/meterial")
@ApiSort()
@Api(tags = "物料基础信息")
public class MaterialController extends BaseController {

    @Resource
    private IMaterialService materialService;

    @Resource
    private MaterialMapper materialMapper;

    @Resource
    private MaterialClassificationMapper materialClassificationMapper;

    @Resource
    private IMaterialClassificationService materialClassificationService;

    @Resource
    private IInventoryInformationService inventoryInformationService;


    @Resource
    private IInventoryDocumentService inventoryDocumentService;

    @Resource
    private InventoryInformationMapper inventoryInformationMapper;

    @Resource
    private IArrivalVerificationService arrivalVerificationService;

    @Resource
    private IOnShelfService onShelfService;

    @Resource
    private IInventoryDocumentDetailsService inventoryDocumentDetailsService;

    @Resource
    private IArrivalVerificationDetailsService arrivalVerificationDetailsService;


    @Resource
    private OssMapper ossMapper;

//    @Autowired
//    private JudgeConfig judgeConfig;



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
    public Result<Page<Material>> page(@RequestParam(defaultValue = "1") Integer current,
                                       @RequestParam(defaultValue = "10") Integer size,
                                       MaterialVO materialVO//查询条件封装的对象
    ) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<Material> pageResult = materialService.pageFuzzyQuery(new Page<>(current, size), materialVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到相关物料信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常"+e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
    }

    /**
     * 添加物料
     *
     * @param addMaterialDTO
     * @return
     */
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "添加物料")
    @PostMapping("/add")
    public Result add(@Valid @RequestBody AddMaterialDTO addMaterialDTO) {

        /**
         * 判断是否有必填参数为空
         */
        try {
//            /**
//             * 实体类转为json
//             */
//            String meterialToJoStr = JSONObject.toJSONString(addMaterialDTO);
//            JSONObject meterialJo = JSONObject.parseObject(meterialToJoStr);
//            /**
//             * 不能为空的参数list
//             * 配置于judge.properties
//             */
//            List<String> list = judgeConfig.getMeterialNotNullList();
//
//            /**
//             * 将MeterialNotNullList中的值当作key判断value是否为空
//             */
//            for (int i = 0; i < list.size(); i++) {
//                String key = list.get(i);
//                if (StringUtils.isBlank(meterialJo.getString(key)) || "null".equals(meterialJo.getString(key))) {
//                    return Result.failure(ErrorCode.PARAM_FORMAT_ERROR, key + ": 不能为空");
//                }
//            }

            /**
             * 查询物料分类是否存在
             */
            QueryWrapper<MaterialClassification> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("type_code", addMaterialDTO.getTypeCode());
            queryWrapper.eq("level_type", "2");
            MaterialClassification materialClassification = materialClassificationMapper.selectOne(queryWrapper);
            if (ObjectUtil.isEmpty(materialClassification)) {
                return Result.failure(ErrorCode.DATA_IS_NULL, "分类编码不存在或不符合规范（请使用小类编码）");
            }

            /**
             * 生成物料编码（分类编码+五位流水号）
             * 1.获取物料分类编码，根据分类编码检索出所有此分类的物料编码
             * 2.截取物料编码的后五位流水号，将流水号+1得到新的物料编码
             */
            QueryWrapper<Material> queryWrapperMaterial = new QueryWrapper<>();
            queryWrapperMaterial.eq("type_code", addMaterialDTO.getTypeCode());
            Material maxMaterial = materialMapper.selectOne(queryWrapperMaterial.orderByDesc("id").last("limit 1"));
            //目前最大的本分类物料编码
            String maxMaterialCode = null;
            if (ObjectUtil.isNotEmpty(maxMaterial)) {
                maxMaterialCode = maxMaterial.getMaterialCoding();
            }
            String orderNo = null;
            //物料编码前缀-类型编码
            String code_pfix = addMaterialDTO.getTypeCode();
            if (maxMaterialCode != null && maxMaterial.getMaterialCoding().contains(code_pfix)) {
                String code_end = maxMaterialCode.substring(6, 11);
                int endNum = Integer.parseInt(code_end);
                int tmpNum = 100000 + endNum + 1;
                orderNo = code_pfix + StrUtils.subStr("" + tmpNum, 1);
            } else {
                orderNo = code_pfix + "00001";
            }


            /**
             * 新增物料
             */
            try {
                Material material = new Material();
                BeanUtil.copyProperties(addMaterialDTO, material);
                material.setMaterialCoding(orderNo);
                int insert = materialMapper.insert(material);
                if (insert > 0) {
                    log.info("添加物料成功");
                    //用更新方式绑定附件
                    //判断OSS是否为空
                    if (StringUtils.isNotBlank(addMaterialDTO.getOssIds())){
                        ossMapper.updateObjectIdByIds(material.getId(),addMaterialDTO.getOssIds());
                    }
                    return Result.success(materialService.getMeterialByMeterialCode(material.getMaterialCoding()), "新增成功");
                } else {
                    return Result.failure(ErrorCode.SYSTEM_ERROR, "新增失败！");
                }
            } catch (Exception e) {
                log.error("添加物料错误--（插入数据）失败,异常：" + e);
                return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--插入数据失败，请稍后再试或联系管理员");
            }

        } catch (Exception e) {
            log.error("生成新物料编码出错: " + e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常--物料编码生成失败，请稍后再试或联系管理员");
        }
    }


    /**
     * 过物料编码更新物料信息
     * @param updateMaterialDTO
     * @return
     */
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "更新物料")
    @PutMapping("/updateByMaterialCoding")
    public Result updateByMaterialCoding(@Valid @RequestBody UpdateMaterialDTO updateMaterialDTO) {

        /**
         * 物料编码不能为空，为更新依据
         */
        try {
            if (StringUtils.isBlank(updateMaterialDTO.getMaterialCoding())) {
                return Result.failure(ErrorCode.PARAM_FORMAT_ERROR, "物料编码为空");
            }
            //判断此物料是否存在
            Material material_exist = materialService.getMeterialByMeterialCode(updateMaterialDTO.getMaterialCoding());
            if (ObjectUtil.isEmpty(material_exist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "无此物料编码");
            }

            /**
             * 判断物料是否停用 若停用是否已经将stopUsing字段改为 0 -使用中
             * 若改为0则允许更新  反之则拒绝
             */
            if (materialService.isStopUsing(updateMaterialDTO.getMaterialCoding())==1){
                if (updateMaterialDTO.getStopUsing()!=0){
                    return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "物料编码已停用禁止更新");
                }
            }

            UpdateWrapper<Material> updateWrapper = new UpdateWrapper<>();
            Material updateMaterial = new Material();
            BeanUtil.copyProperties(updateMaterialDTO, updateMaterial);
            updateWrapper.eq("material_coding", updateMaterialDTO.getMaterialCoding());
            int i = materialMapper.update(updateMaterial, updateWrapper);
            log.info("物料: " + updateMaterialDTO.getMaterialCoding() + " 更新成功");
            return render(i > 0);
        } catch (Exception e) {
            log.error("更新物料信息出错--更新失败，异常：" + e);
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
            Material material_exist = materialService.getMeterialByMeterialCode(meterialCode);
            if (ObjectUtil.isEmpty(material_exist)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "无此物料编码");
            }

            /**
             * 如果物料被使用则不能删除-库存
             */
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("material_coding",meterialCode);
            Integer count = inventoryInformationMapper.selectCount(queryWrapper);
            if (count>0){
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR,"物料使用中，无法删除");
            }

            QueryWrapper<Material> wrapper = new QueryWrapper<>();
            wrapper.eq("material_coding", meterialCode);
            int i = materialMapper.delete(wrapper);
            if (i > 0) {
                log.info("物料: " + meterialCode + " 删除成功");
            }
            return render(i > 0);
        } catch (Exception e) {
            log.error("删除物料信息出错--删除失败，异常：" + e);
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
        Material material = materialService.getMeterialByMeterialCode(meterialCode);
        if (ObjectUtil.isNotEmpty(material)) {
            List<OssVo> list = ossMapper.getOssByObjectIdNoType(material.getId());
            MaterialDetailsVO materialDetailsVO = new MaterialDetailsVO();
            BeanUtil.copyProperties(material,materialDetailsVO);
            materialDetailsVO.setListFile(list);
            return Result.success(materialDetailsVO, "查询成功");
        }
        return Result.noDataError();
    }



    @ApiOperationSupport(order = 6)
    @ApiOperation(value="PDA根据物料编码获取未完成清点单、到货检验单、上架单")
    @GetMapping("/getDocIdAndDocNumByMaterialCodeAndWareHouseId")
    public Result getDocIdAndDocNumByMaterialCode(@RequestParam String materialCoding,@RequestParam String warehouseId){

        JSONObject jsonObject = new JSONObject();

        /**
         * 清点单
         */
        List<String> inventoryDocumentDocNumList= new ArrayList<>();
        List<InventoryDocumentDetails> inventoryDocumentDetailsList = inventoryDocumentDetailsService.getInventoryDocumentDetailsListByMaterialCodeAndWarehouseId(materialCoding, warehouseId);
        for (InventoryDocumentDetails inventoryDocumentDetails:inventoryDocumentDetailsList
             ) {
            String docNum = inventoryDocumentDetails.getDocumentNumber();
            inventoryDocumentDocNumList.add(docNum);
        }
        //去重
        inventoryDocumentDocNumList=inventoryDocumentDocNumList.stream()
                .distinct()
                .collect(Collectors.toList());
        JSONObject jsonObjectInventory = new JSONObject();
        JSONArray  jsonArrayInventory = new JSONArray();
        for (String docNum:inventoryDocumentDocNumList
             ) {
            InventoryDocument inventoryDocument = inventoryDocumentService.getInventoryDocumentByDocumentNumberAndWarehouseId(docNum, warehouseId);
            if (ObjectUtil.isNotEmpty(inventoryDocument)){
                jsonObjectInventory.put("id", inventoryDocument.getId());
                jsonObjectInventory.put("docNum",docNum);
                jsonArrayInventory.add(jsonObjectInventory);
            }
        }
        jsonObject.put("inventoryDocument",jsonArrayInventory);

        /**
         * 到货检验
         */
        List<String> arrivalVerificationDocNumList = new ArrayList<>();
        List<ArrivalVerificationDetails> arrivalVerificationDetailsList = arrivalVerificationDetailsService.getArrivalVerificationDetailsListByMaterialCodeAndWarehouseId(materialCoding, warehouseId);
        for (ArrivalVerificationDetails  arrivalVerificationDetails : arrivalVerificationDetailsList
             ) {
            String docNum = arrivalVerificationDetails.getDocumentNumber();
            arrivalVerificationDocNumList.add(docNum);
        }
        //去重
        arrivalVerificationDocNumList = arrivalVerificationDocNumList.stream()
                .distinct()
                .collect(Collectors.toList());
        JSONObject jsonObjectArrivalVerification = new JSONObject();
        JSONArray  jsonArrayArrivalVerification= new JSONArray();
        for (String docNum:arrivalVerificationDocNumList
        ) {
            ArrivalVerification arrivalVerification = arrivalVerificationService.getArrivalVerificationByDocNumberAndWarhouseId(docNum, warehouseId);
            if (ObjectUtil.isNotNull(arrivalVerification)){
                jsonObjectArrivalVerification.put("id", arrivalVerification.getId());
                jsonObjectArrivalVerification.put("docNum",docNum);
                jsonArrayArrivalVerification.add(jsonObjectArrivalVerification);
            }
        }
        jsonObject.put("arrivalVerification",jsonArrayArrivalVerification);


        /**
         * 上架单
         */
        List<OnShelf> onShelfList = onShelfService.getOnshelfByMaterialCodingAndWarehouseId(materialCoding, warehouseId);
        JSONObject jsonObjectOnShelf= new JSONObject();
        JSONArray  jsonArrayOnShelf= new JSONArray();
        for (OnShelf onshelf: onShelfList
             ) {
            jsonObjectOnShelf.put("id", onshelf.getId());
            jsonObjectOnShelf.put("docNum", onshelf.getDocumentNumber());
            jsonArrayOnShelf.add(jsonObjectOnShelf);
        }
        jsonObject.put("onShelf",jsonArrayOnShelf);


        return ObjectUtil.isNotEmpty(jsonObject)?Result.success(jsonObject):Result.failure("未找对应信息！");
    }


//    /**
//     * 根据物料名称查询物料信息
//     *
//     * @param meterialName
//     * @return
//     */
//    @ApiOperationSupport(order = 6)
//    @ApiOperation(value = "根据物料名称查询物料信息")
//    @GetMapping("/getMeterialByMeterialName/{meterialName}")
//    public Result getMeterialByMeterialName(@PathVariable("meterialName") String meterialName) {
//        QueryWrapper<Meterial> wrapper = new QueryWrapper<>();
//        wrapper.eq("material_name", meterialName);
//        ArrayList<Meterial> meterialList = (ArrayList<Meterial>) meterialMapper.selectList(wrapper);
//        if (meterialList != null) {
//            return Result.success(meterialList, "查询成功");
//        }
//        return Result.noDataError();
//    }

//    /**
//     * 根据物料俗称查询物料信息
//     *
//     * @param slang
//     * @return
//     */
//    @ApiOperationSupport(order = 7)
//    @ApiOperation(value = "根据物料俗称查询物料信息")
//    @GetMapping("/getMetrialByMeterialName/{slang}")
//    public Result getMeterialBySlang(@PathVariable("slang") String slang) {
//        QueryWrapper<Meterial> wrapper = new QueryWrapper<>();
//        wrapper.eq("slang", slang);
//        ArrayList<Meterial> meterialList = (ArrayList<Meterial>) meterialMapper.selectList(wrapper);
//        if (meterialList != null) {
//            return Result.success(meterialList, "查询成功");
//        }
//        return Result.noDataError();
//    }


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