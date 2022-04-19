package com.huanhong.wms.controller;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.huanhong.common.annotion.OperateLog;
import com.huanhong.common.enums.OperateType;
import com.huanhong.common.units.TokenUtil;
import com.huanhong.common.units.sms.AliSmsTool;
import com.huanhong.common.units.sms.SMSResult;
import com.huanhong.common.units.weixin.WeiXinUtil;
import com.huanhong.common.units.weixin.WeixinConstant;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.RedisKey;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.entity.dto.LoginDTO;
import com.huanhong.wms.entity.param.DeptMaterialParam;
import com.huanhong.wms.entity.param.MaterialPriceParam;
import com.huanhong.wms.entity.param.MaterialProfitParam;
import com.huanhong.wms.properties.OssProperties;
import com.huanhong.wms.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/v1/data")
@ApiSort(1)
@Api(tags = "数据分析 📈")
public class DataController extends BaseController {


    @Resource
    private IWarehouseManagementService warehouseManagementService;

    @Resource
    private IRequirementsPlanningService requirementsPlanningService;

    @Resource
    private IEnterWarehouseService enterWarehouseService;

    @Resource
    private IInventoryInformationService inventoryInformationService;

    @Resource
    private IProcurementPlanService procurementPlanService;



    @ApiOperation(value = "小程序首页")
    @GetMapping("/home")
    public Result<Object> miniProgramLogout() {
        LoginUser loginUser = this.getLoginUser();
        return warehouseManagementService.selectWarehouseInfo(loginUser.getId());
    }

    @OperateLog(title = "部门物料需求与领用对比_查询",type = OperateType.QUERY)
    @ApiOperation(value = "部门物料需求与领用对比")
    @GetMapping("/dept")
    public Result<Object> deptMaterialNeedAndUse(DeptMaterialParam param) {
        LoginUser loginUser = this.getLoginUser();
        return requirementsPlanningService.getDeptMaterialNeedAndUseByParam(param);
    }

    @OperateLog(title = "物料价格波动_查询",type = OperateType.QUERY)
    @ApiOperation(value = "物料价格波动")
    @GetMapping("/price")
    public Result<Object> materialPrice(MaterialPriceParam param) {
        LoginUser loginUser = this.getLoginUser();
        return enterWarehouseService.getMaterialPriceByParam(param);
    }

    @OperateLog(title = "各物料平均利润率分析_查询",type = OperateType.QUERY)
    @ApiOperation(value = "各物料平均利润率分析")
    @GetMapping("/profit")
    public Result<Object> materialProfit(MaterialProfitParam param) {
        LoginUser loginUser = this.getLoginUser();
        return inventoryInformationService.getMaterialProfit(param) ;
    }

    @OperateLog(title = "物料采购频次与数量月度分析_查询",type = OperateType.QUERY)
    @ApiOperation(value = "物料采购频次与数量月度分析")
    @GetMapping("/materialFQ")
    public Result<Object> monthlyAnalysisOfPurchasingFrequencyAndQuantity(DeptMaterialParam param) {
        LoginUser loginUser = this.getLoginUser();
        return procurementPlanService.getProcurementPlanFrequencyAndQuantity(param) ;
    }

    @OperateLog(title = "物料采购同比分析_查询",type = OperateType.QUERY)
    @ApiOperation(value = "物料采购同比分析")
    @GetMapping("/purchasing")
    public Result<Object> materialPurchasingAnalysisOnYearBasis() {
        LoginUser loginUser = this.getLoginUser();
        return null ;
    }

    @OperateLog(title = "物料库存ABC分析_查询",type = OperateType.QUERY)
    @ApiOperation(value = "物料库存ABC分析")
    @GetMapping("/inventoryABC")
    public Result<Object> materialInventoryABCAnalysis() {
        LoginUser loginUser = this.getLoginUser();
        return null ;
    }

    @OperateLog(title = "低于安全库存物料预警_查询",type = OperateType.QUERY)
    @ApiOperation(value = "低于安全库存物料预警")
    @GetMapping("/belowWarning")
    public Result<Object> belowSafetyStockMaterialWarning() {
        LoginUser loginUser = this.getLoginUser();
        return null ;
    }

    @OperateLog(title = "预过期信息预警_查询",type = OperateType.QUERY)
    @ApiOperation(value = "预过期信息预警")
    @GetMapping("/preExpirationWarning")
    public Result<Object> preExpirationWarning() {
        LoginUser loginUser = this.getLoginUser();
        return null ;
    }

    @OperateLog(title = "各仓库出入库趋势分析_查询",type = OperateType.QUERY)
    @ApiOperation(value = "各仓库出入库趋势分析")
    @GetMapping("/trend-in-out")
    public Result<Object> analysisOfTheTrendOfWarehouseInboundAndOutbound() {
        LoginUser loginUser = this.getLoginUser();
        return null ;
    }

    @OperateLog(title = "出入库金额统计分析_查询",type = OperateType.QUERY)
    @ApiOperation(value = "出入库金额统计分析")
    @GetMapping("/amount-in-out")
    public Result<Object> statisticalAnalysisOfInboundAndOutboundAmount() {
        LoginUser loginUser = this.getLoginUser();
        return null ;
    }

    @OperateLog(title = "仓库使用情况分析_查询",type = OperateType.QUERY)
    @ApiOperation(value = "仓库使用情况分析")
    @GetMapping("/warehouseUsage")
    public Result<Object> warehouseUsageAnalysis() {
        LoginUser loginUser = this.getLoginUser();
        return null ;
    }

    @OperateLog(title = "合同跟踪分析_查询",type = OperateType.QUERY)
    @ApiOperation(value = "合同跟踪分析")
    @GetMapping("/contractTracking")
    public Result<Object> contractTrackingAnalysis() {
        LoginUser loginUser = this.getLoginUser();
        return null ;
    }
}
