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
import com.huanhong.wms.mapper.*;
import com.huanhong.wms.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Resource
    private IAllocationPlanService allocationPlanService;
    @Resource
    private IProcurementPlanService procurementPlanService;
    @Resource
    private VariableMapper variableMapper;

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
                throw new BizException(Result.failure(400, StrUtil.format("采购计划单号：{},已被导入，不可重复导入", procurementPlan.getPlanNumber())));
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
        List<Variable> variables = variableMapper.selectList(Wrappers.<Variable>lambdaQuery()
                .eq(Variable::getKey, "consignor"));
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
                    List<BalanceLibraryRecord> balanceLibraryRecordList = rs.stream().filter(r -> r.getOutWarehouseId().equals(wm.getWarehouseId()))
                            .collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(balanceLibraryRecordList)) {

                        for (BalanceLibraryRecord balanceLibraryRecord : balanceLibraryRecordList) {
                            BalanceLibraryRecordVo recordVo = new BalanceLibraryRecordVo();
                            BeanUtil.copyProperties(balanceLibraryRecord, recordVo);
                            if (balanceLibraryRecord.getConsignor() == 0) {
                                //自有
                                recordVo.setOutWarehouseId(wm.getWarehouseId());
                                recordVo.setOutWarehouse(wm.getWarehouseName());
                                Double inventoryCredit = inventoryInformationMapper.sumInventoryCreditByWarehouseMaterialCoding(wm.getWarehouseId(), bld.getMaterialCoding(), String.valueOf(1));
                                recordVo.setInventoryCredit(inventoryCredit);
                                recordVo.setIsOwn(1);
                                recordVo.setBalanceLibraryDetailId(bld.getId());
                                recordVo.setMaterialCoding(bld.getMaterialCoding());
                                recordVo.setBalanceLibraryNo(bld.getBalanceLibraryNo());
                                recordVo.setMaterialId(bld.getMaterialId());
                                recordVo.setMaterialName(bld.getMaterialName());
                                recordVo.setConsignor(0);
                                recordVos.add(recordVo);
                                if (balanceLibraryRecordList.size() == 1) {
                                    BalanceLibraryRecordVo recordVo2 = new BalanceLibraryRecordVo();
                                    recordVo2.setOutWarehouseId(wm.getWarehouseId());
                                    recordVo2.setOutWarehouse(wm.getWarehouseName());
                                    Double inventoryCredit2 = inventoryInformationMapper.sumInventoryCreditByWarehouseMaterialCoding(wm.getWarehouseId(), bld.getMaterialCoding(), String.valueOf(0));
                                    recordVo2.setInventoryCredit(inventoryCredit2);
                                    recordVo2.setIsOwn(0);
                                    recordVo2.setBalanceLibraryDetailId(bld.getId());
                                    recordVo2.setMaterialCoding(bld.getMaterialCoding());
                                    recordVo2.setBalanceLibraryNo(bld.getBalanceLibraryNo());
                                    recordVo2.setMaterialId(bld.getMaterialId());
                                    recordVo2.setMaterialName(bld.getMaterialName());
                                    Integer consignor = variables.stream().filter(v -> v.getParentValue().equals(wm.getWarehouseId())).mapToInt(v -> Convert.toInt(v.getValue())).findFirst().getAsInt();
                                    recordVo2.setConsignor(consignor);
                                    recordVos.add(recordVo2);
                                }
                            } else {
                                //代管
                                BalanceLibraryRecordVo recordVo2 = new BalanceLibraryRecordVo();
                                BeanUtil.copyProperties(balanceLibraryRecord, recordVo2);
                                Double inventoryCredit2 = inventoryInformationMapper.sumInventoryCreditByWarehouseMaterialCoding(wm.getWarehouseId(), bld.getMaterialCoding(), String.valueOf(0));
                                recordVo2.setInventoryCredit(inventoryCredit2);
                                recordVo2.setIsOwn(0);
                                recordVo2.setBalanceLibraryDetailId(bld.getId());
                                recordVo2.setMaterialCoding(bld.getMaterialCoding());
                                recordVo2.setBalanceLibraryNo(bld.getBalanceLibraryNo());
                                recordVo2.setMaterialId(bld.getMaterialId());
                                recordVo2.setMaterialName(bld.getMaterialName());
                                Integer consignor = variables.stream().filter(v -> v.getParentValue().equals(wm.getWarehouseId())).mapToInt(v -> Convert.toInt(v.getValue())).findFirst().getAsInt();
                                recordVo2.setConsignor(consignor);
                                recordVos.add(recordVo2);
                                if (balanceLibraryRecordList.size() == 1) {
                                    BalanceLibraryRecordVo recordVo3 = new BalanceLibraryRecordVo();
                                    recordVo3.setOutWarehouseId(wm.getWarehouseId());
                                    recordVo3.setOutWarehouse(wm.getWarehouseName());
                                    Double inventoryCredit = inventoryInformationMapper.sumInventoryCreditByWarehouseMaterialCoding(wm.getWarehouseId(), bld.getMaterialCoding(), String.valueOf(1));
                                    recordVo3.setInventoryCredit(inventoryCredit);
                                    recordVo3.setIsOwn(1);
                                    recordVo3.setConsignor(0);
                                    recordVo3.setBalanceLibraryDetailId(bld.getId());
                                    recordVo3.setMaterialCoding(bld.getMaterialCoding());
                                    recordVo3.setBalanceLibraryNo(bld.getBalanceLibraryNo());
                                    recordVo3.setMaterialId(bld.getMaterialId());
                                    recordVo3.setMaterialName(bld.getMaterialName());
                                    recordVos.add(recordVo3);
                                }
                            }
                        }

                    } else {
                        BalanceLibraryRecordVo recordVo = new BalanceLibraryRecordVo();
                        recordVo.setOutWarehouseId(wm.getWarehouseId());
                        recordVo.setOutWarehouse(wm.getWarehouseName());
                        Double inventoryCredit = inventoryInformationMapper.sumInventoryCreditByWarehouseMaterialCoding(wm.getWarehouseId(), bld.getMaterialCoding(), String.valueOf(1));
                        recordVo.setInventoryCredit(inventoryCredit);
                        recordVo.setIsOwn(1);
                        recordVo.setConsignor(0);
                        recordVo.setBalanceLibraryDetailId(bld.getId());
                        recordVo.setMaterialCoding(bld.getMaterialCoding());
                        recordVo.setBalanceLibraryNo(bld.getBalanceLibraryNo());
                        recordVo.setMaterialId(bld.getMaterialId());
                        recordVo.setMaterialName(bld.getMaterialName());
                        recordVos.add(recordVo);
                        BalanceLibraryRecordVo recordVo2 = new BalanceLibraryRecordVo();
                        BeanUtil.copyProperties(recordVo, recordVo2);
                        Double inventoryCredit2 = inventoryInformationMapper.sumInventoryCreditByWarehouseMaterialCoding(wm.getWarehouseId(), bld.getMaterialCoding(), String.valueOf(0));
                        recordVo2.setInventoryCredit(inventoryCredit2);
                        recordVo2.setIsOwn(0);
                        recordVo2.setBalanceLibraryDetailId(bld.getId());
                        recordVo2.setMaterialCoding(bld.getMaterialCoding());
                        recordVo2.setBalanceLibraryNo(bld.getBalanceLibraryNo());
                        recordVo2.setMaterialId(bld.getMaterialId());
                        recordVo2.setMaterialName(bld.getMaterialName());
                        Integer consignor = variables.stream().filter(v -> v.getParentValue().equals(wm.getWarehouseId())).mapToInt(v -> Convert.toInt(v.getValue())).findFirst().getAsInt();
                        recordVo2.setConsignor(consignor);
                        recordVos.add(recordVo2);
                    }
                });

            } else {
                // 统计每个仓库该物料的库存
                warehouseManagements.forEach(wm -> {
                    BalanceLibraryRecordVo recordVo = new BalanceLibraryRecordVo();
                    recordVo.setOutWarehouseId(wm.getWarehouseId());
                    recordVo.setOutWarehouse(wm.getWarehouseName());
                    Double inventoryCredit = inventoryInformationMapper.sumInventoryCreditByWarehouseMaterialCoding(wm.getWarehouseId(), bld.getMaterialCoding(), String.valueOf(1));
                    recordVo.setInventoryCredit(inventoryCredit);
                    recordVo.setIsOwn(1);
                    recordVo.setConsignor(0);
                    recordVo.setBalanceLibraryDetailId(bld.getId());
                    recordVo.setMaterialCoding(bld.getMaterialCoding());
                    recordVo.setBalanceLibraryNo(bld.getBalanceLibraryNo());
                    recordVo.setMaterialId(bld.getMaterialId());
                    recordVo.setMaterialName(bld.getMaterialName());
                    recordVos.add(recordVo);
                    BalanceLibraryRecordVo recordVo2 = new BalanceLibraryRecordVo();
                    BeanUtil.copyProperties(recordVo, recordVo2);
                    Double inventoryCredit2 = inventoryInformationMapper.sumInventoryCreditByWarehouseMaterialCoding(wm.getWarehouseId(), bld.getMaterialCoding(), String.valueOf(0));
                    recordVo2.setInventoryCredit(inventoryCredit2);
                    recordVo2.setIsOwn(0);
                    recordVo2.setBalanceLibraryDetailId(bld.getId());
                    recordVo2.setMaterialCoding(bld.getMaterialCoding());
                    recordVo2.setBalanceLibraryNo(bld.getBalanceLibraryNo());
                    recordVo2.setMaterialId(bld.getMaterialId());
                    recordVo2.setMaterialName(bld.getMaterialName());
                    Integer consignor = variables.stream().filter(v -> v.getParentValue().equals(wm.getWarehouseId())).mapToInt(v -> Convert.toInt(v.getValue())).findFirst().getAsInt();
                    recordVo2.setConsignor(consignor);
                    recordVos.add(recordVo2);
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result delete(Integer id) {
        BalanceLibrary balanceLibrary = this.baseMapper.selectById(id);
        if (null == balanceLibrary) {
            return Result.success("该平衡利库已被删除");
        }
        List<BalanceLibraryDetail> libraryDetailList = balanceLibraryDetailService.list(Wrappers.lambdaQuery(new BalanceLibraryDetail())
                .eq(BalanceLibraryDetail::getBalanceLibraryNo, balanceLibrary.getBalanceLibraryNo()));
        if (CollectionUtil.isNotEmpty(libraryDetailList)) {
            List<Integer> detailIds = libraryDetailList.stream().map(BalanceLibraryDetail::getId).collect(Collectors.toList());
            // 判断是否有调拨记录
            List<BalanceLibraryRecord> records = balanceLibraryRecordService.list(Wrappers.lambdaQuery(new BalanceLibraryRecord())
                    .in(BalanceLibraryRecord::getBalanceLibraryDetailId, detailIds));
            if (CollectionUtil.isNotEmpty(records)) {
                List<String> planNos = new ArrayList<>();
                for (BalanceLibraryRecord record : records) {
                    if (StrUtil.isNotBlank(record.getPlanNo())) {
                        planNos.add(record.getPlanNo());
                    }
                    if (StrUtil.isNotBlank(record.getPlanNo2())) {
                        planNos.add(record.getPlanNo2());
                    }
                    if (StrUtil.isNotBlank(record.getPlanNo3())) {
                        planNos.add(record.getPlanNo3());
                    }
                }
                allocationPlanService.deleteByPlanNos(planNos);
                List<Integer> recordIds = records.stream().map(BalanceLibraryRecord::getId).collect(Collectors.toList());
                balanceLibraryRecordService.removeByIds(recordIds);
            }
            balanceLibraryDetailService.removeByIds(detailIds);
        }
        // 删除创建的采购计划
        procurementPlanService.remove(Wrappers.<ProcurementPlan>lambdaUpdate().eq(ProcurementPlan::getBalanceLibraryNo, balanceLibrary.getBalanceLibraryNo()));
        this.baseMapper.deleteById(id);

        // 回滚之前导入的采购计划
        String originalDocumentNumber = balanceLibrary.getProcurementNos();
        String[] originalDocumentNumbers = JSON.parseArray(originalDocumentNumber).toArray(new String[]{});
        procurementPlanService.updateIsImportedByPlanNumbers(0, "", originalDocumentNumbers, 0);
        return Result.success();
    }
}
