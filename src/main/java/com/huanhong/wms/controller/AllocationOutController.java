package com.huanhong.wms.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.AllocationOutVO;
import com.huanhong.wms.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.mapper.AllocationOutMapper;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApiSort()
@Api(tags = "调拨出库主表")
@Slf4j
@RestController
@RequestMapping("/v1//allocation-out")
public class AllocationOutController extends BaseController {

    @Resource
    private IAllocationOutService allocationOutService;

    @Resource
    private IAllocationOutDetailsService allocationOutDetailsService;

    @Resource
    private IAllocationPlanService allocationPlanService;

    @Resource
    private IAllocationPlanDetailService allocationPlanDetailService;

    @Resource
    private IOutboundRecordService outboundRecordService;

    @Resource
    private IMaterialService materialService;

    @Resource
    private IInventoryInformationService inventoryInformationService;

    @Resource
    private IWarehouseManagementService warehouseManagementService;

    @Resource
    private IUserService userService;

    @ApiImplicitParams({
        @ApiImplicitParam(name = "current", value = "当前页码"),
        @ApiImplicitParam(name = "size", value = "每页行数")
    })
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "分页查询", notes = "生成代码")
    @GetMapping("/page")
    public Result<Page<AllocationOut>> page(@RequestParam(defaultValue = "1") Integer current,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            AllocationOutVO allocationOutVO) {
        try {
            //调用服务层方法，传入page对象和查询条件对象
            Page<AllocationOut> pageResult = allocationOutService.pageFuzzyQuery(new Page<>(current, size), allocationOutVO);
            if (ObjectUtil.isEmpty(pageResult.getRecords())) {
                return Result.success(pageResult, "未查询到调拨出库单信息");
            }
            return Result.success(pageResult);
        } catch (Exception e) {
            log.error("分页查询异常", e);
            return Result.failure("查询失败--系统异常，请联系管理员");
        }
        }

        @ApiOperationSupport(order = 2)
        @ApiOperation(value = "添加")
        @PostMapping("/add")
        public Result add(@Valid @RequestBody AddAllocationOutDTOAndDetails addAllocationOutDTOAndDetails) {
            try {
                AddAllocationOutDTO addAllocationOutDTO = addAllocationOutDTOAndDetails.getAddAllocationOutDTO();
                List<AddAllocationOutDetailsDTO> addAllocationOutDetailsDTOList = addAllocationOutDTOAndDetails.getAddAllocationOutDetailsDTOS();
                Result result = allocationOutService.addAllocationOutDTO(addAllocationOutDTO);
                if (!result.isOk()) {
                    return Result.failure("新增调拨出库失败！");
                }
                AllocationOut allocationOut = (AllocationOut) result.getData();
                String docNum = allocationOut.getAllocationOutNumber();
                for (AddAllocationOutDetailsDTO addAllocationOutDetailsDTO : addAllocationOutDetailsDTOList) {
                    addAllocationOutDetailsDTO.setAllocationOutNumber(allocationOut.getAllocationOutNumber());
                }
                return allocationOutDetailsService.addAllocationOutDetails(addAllocationOutDetailsDTOList);
            } catch (Exception e) {
                log.error("新增调拨出库失败");
                return Result.failure("系统异常，新增调拨出库失败！");
            }
        }

        @ApiOperationSupport(order = 3)
        @ApiOperation(value = "更新", notes = "生成代码")
        @PutMapping("/update")
        public Result update(@Valid @RequestBody UpdateAllocationOutAndDetailsDTO updateAllocationOutAndDetailsDTO) {
            try {
                UpdateAllocationOutDTO updateAllocationOutDTO = updateAllocationOutAndDetailsDTO.getUpdateAllocationOutDTO();
                List<UpdateAllocationOutDetailsDTO> updateAllocationOutDetailsDTOS = updateAllocationOutAndDetailsDTO.getUpdateAllocationOutDetailsDTOS();
                Result result = allocationOutService.update(updateAllocationOutDTO);
                if (!result.isOk()) {
                    return Result.failure("更新调拨出库失败！");
                }
                return allocationOutDetailsService.updateAllocationOutDetails(updateAllocationOutDetailsDTOS);
            } catch (Exception e) {
                log.error("更新调拨出库失败");
                return Result.failure("系统异常：更新调拨出库失败!");
            }
        }

        @ApiOperationSupport(order = 4)
        @ApiOperation(value = "删除", notes = "生成代码")
        @DeleteMapping("/{id}")
        public Result delete(@PathVariable Integer id) {
            AllocationOut allocationOut = allocationOutService.getAllocationOutById(id);
            if (ObjectUtil.isNull(allocationOut)){
                return Result.failure("单据不存在！");
            }
            Boolean delete = allocationOutService.removeById(id);
            //主表删除成功,删除明细
            if (delete){
                String docNum = allocationOut.getAllocationOutNumber();
                List<AllocationOutDetails> allocationOutDetailsList = allocationOutDetailsService.getAllocationOutDetailsListByDocNum(docNum);
                for (AllocationOutDetails allocationOutDetails:allocationOutDetailsList
                ) {
                    allocationOutDetailsService.removeById(allocationOutDetails.getId());
                }
            }
            return Result.success("删除成功");
        }


        @ApiOperationSupport(order = 5)
        @ApiOperation(value = "根据ID获取调拨出库及其明细")
        @GetMapping("getAllocationOutByAndDetailsById/{id}")
        public Result getAllocationOutByAndDetailsById(@PathVariable Integer id) {
        JSONObject jsonObject = new JSONObject();
        AllocationOut allocationOut = allocationOutService.getAllocationOutById(id);
        if (ObjectUtil.isEmpty(allocationOut)) {
            return Result.failure("未找到对应信息！");
        }
        List<AllocationOutDetails> allocationOutDetailsList = allocationOutDetailsService.getAllocationOutDetailsListByDocNum(allocationOut.getAllocationOutNumber());
        jsonObject.put("doc", allocationOut);
        jsonObject.put("details", allocationOutDetailsList);
        return Result.success(jsonObject);
        }


        @ApiOperationSupport(order = 6)
        @ApiOperation(value = "根据单据编号获取调拨出库及其明细")
        @GetMapping("/getAllocationOutAndDetailsByDocNum")
        public Result getAllocationOutAndDetailsByDocNum(@RequestParam String docNum) {
        JSONObject jsonObject = new JSONObject();
        AllocationOut allocationOut = allocationOutService.getAllocationOutByDocNumber(docNum);
        if (ObjectUtil.isEmpty(allocationOut)) {
            return Result.failure("未找到对应信息！");
        }
        List<AllocationOutDetails> allocationOutDetailsList = allocationOutDetailsService.getAllocationOutDetailsListByDocNum(allocationOut.getAllocationOutNumber());
        jsonObject.put("doc", allocationOut);
        jsonObject.put("details", allocationOutDetailsList);
        return Result.success(jsonObject);
        }



        @ApiOperationSupport(order = 7)
        @ApiOperation(value = "PDA根据调拨出库单Id获取调拨出库明细list加出库记录（预生成、锁库）包含物料详情")
        @GetMapping("/getAllocationOutAndOutboundById")
        public Result getAllocationOutAndOutboundById(@RequestParam Integer id) {

            try {

                List listResult = new ArrayList();

                //加一层主表数据
                JSONObject jsonObjectMain = new JSONObject();

                //存储于list
                JSONObject jsonObjectList = new JSONObject();

                //根据单据id获取出库单主表
                AllocationOut allocationOut = allocationOutService.getAllocationOutById(id);

                if (ObjectUtil.isEmpty(allocationOut)){
                    return Result.failure("未查询到单据信息！");
                }

                List<AllocationOutDetails> allocationOutDetailsList;
                /**
                 * 判断out_status
                 * 0-未出库   1-部分出库   2-全部出库
                 */
                for (int i = 0; i < 3; i++ ){
                    allocationOutDetailsList = allocationOutDetailsService.getListAllocationOutDetailsByDocNumberAndOutStatus(allocationOut.getAllocationOutNumber(),i);
                    if (ObjectUtil.isNotNull(allocationOutDetailsList)) {
                        jsonObjectList.put(String.valueOf(i), getOut(allocationOutDetailsList));
                    }
                }

                listResult.add(jsonObjectList);
                //主表
                String warehouseName = warehouseManagementService.getWarehouseByWarehouseId(allocationOut.getSendWarehouse()).getWarehouseName();
                jsonObjectMain.put("warehouseName",warehouseName);
                jsonObjectMain.put("panUseOut",allocationOut);
                jsonObjectMain.put("list",listResult);
                return Result.success(jsonObjectMain);

            }catch (Exception e){
                log.error("PDA查询领料出库明细异常",e);
                return Result.failure("PDA查询领料出库明细异常!");
            }
        }


        @ApiOperationSupport(order = 8)
        @ApiOperation(value = "根据调拨计划单生成调拨出库单")
        @PutMapping("/allocationPlanToAllocationOut")
        public Result allocationPlanToAllocationOut(@Valid @RequestBody AllocationPlan allocationPlan){


            AddAllocationOutDTOAndDetails addAllocationOutDTOAndDetails = new AddAllocationOutDTOAndDetails();

            List<AddAllocationOutDetailsDTO> addAllocationOutDetailsDTOList = new ArrayList<>();

            LoginUser loginUser = this.getLoginUser();

            if (ObjectUtil.isNull(allocationPlan)){
                return Result.failure("单据不存在！");
            }

            /**
             * 处理主表
             */
            AddAllocationOutDTO addAllocationOutDTO = new AddAllocationOutDTO();

            BeanUtil.copyProperties(allocationPlan,addAllocationOutDTO);

            //库管员
            addAllocationOutDTO.setLibrarian(loginUser.getLoginName());
            //检验人
            addAllocationOutDTO.setVerification(loginUser.getLoginName());
            //调入仓库
            addAllocationOutDTO.setEnterWarehouse(allocationPlan.getReceiveWarehouse());

            addAllocationOutDTO.setRemark("系统自动生成");

            addAllocationOutDTO.setSendCompany(loginUser.getCompanyId().toString());

            addAllocationOutDTOAndDetails.setAddAllocationOutDTO(addAllocationOutDTO);

            /**
             * 处理明细
             */
            List<AllocationPlanDetail> allocationPlanDetailsList = allocationPlanDetailService.getAllocationPlanDetailsListByDocNum(allocationPlan.getAllocationNumber());
            for (AllocationPlanDetail allocationPlanDetail:allocationPlanDetailsList
                 ) {
                AddAllocationOutDetailsDTO addAllocationOutDetailsDTO = new AddAllocationOutDetailsDTO();
                BeanUtil.copyProperties(allocationPlanDetail,addAllocationOutDetailsDTO);
                addAllocationOutDetailsDTO.setRemark("系统自动生成");
                addAllocationOutDetailsDTOList.add(addAllocationOutDetailsDTO);
            }
            addAllocationOutDTOAndDetails.setAddAllocationOutDetailsDTOS(addAllocationOutDetailsDTOList);
            return  add(addAllocationOutDTOAndDetails);
        }



        public List<JSONObject> getOut(List<AllocationOutDetails> allocationOutDetailsList) {
            List<JSONObject> listResult = new ArrayList<>();
            for (AllocationOutDetails allocationOutDetails : allocationOutDetailsList
            ) {
                JSONObject jsonObject = new JSONObject();
                AllocationOut allocationOut = allocationOutService.getAllocationOutByDocNumber(allocationOutDetails.getAllocationOutNumber());
                List<OutboundRecord> outboundRecordList = outboundRecordService.getOutboundRecordListByDocNumAndWarehouseId(allocationOutDetails.getAllocationOutNumber(),allocationOut.getSendWarehouse());
                if (ObjectUtil.isEmpty(outboundRecordList)) {
                    Result.failure("未查询到出库记录单相关信息");
                }
                jsonObject.put("planUseOutDetails", allocationOutDetails);
                jsonObject.put("outboundList", outboundRecordList);
                jsonObject.put("material", materialService.getMeterialByMeterialCode(allocationOutDetails.getMaterialCoding()));
                List<InventoryInformation> inventoryInformationList = inventoryInformationService.getInventoryInformationListByMaterialCodingAndWarehouseId(allocationOutDetails.getMaterialCoding(),allocationOut.getSendWarehouse());
                jsonObject.put("inventory",inventoryInformationList);
                listResult.add(jsonObject);
            }
            return listResult;
        }


}

