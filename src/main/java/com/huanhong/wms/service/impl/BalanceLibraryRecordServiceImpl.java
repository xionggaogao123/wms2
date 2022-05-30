package com.huanhong.wms.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huanhong.common.exception.BizException;
import com.huanhong.common.units.user.CurrentUserUtil;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.AddAllocationPlanAndDetailsDTO;
import com.huanhong.wms.entity.dto.AddAllocationPlanDTO;
import com.huanhong.wms.entity.dto.AddAllocationPlanDetailDTO;
import com.huanhong.wms.mapper.*;
import com.huanhong.wms.service.IAllocationPlanService;
import com.huanhong.wms.service.IBalanceLibraryRecordService;
import com.huanhong.wms.service.IProcurementPlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 平衡利库记录 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-24
 */
@Service
public class BalanceLibraryRecordServiceImpl extends SuperServiceImpl<BalanceLibraryRecordMapper, BalanceLibraryRecord> implements IBalanceLibraryRecordService {

    @Resource
    private IAllocationPlanService allocationPlanService;

    @Resource
    private IProcurementPlanService procurementPlanService;

    @Resource
    private BalanceLibraryDetailMapper balanceLibraryDetailMapper;
    @Resource
    private BalanceLibraryMapper balanceLibraryMapper;
    @Resource
    private InventoryInformationMapper inventoryInformationMapper;
    @Resource
    private VariableMapper variableMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result add(List<BalanceLibraryRecord> balanceLibraryRecords) {
        if (CollectionUtil.isEmpty(balanceLibraryRecords)) {
            return Result.failure(400, "参数不可为空");
        }
        LoginUser loginUser = CurrentUserUtil.getCurrentUser();
        // 仓库：调拨
        Map<String, AddAllocationPlanAndDetailsDTO> addAllocationPlanAndDetailsDTOMap = new HashMap<>();
        Map<String, List<BalanceLibraryRecord>> balanceLibraryRecordMap = new HashMap<>();
        for (BalanceLibraryRecord balanceLibraryRecord : balanceLibraryRecords) {
            Integer balanceLibraryDetailId = balanceLibraryRecord.getBalanceLibraryDetailId();
            if (null == balanceLibraryDetailId) {
                throw new BizException(Result.failure(400, "平衡利库明细ID不可为空"));
            }
            BalanceLibraryDetail balanceLibraryDetail = balanceLibraryDetailMapper.selectById(balanceLibraryDetailId);
            if (null == balanceLibraryDetail) {
                throw new BizException(Result.failure(400, "平衡利库明细不存在或已被删除"));
            }
            BalanceLibrary balanceLibrary = balanceLibraryMapper.selectOne(Wrappers.<BalanceLibrary>lambdaQuery()
                    .eq(BalanceLibrary::getBalanceLibraryNo, balanceLibraryDetail.getBalanceLibraryNo())
                    .orderByDesc(BalanceLibrary::getId).last("limit 1"));
            if (null == balanceLibrary) {
                throw new BizException(Result.failure(400, "平衡利库不存在或已被删除"));
            }
            // 查询该明细下是否有待审批的调拨，如果有的话不可以再次调拨，需审批通过才可以
            int count = this.baseMapper.selectCount(Wrappers.lambdaQuery(new BalanceLibraryRecord())
                    .eq(BalanceLibraryRecord::getBalanceLibraryDetailId, balanceLibraryDetailId).and(wrapper -> wrapper.ne(BalanceLibraryRecord::getCalibrationStatus, 3).or()
                            .ne(BalanceLibraryRecord::getCalibrationStatus2, 3).or()
                            .ne(BalanceLibraryRecord::getCalibrationStatus3, 3))
            );
            if (count > 0) {
                throw new BizException(Result.failure("存在不是审批生效的调拨计划，请等待审批通过后再操作"));
            }
            // 库存
            List<InventoryInformation> inventoryInformations = inventoryInformationMapper.selectList(Wrappers.<InventoryInformation>lambdaQuery().eq(InventoryInformation::getMaterialCoding, balanceLibraryDetail.getMaterialCoding())
                    .likeRight(InventoryInformation::getCargoSpaceId, balanceLibraryRecord.getOutWarehouseId()).gt(InventoryInformation::getInventoryCredit, 0));
            if (CollectionUtil.isEmpty(inventoryInformations)) {
                throw new BizException(Result.failure(1001, StrUtil.format("仓库：{},物料：{},库存不足", balanceLibraryRecord.getOutWarehouse(), balanceLibraryDetail.getMaterialCoding())));
            }
            // 判断数量是否符合条件
            List<BalanceLibraryRecord> libraryRecords = this.baseMapper.selectList(Wrappers.lambdaQuery(new BalanceLibraryRecord())
                    .eq(BalanceLibraryRecord::getBalanceLibraryDetailId, balanceLibraryDetailId));
            double sumCalibrationQuantity = libraryRecords.stream().map(r -> NumberUtil.add(r.getCalibrationQuantity(), r.getCalibrationQuantity2(), r.getCalibrationQuantity3()))
                    .reduce(new BigDecimal("0"), NumberUtil::add).doubleValue();
            double sumPreCalibrationQuantity = balanceLibraryRecords.stream().filter(bl -> bl.getBalanceLibraryDetailId().equals(balanceLibraryDetailId)).map(r -> NumberUtil.add(r.getPreCalibrationQuantity(), r.getPreCalibrationQuantity2(), r.getPreCalibrationQuantity3()))
                    .reduce(new BigDecimal("0"), NumberUtil::add).doubleValue();
            double sumCalibration = NumberUtil.add(sumCalibrationQuantity, sumPreCalibrationQuantity);
            if (sumCalibration > balanceLibraryDetail.getApprovedQuantity()) {
                throw new BizException(Result.failure("预调拨总数超出限制"));
            }
            List<BalanceLibraryRecord> recordList;
            if (balanceLibraryRecordMap.containsKey(balanceLibraryRecord.getOutWarehouseId())) {
                recordList = balanceLibraryRecordMap.get(balanceLibraryRecord.getOutWarehouseId());
            } else {
                recordList = new ArrayList<>();
                balanceLibraryRecordMap.put(balanceLibraryRecord.getOutWarehouseId(), recordList);
            }
            if (null == balanceLibraryRecord.getConsignor()) {
                if (null == balanceLibraryRecord.getIsOwn()) {
                    throw new BizException(Result.failure(400, "isOwn 不可为空"));
                }
                if (balanceLibraryRecord.getIsOwn() == 1) {
                    balanceLibraryRecord.setConsignor(0);
                } else {
                    String warehouseId = balanceLibraryRecord.getOutWarehouseId();
                    Variable variable = variableMapper.selectOne(Wrappers.<Variable>lambdaQuery()
                            .eq(Variable::getKey, "consignor")
                            .eq(Variable::getParentValue, warehouseId).last("limit 1"));
                    if (null != variable) {
                        balanceLibraryRecord.setConsignor(Convert.toInt(variable.getValue()));
                    }
                }
            }
            recordList.add(balanceLibraryRecord);
            // 创建调拨计划
            AddAllocationPlanAndDetailsDTO addAllocationPlanAndDetailsDTO;
            AddAllocationPlanDTO addAllocationPlanDTO;
            List<AddAllocationPlanDetailDTO> addAllocationPlanDetailDTOList;
            if (addAllocationPlanAndDetailsDTOMap.containsKey(balanceLibraryRecord.getOutWarehouseId())) {
                // 已创建数据 取出原来的
                addAllocationPlanAndDetailsDTO = addAllocationPlanAndDetailsDTOMap.get(balanceLibraryRecord.getOutWarehouseId());
                addAllocationPlanDTO = addAllocationPlanAndDetailsDTO.getAddAllocationPlanDTO();
                addAllocationPlanDetailDTOList = addAllocationPlanAndDetailsDTO.getAddAllocationPlanDetailDTOList();
            } else {
                // 首次新建
                addAllocationPlanAndDetailsDTO = new AddAllocationPlanAndDetailsDTO();
                addAllocationPlanAndDetailsDTOMap.put(balanceLibraryRecord.getOutWarehouseId(), addAllocationPlanAndDetailsDTO);
                addAllocationPlanDetailDTOList = new ArrayList<>();
                // 创建调拨计划
                addAllocationPlanDTO = new AddAllocationPlanDTO();
                addAllocationPlanDTO.setPlanClassification(balanceLibrary.getPlanClassification());
                addAllocationPlanDTO.setApplicant(loginUser.getLoginName());
                addAllocationPlanDTO.setPlanStatus(1);
                addAllocationPlanDTO.setBusinessType(1);
//            addAllocationPlanDTO.setReceiveUser();
                addAllocationPlanDTO.setReceiveWarehouse(balanceLibraryDetail.getWarehouseId());
//            addAllocationPlanDTO.setSendUser();
                addAllocationPlanDTO.setSendWarehouse(balanceLibraryRecord.getOutWarehouseId());
                addAllocationPlanDTO.setAssignmentDate(balanceLibraryDetail.getRequestArrivalTime());
                addAllocationPlanDTO.setBalanceLibraryNo(balanceLibrary.getBalanceLibraryNo());
                addAllocationPlanAndDetailsDTO.setAddAllocationPlanDTO(addAllocationPlanDTO);
                addAllocationPlanAndDetailsDTO.setAddAllocationPlanDetailDTOList(addAllocationPlanDetailDTOList);
            }
            // 预调拨数量
            double preCalibrationQuantity = 0D;
            if (null != balanceLibraryRecord.getPreCalibrationQuantity() && 0 != balanceLibraryRecord.getPreCalibrationQuantity()) {
                preCalibrationQuantity = balanceLibraryRecord.getPreCalibrationQuantity();
            } else if (null != balanceLibraryRecord.getPreCalibrationQuantity2() && 0 != balanceLibraryRecord.getPreCalibrationQuantity2()) {
                preCalibrationQuantity = balanceLibraryRecord.getPreCalibrationQuantity2();
            } else if (null != balanceLibraryRecord.getPreCalibrationQuantity3() && 0 != balanceLibraryRecord.getPreCalibrationQuantity3()) {
                preCalibrationQuantity = balanceLibraryRecord.getPreCalibrationQuantity3();
            }
            double preCalibrationQuantityTemp = preCalibrationQuantity;
            // 遍历库存记录 先入先出原则出库
            for (InventoryInformation inventoryInformation : inventoryInformations) {
                AddAllocationPlanDetailDTO addAllocationPlanDetailDTO = new AddAllocationPlanDetailDTO();
                addAllocationPlanDetailDTO.setConsignor(balanceLibraryRecord.getConsignor());
                addAllocationPlanDetailDTO.setBatch(inventoryInformation.getBatch());
                addAllocationPlanDetailDTO.setMaterialCoding(balanceLibraryDetail.getMaterialCoding());
                if (preCalibrationQuantityTemp <= inventoryInformation.getInventoryCredit()) {
                    addAllocationPlanDetailDTO.setCalibrationQuantity(preCalibrationQuantityTemp);
                    addAllocationPlanDetailDTO.setRequestQuantity(preCalibrationQuantityTemp);
                    addAllocationPlanDetailDTOList.add(addAllocationPlanDetailDTO);
                    break;
                } else {
                    preCalibrationQuantityTemp -= inventoryInformation.getInventoryCredit();
                    addAllocationPlanDetailDTO.setCalibrationQuantity(inventoryInformation.getInventoryCredit());
                    addAllocationPlanDetailDTO.setRequestQuantity(inventoryInformation.getInventoryCredit());
                    addAllocationPlanDetailDTOList.add(addAllocationPlanDetailDTO);
                }
            }

        }
        addAllocationPlanAndDetailsDTOMap.forEach((s, addAllocationPlanAndDetailsDTO) -> {
            Result r = allocationPlanService.add(addAllocationPlanAndDetailsDTO);
            if (!r.isOk()) {
                throw new BizException("平衡利库调拨计划创建失败：" + r.getMessage());
            }
            String planNo;
            planNo = ((AllocationPlan) r.getData()).getAllocationNumber();
            List<BalanceLibraryRecord> balanceLibraryRecordList = balanceLibraryRecordMap.get(s);
            if (CollectionUtil.isNotEmpty(balanceLibraryRecordList)) {
                for (BalanceLibraryRecord balanceLibraryRecord : balanceLibraryRecordList) {
                    if (null != balanceLibraryRecord.getPreCalibrationQuantity() && 0 != balanceLibraryRecord.getPreCalibrationQuantity()) {
                        balanceLibraryRecord.setPlanNo(planNo);
                        balanceLibraryRecord.setCalibrationStatus(1);
                    } else if (null != balanceLibraryRecord.getPreCalibrationQuantity2() && 0 != balanceLibraryRecord.getPreCalibrationQuantity2()) {
                        balanceLibraryRecord.setPlanNo2(planNo);
                        balanceLibraryRecord.setCalibrationStatus2(1);
                    } else if (null != balanceLibraryRecord.getPreCalibrationQuantity3() && 0 != balanceLibraryRecord.getPreCalibrationQuantity3()) {
                        balanceLibraryRecord.setPlanNo3(planNo);
                        balanceLibraryRecord.setCalibrationStatus3(1);
                    }
                }
                boolean flag = saveOrUpdateBatch(balanceLibraryRecordList);
                if (!flag) {
                    throw new BizException("平衡利库操作记录保存失败，请稍后重试");
                }
            }

        });

        return Result.success();
    }
}
