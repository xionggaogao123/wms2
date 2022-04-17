package com.huanhong.wms.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.annotion.OperateLog;
import com.huanhong.common.enums.OperateType;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.param.InventoryInfoVoPage;
import com.huanhong.wms.entity.vo.InventoryInfoVo;
import com.huanhong.wms.service.IInventoryInformationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/v1/report")
@Api(tags = "报表控制器")
public class ReportController extends BaseController {

    @Autowired
    private IInventoryInformationService iInventoryInformationService;

    @OperateLog(title = "库存账_查询", type = OperateType.QUERY)
    @ApiOperation(value = "库存账_查询")
    @GetMapping("/inventoryBill")
    public Result<Page<InventoryInfoVo>> inventoryBill(InventoryInfoVoPage page){
        return iInventoryInformationService.inventoryBill(page);
    }
    @OperateLog(title = "库存账_导出", type = OperateType.EXPORT)
    @ApiOperation(value = "库存账_导出")
    @GetMapping("/inventoryBillExport")
    public void inventoryBillExport(InventoryInfoVoPage page, HttpServletRequest request,
                                    HttpServletResponse response){
        page.setSize(30000);
        LoginUser loginUser = getLoginUser();
        page.setUserId(loginUser.getId());
        page.setUserName(loginUser.getUserName());
        iInventoryInformationService.inventoryBillExport(page,request,response);
    }

    @OperateLog(title = "呆滞货物明细表(查询)_查询", type = OperateType.QUERY)
    @ApiOperation(value = "呆滞货物明细表(查询)_查询")
    @GetMapping("/deadGoods")
    public Result<Page<InventoryInfoVo>> deadGoods(InventoryInfoVoPage page){
        if(null == page.getInMonth()){
            page.setInMonth(6);
        }
        return iInventoryInformationService.deadGoods(page);
    }
    @OperateLog(title = "呆滞货物明细表(查询)_导出", type = OperateType.EXPORT)
    @ApiOperation(value = "呆滞货物明细表(查询)_导出")
    @GetMapping("/deadGoodsExport")
    public void deadGoodsExport(InventoryInfoVoPage page, HttpServletRequest request,
                                    HttpServletResponse response){
        if(null == page.getInMonth()){
            page.setInMonth(6);
        }
        page.setSize(30000);
        LoginUser loginUser = getLoginUser();
        page.setUserId(loginUser.getId());
        page.setUserName(loginUser.getUserName());
        iInventoryInformationService.deadGoodsExport(page,request,response);
    }

    @OperateLog(title = "呆滞货物明细表(结算)_查询", type = OperateType.QUERY)
    @ApiOperation(value = "呆滞货物明细表(结算)_查询")
    @GetMapping("/deadGoodsSettle")
    public Result<Page<InventoryInfoVo>> deadGoodsSettle(InventoryInfoVoPage page){
        if(null == page.getConsignor()){
            page.setConsignor(0);
        }
        if(null == page.getInMonth()){
            page.setInMonth(6);
        }
        return iInventoryInformationService.deadGoodsSettle(page);
    }
    @OperateLog(title = "呆滞货物明细表(结算)_导出", type = OperateType.EXPORT)
    @ApiOperation(value = "呆滞货物明细表(结算)_导出")
    @GetMapping("/deadGoodsSettleExport")
    public void deadGoodsSettleExport(InventoryInfoVoPage page, HttpServletRequest request,
                                    HttpServletResponse response){
        if(null == page.getConsignor()){
            page.setConsignor(0);
        }
        if(null == page.getInMonth()){
            page.setInMonth(6);
        }
        page.setSize(30000);
        LoginUser loginUser = getLoginUser();
        page.setUserId(loginUser.getId());
        page.setUserName(loginUser.getUserName());
        iInventoryInformationService.deadGoodsSettleExport(page,request,response);
    }
}
