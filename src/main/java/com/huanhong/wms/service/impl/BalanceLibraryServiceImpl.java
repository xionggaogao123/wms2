package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huanhong.common.exception.BizException;
import com.huanhong.common.units.StrUtils;
import com.huanhong.common.units.user.CurrentUserUtil;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.vo.BalanceLibraryDetailVo;
import com.huanhong.wms.entity.vo.BalanceLibraryRecordVo;
import com.huanhong.wms.entity.vo.BalanceLibraryVo;
import com.huanhong.wms.mapper.BalanceLibraryMapper;
import com.huanhong.wms.mapper.InventoryInformationMapper;
import com.huanhong.wms.mapper.ProcurementPlanMapper;
import com.huanhong.wms.mapper.WarehouseManagementMapper;
import com.huanhong.wms.service.IBalanceLibraryDetailService;
import com.huanhong.wms.service.IBalanceLibraryRecordService;
import com.huanhong.wms.service.IBalanceLibraryService;
import com.huanhong.wms.service.IProcurementPlanDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 平衡利库表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-24
 */
@Service
public class BalanceLibraryServiceImpl extends SuperServiceImpl<BalanceLibraryMapper, BalanceLibrary> implements IBalanceLibraryService {

    @Resource
    private ProcurementPlanMapper procurementPlanMapper;
    @Resource
    private IProcurementPlanDetailsService procurementPlanDetailsService;
    @Resource
    private IBalanceLibraryDetailService balanceLibraryDetailService;
    @Resource
    private IBalanceLibraryRecordService balanceLibraryRecordService;
    @Resource
    private WarehouseManagementMapper warehouseManagementMapper;
    @Resource
    private InventoryInformationMapper inventoryInformationMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result importProcurementPlans(List<Integer> procurementPlanIds) {
        if (CollectionUtil.isEmpty(procurementPlanIds)) {
            return Result.failure("请选择采购计划");
        }
        List<ProcurementPlan> procurementPlans = procurementPlanMapper.selectBatchIds(procurementPlanIds);
        if (CollectionUtil.isEmpty(procurementPlans)) {
            return Result.failure("采购计划不存在或已被删除");
        }
        for (ProcurementPlan procurementPlan : procurementPlans) {
            if (procurementPlan.getIsImported() == 1) {
                throw new BizException( Result.failure(400,StrUtil.format("采购计划单号：{},已被导入，不可重复导入", procurementPlan.getPlanNumber())));
            }
        }
        // 合并采购计划主表数据到平衡利库
        BalanceLibrary balanceLibrary = new BalanceLibrary();
        LoginUser loginUser = CurrentUserUtil.getCurrentUser();
        balanceLibrary.setCreater(loginUser.getLoginName());
        // likeRigh: BL+XXXXXXXX(当前年月日)
        String today = DateUtil.date().toString("yyyyMMdd");
        BalanceLibrary balanceLibrary1 = this.baseMapper.selectOne(Wrappers.<BalanceLibrary>lambdaQuery()
                .likeRight(BalanceLibrary::getBalanceLibraryNo, "BL" + today).orderByDesc(BalanceLibrary::getId).last("limit 1"));
        //目前最大的单据编码
        String maxDocNum = null;
        if (ObjectUtil.isNotEmpty(balanceLibrary1)) {
            maxDocNum = balanceLibrary1.getBalanceLibraryNo();
        }
        String no = null;
        //单据编码前缀-BL+年月日
        String codePrefix = "BL" + today;
        if (maxDocNum != null && balanceLibrary1.getBalanceLibraryNo().contains(codePrefix)) {
            String codeEnd = balanceLibrary1.getBalanceLibraryNo().substring(10, 14);
            int endNum = Integer.parseInt(codeEnd);
            int tmpNum = 10000 + endNum + 1;
            no = codePrefix + StrUtils.subStr("" + tmpNum, 1);
        } else {
            no = codePrefix + "0001";
        }
        balanceLibrary.setBalanceLibraryNo(no);
        List<String> procurementNos = new ArrayList<>();
        List<String> materialUse = new ArrayList<>();
        List<String> planner = new ArrayList<>();
        procurementPlans.forEach(pp -> {
            procurementNos.add(pp.getPlanNumber());
            materialUse.add(pp.getMaterialUse());
            planner.add(pp.getPlanner());

        });
        balanceLibrary.setProcurementNos(JSON.toJSONString(procurementNos));
        balanceLibrary.setMaterialUse(JSON.toJSONString(materialUse));
        balanceLibrary.setPlanner(JSON.toJSONString(planner));
        ProcurementPlan pp = procurementPlans.get(0);
        balanceLibrary.setPlanningDepartment(pp.getPlanningDepartment());
        balanceLibrary.setTargetWarehouse(pp.getWarehouseId());
        balanceLibrary.setDemandDepartment(pp.getDemandDepartment());
        balanceLibrary.setPlanClassification(pp.getPlanClassification());
        int flag = this.baseMapper.insert(balanceLibrary);
        if (flag < 1) {
            throw new BizException("创建平衡利库主表失败");
        }
        // 处理明细数据
        List<ProcurementPlanDetails> procurementPlanDetails = procurementPlanDetailsService.list(Wrappers.<ProcurementPlanDetails>lambdaQuery()
                .in(ProcurementPlanDetails::getPlanNumber, procurementNos));
        List<BalanceLibraryDetail> balanceLibraryDetails = new ArrayList<>();

        String finalNo = no;
        procurementPlanDetails.forEach(ppd -> {
            Optional<BalanceLibraryDetail> optionalBalanceLibraryDetail = balanceLibraryDetails.stream().filter(bld -> bld.getMaterialCoding().equals(ppd.getMaterialCoding())).findFirst();
            if (optionalBalanceLibraryDetail.isPresent()) {
                BalanceLibraryDetail balanceLibraryDetail = optionalBalanceLibraryDetail.get();
                balanceLibraryDetail.setApprovedQuantity(NumberUtil.add(balanceLibraryDetail.getApprovedQuantity(), ppd.getApprovedQuantity()));
                balanceLibraryDetail.setRequiredQuantity(NumberUtil.add(balanceLibraryDetail.getRequiredQuantity(), ppd.getRequiredQuantity()));
                balanceLibraryDetail.setPlannedPurchaseQuantity(NumberUtil.add(balanceLibraryDetail.getPlannedPurchaseQuantity(), ppd.getPlannedPurchaseQuantity()));
            } else {
                BalanceLibraryDetail balanceLibraryDetail = new BalanceLibraryDetail();
                BeanUtil.copyProperties(ppd, balanceLibraryDetail);
                balanceLibraryDetail.setBalanceLibraryNo(finalNo);
                balanceLibraryDetails.add(balanceLibraryDetail);
            }

        });
        boolean b = balanceLibraryDetailService.saveBatch(balanceLibraryDetails);
        if (!b) {
            throw new BizException("平衡利库明细表数据保存失败");
        }
        // 更新采购计划状态为已导入
        ProcurementPlan procurementPlan = new ProcurementPlan();
        procurementPlan.setIsImported(1);
        procurementPlan.setDocumentNumberImported(no);
        procurementPlanMapper.update(procurementPlan, Wrappers.<ProcurementPlan>lambdaUpdate()
                .in(ProcurementPlan::getId, procurementPlanIds));
        // 返回平衡利库详情
        return detail(balanceLibrary.getId());
    }

