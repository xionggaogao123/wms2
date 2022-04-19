package com.huanhong.wms.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.annotion.OperateLog;
import com.huanhong.common.enums.OperateType;
import com.huanhong.wms.BaseController;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.param.InventoryInfoPage;
import com.huanhong.wms.entity.param.OutboundDetailPage;
import com.huanhong.wms.entity.param.WarehousingDetailPage;
import com.huanhong.wms.entity.vo.InventoryInfoVo;
import com.huanhong.wms.entity.vo.OutboundDetailVo;
import com.huanhong.wms.entity.vo.WarehousingDetailVo;
import com.huanhong.wms.service.IInventoryInformationService;
import com.huanhong.wms.service.IOutboundRecordService;
import com.huanhong.wms.service.IWarehousingRecordService;
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
    private IInventoryInformationService inventoryInformationService;
    @Autowired
    private IWarehousingRecordService warehousingRecordService;
    @Autowired
    private IOutboundRecordService outboundRecordService;

    @OperateLog(title = "库存账_查询", type = OperateType.QUERY)
    @ApiOperation(value = "库存账_查询")
    @GetMapping("/inventoryBill")
    public Result<Page<InventoryInfoVo>> inventoryBill(InventoryInfoPage page){
        return inventoryInformationService.inventoryBill(page);
    }
    @OperateLog(title = "库存账_导出", type = OperateType.EXPORT)
    @ApiOperation(value = "库存账_导出")
    @GetMapping("/inventoryBillExport")
    public void inventoryBillExport(InventoryInfoPage page, HttpServletRequest request,
                                    HttpServletResponse response){
        page.setSize(30000);
        LoginUser loginUser = getLoginUser();
        page.setUserId(loginUser.getId());
        page.setUserName(loginUser.getUserName());
        inventoryInformationService.inventoryBillExport(page,request,response);
    }

    @OperateLog(title = "呆滞货物明细表(查询)_查询", type = OperateType.QUERY)
    @ApiOperation(value = "呆滞货物明细表(查询)_查询")
    @GetMapping("/deadGoods")
    public Result<Page<InventoryInfoVo>> deadGoods(InventoryInfoPage page){
        if(null == page.getInMonth()){
            page.setInMonth(6);
        }
        return inventoryInformationService.deadGoods(page);
    }
    @OperateLog(title = "呆滞货物明细表(查询)_导出", type = OperateType.EXPORT)
    @ApiOperation(value = "呆滞货物明细表(查询)_导出")
    @GetMapping("/deadGoodsExport")
    public void deadGoodsExport(InventoryInfoPage page, HttpServletRequest request,
                                HttpServletResponse response){
        if(null == page.getInMonth()){
            page.setInMonth(6);
        }
        page.setSize(30000);
        LoginUser loginUser = getLoginUser();
        page.setUserId(loginUser.getId());
        page.setUserName(loginUser.getUserName());
        inventoryInformationService.deadGoodsExport(page,request,response);
    }

    @OperateLog(title = "呆滞货物明细表(结算)_查询", type = OperateType.QUERY)
    @ApiOperation(value = "呆滞货物明细表(结算)_查询")
    @GetMapping("/deadGoodsSettle")
    public Result<Page<InventoryInfoVo>> deadGoodsSettle(InventoryInfoPage page){
        if(null == page.getConsignor()){
            page.setConsignor(0);
        }
        if(null == page.getInMonth()){
            page.setInMonth(6);
        }
        return inventoryInformationService.deadGoodsSettle(page);
    }
    @OperateLog(title = "呆滞货物明细表(结算)_导出", type = OperateType.EXPORT)
    @ApiOperation(value = "呆滞货物明细表(结算)_导出")
    @GetMapping("/deadGoodsSettleExport")
    public void deadGoodsSettleExport(InventoryInfoPage page, HttpServletRequest request,
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
        inventoryInformationService.deadGoodsSettleExport(page,request,response);
    }

    @OperateLog(title = "入库明细表_查询", type = OperateType.QUERY)
    @ApiOperation(value = "入库明细表_查询")
    @GetMapping("/warehousingDetail")
    public Result<Page<WarehousingDetailVo>> warehousingDetail(WarehousingDetailPage page){
        if(null == page.getInDateStart()){
            page.setInDateStart(DateUtil.lastMonth());
        }
        if(null == page.getInDateEnd()){
            page.setInDateEnd(DateUtil.date());
        }
        return warehousingRecordService.warehousingDetail(page);
    }
    @OperateLog(title = "入库明细表_导出", type = OperateType.EXPORT)
    @ApiOperation(value = "入库明细表_导出")
    @GetMapping("/warehousingDetailExport")
    public void warehousingDetailExport(WarehousingDetailPage page, HttpServletRequest request,
                                        HttpServletResponse response){
        if(null == page.getInDateStart()){
            page.setInDateStart(DateUtil.lastMonth());
        }
        if(null == page.getInDateEnd()){
            page.setInDateEnd(DateUtil.date());
        }
        page.setSize(30000);
        LoginUser loginUser = getLoginUser();
        page.setUserId(loginUser.getId());
        page.setUserName(loginUser.getUserName());
        warehousingRecordService.warehousingDetailExport(page,request,response);
    }
    @OperateLog(title = "领料出库明细表_查询", type = OperateType.QUERY)
    @ApiOperation(value = "领料出库明细表_查询")
    @GetMapping("/outboundDetail")
    public Result<Page<OutboundDetailVo>> outboundDetail(OutboundDetailPage page){
        if(null == page.getGmtStart()){
            page.setGmtStart(DateUtil.lastMonth());
        }
        if(null == page.getGmtEnd()){
            page.setGmtEnd(DateUtil.date());
        }
        return outboundRecordService.outboundDetail(page);
    }
    @OperateLog(title = "领料出库明细表_导出", type = OperateType.EXPORT)
    @ApiOperation(value = "领料出库明细表_导出")
    @GetMapping("/outboundDetailExport")
    public void outboundDetailExport(OutboundDetailPage page, HttpServletRequest request,
                                        HttpServletResponse response){
        if(null == page.getGmtStart()){
            page.setGmtStart(DateUtil.lastMonth());
        }
        if(null == page.getGmtEnd()){
            page.setGmtEnd(DateUtil.date());
        }
        page.setSize(30000);
        LoginUser loginUser = getLoginUser();
        page.setUserId(loginUser.getId());
        page.setUserName(loginUser.getUserName());
        outboundRecordService.outboundDetailExport(page,request,response);
    }
}