    @Override
    public Result detail(Integer id) {
        BalanceLibrary balanceLibrary = this.baseMapper.selectById(id);
        if (null == balanceLibrary) {
            return Result.failure("该平衡利库不存在或已被删除");
        }
        // 获取其他的仓库
        List<WarehouseManagement> warehouseManagements = warehouseManagementMapper.selectList(Wrappers.<WarehouseManagement>lambdaQuery()
                .ne(WarehouseManagement::getWarehouseId, balanceLibrary.getTargetWarehouse()));

        BalanceLibraryVo balanceLibraryVo = new BalanceLibraryVo();
        BeanUtil.copyProperties(balanceLibrary, balanceLibraryVo);
        List<BalanceLibraryDetail> balanceLibraryDetails = balanceLibraryDetailService.list(Wrappers.<BalanceLibraryDetail>lambdaQuery()
                .eq(BalanceLibraryDetail::getBalanceLibraryNo, balanceLibrary.getBalanceLibraryNo()));
        List<BalanceLibraryDetailVo> balanceLibraryDetailVos = new ArrayList<>();
        balanceLibraryDetails.forEach(bld -> {
            BalanceLibraryDetailVo balanceLibraryDetailVo = new BalanceLibraryDetailVo();
            BeanUtil.copyProperties(bld, balanceLibraryDetailVo);
            double sumCalibrationQuantity = 0D;
            double sumPreCalibrationQuantity = 0D;
            List<BalanceLibraryRecordVo> recordVos = new ArrayList<>();
            List<BalanceLibraryRecord> rs = balanceLibraryRecordService.list(Wrappers.<BalanceLibraryRecord>lambdaQuery()
                    .eq(BalanceLibraryRecord::getBalanceLibraryDetailId, bld.getId()));
            if (CollectionUtil.isNotEmpty(rs)) {
                sumCalibrationQuantity = rs.stream().map(r -> NumberUtil.add(r.getCalibrationQuantity(), r.getCalibrationQuantity2(), r.getCalibrationQuantity3()))
                        .reduce(new BigDecimal("0"), NumberUtil::add).doubleValue();
                sumPreCalibrationQuantity = rs.stream().map(r -> NumberUtil.add(r.getPreCalibrationQuantity(), r.getPreCalibrationQuantity2(), r.getPreCalibrationQuantity3()))
                        .reduce(new BigDecimal("0"), NumberUtil::add).doubleValue();
                //已有调拨操作
                warehouseManagements.forEach(wm -> {
                    BalanceLibraryRecordVo recordVo = new BalanceLibraryRecordVo();
                    Optional<BalanceLibraryRecord> optionalBalanceLibraryRecord = rs.stream().filter(r -> r.getOutWarehouseId().equals(wm.getWarehouseId()))
                            .findFirst();
                    if (optionalBalanceLibraryRecord.isPresent()) {
                        BalanceLibraryRecord balanceLibraryRecord = optionalBalanceLibraryRecord.get();
                        BeanUtil.copyProperties(balanceLibraryRecord, recordVo);
                    }
                    recordVo.setOutWarehouseId(wm.getWarehouseId());
                    recordVo.setOutWarehouse(wm.getWarehouseName());
                    Double inventoryCredit = inventoryInformationMapper.sumInventoryCreditByWarehouseMaterialCoding(wm.getWarehouseId(), bld.getMaterialCoding());
                    recordVo.setInventoryCredit(inventoryCredit);
                    recordVos.add(recordVo);
                });

            } else {
                // 统计每个仓库该物料的库存
                warehouseManagements.forEach(wm -> {
                    BalanceLibraryRecordVo recordVo = new BalanceLibraryRecordVo();
                    recordVo.setOutWarehouseId(wm.getWarehouseId());
                    recordVo.setOutWarehouse(wm.getWarehouseName());
                    Double inventoryCredit = inventoryInformationMapper.sumInventoryCreditByWarehouseMaterialCoding(wm.getWarehouseId(), bld.getMaterialCoding());
                    recordVo.setInventoryCredit(inventoryCredit);
                    recordVos.add(recordVo);
                });
            }
            balanceLibraryDetailVo.setSumCalibrationQuantity(sumCalibrationQuantity);
            balanceLibraryDetailVo.setSumPreCalibrationQuantity(sumPreCalibrationQuantity);
            balanceLibraryDetailVo.setPurchasedQuantity(NumberUtil.sub(Convert.toDouble(bld.getApprovedQuantity(), 0D).doubleValue(), sumCalibrationQuantity));
            balanceLibraryDetailVo.setRecords(recordVos);
            balanceLibraryDetailVos.add(balanceLibraryDetailVo);
        });
        balanceLibraryVo.setDetails(balanceLibraryDetailVos);
        return Result.success(balanceLibraryVo);
    }
}
