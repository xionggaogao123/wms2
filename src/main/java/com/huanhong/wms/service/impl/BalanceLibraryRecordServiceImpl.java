package com.huanhong.wms.service.impl;

import cn.hutool.core.collection.CollectionUtil;
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
import com.huanhong.wms.mapper.BalanceLibraryDetailMapper;
import com.huanhong.wms.mapper.BalanceLibraryMapper;
import com.huanhong.wms.mapper.BalanceLibraryRecordMapper;
import com.huanhong.wms.mapper.InventoryInformationMapper;
import com.huanhong.wms.service.IAllocationPlanService;
import com.huanhong.wms.service.IBalanceLibraryRecordService;
import com.huanhong.wms.service.IProcurementPlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result add(List<BalanceLibraryRecord> balanceLibraryRecords) {
        if (CollectionUtil.isEmpty(balanceLibraryRecords)) {
            return Result.failure(400, "参数不可为空");
        }
        Integer balanceLibraryDetailId = balanceLibraryRecords.get(0).getBalanceLibraryDetailId();
        if (null == balanceLibraryDetailId) {
            return Result.failure(400, "平衡利库明细ID不可为空");
        }
        BalanceLibraryDetail balanceLibraryDetail = balanceLibraryDetailMapper.selectById(balanceLibraryDetailId);
        if (null == balanceLibraryDetail) {
            return Result.failure(400, "平衡利库明细不存在或已被删除");
        }
        BalanceLibrary balanceLibrary = balanceLibraryMapper.selectOne(Wrappers.<BalanceLibrary>lambdaQuery()
                .eq(BalanceLibrary::getBalanceLibraryNo, balanceLibraryDetail.getBalanceLibraryNo())
                .orderByDesc(BalanceLibrary::getId).last("limit 1"));
        if (null == balanceLibrary) {
            return Result.failure(400, "平衡利库不存在或已被删除");
        }
        // 查询该明细下是否有待审批的调拨，如果有的话不可以再次调拨，需审批通过才可以
        int count = this.baseMapper.selectCount(Wrappers.lambdaQuery(new BalanceLibraryRecord())
                .eq(BalanceLibraryRecord::getBalanceLibraryDetailId, balanceLibraryDetailId).and(wrapper -> wrapper.ne(BalanceLibraryRecord::getCalibrationStatus, 3).or()
                        .ne(BalanceLibraryRecord::getCalibrationStatus2, 3).or()
                        .ne(BalanceLibraryRecord::getCalibrationStatus3, 3))
        );
        if (count > 0) {
            return Result.failure("存在不是审批生效的调拨计划，请等待审批通过后再操作");
        }
        // 判断数量是否符合条件
        List<BalanceLibraryRecord> libraryRecords = this.baseMapper.selectList(Wrappers.lambdaQuery(new BalanceLibraryRecord())
                .eq(BalanceLibraryRecord::getBalanceLibraryDetailId, balanceLibraryDetailId));
        double sumCalibrationQuantity = libraryRecords.stream().map(r -> NumberUtil.add(r.getCalibrationQuantity(), r.getCalibrationQuantity2(), r.getCalibrationQuantity3()))
                .reduce(new BigDecimal("0"), NumberUtil::add).doubleValue();
        double sumPreCalibrationQuantity = balanceLibraryRecords.stream().map(r -> NumberUtil.add(r.getPreCalibrationQuantity(), r.getPreCalibrationQuantity2(), r.getPreCalibrationQuantity3()))
                .reduce(new BigDecimal("0"), NumberUtil::add).doubleValue();
        double sumCalibration = NumberUtil.add(sumCalibrationQuantity, sumPreCalibrationQuantity);
        if (sumCalibration > balanceLibraryDetail.getApprovedQuantity()) {
            return Result.failure("预调拨总数超出限制");
        }
        LoginUser loginUser = CurrentUserUtil.getCurrentUser();
        String planNo = null;
        for (BalanceLibraryRecord balanceLibraryRecord : balanceLibraryRecords) {
            // 创建调拨计划
            AddAllocationPlanAndDetailsDTO addAllocationPlanAndDetailsDTO = new AddAllocationPlanAndDetailsDTO();
            AddAllocationPlanDTO addAllocationPlanDTO = new AddAllocationPlanDTO();
            addAllocationPlanDTO.setPlanClassification(balanceLibrary.getPlanClassification());
            addAllocationPlanDTO.setApplicant(loginUser.getLoginName());
            addAllocationPlanDTO.setPlanStatus(1);
            addAllocationPlanDTO.setBusinessType(1);
//            addAllocationPlanDTO.setReceiveUser();
            addAllocationPlanDTO.setReceiveWarehouse(balanceLibraryDetail.getWarehouseId());
//            addAllocationPlanDTO.setSendUser();
            addAllocationPlanDTO.setSendWarehouse(balanceLibraryRecord.getOutWarehouseId());
            addAllocationPlanDTO.setAssignmentDate(balanceLibraryDetail.getRequestArrivalTime());
            double preCalibrationQuantity = 0D;
            if (null != balanceLibraryRecord.getPreCalibrationQuantity() && 0 != balanceLibraryRecord.getPreCalibrationQuantity()) {
                addAllocationPlanDTO.setBalanceLibraryRecordNum(1);
                preCalibrationQuantity = balanceLibraryRecord.getPreCalibrationQuantity();
            } else if (null != balanceLibraryRecord.getPreCalibrationQuantity2() && 0 != balanceLibraryRecord.getPreCalibrationQuantity2()) {
                addAllocationPlanDTO.setBalanceLibraryRecordNum(2);
                preCalibrationQuantity = balanceLibraryRecord.getPreCalibrationQuantity2();
            } else if (null != balanceLibraryRecord.getPreCalibrationQuantity3() && 0 != balanceLibraryRecord.getPreCalibrationQuantity3()) {
                addAllocationPlanDTO.setBalanceLibraryRecordNum(3);
                preCalibrationQuantity = balanceLibraryRecord.getPreCalibrationQuantity3();
            }
            addAllocationPlanDTO.setBalanceLibraryNo(balanceLibrary.getBalanceLibraryNo());
            addAllocationPlanDTO.setBalanceLibraryDetailId(balanceLibraryDetailId);
            addAllocationPlanAndDetailsDTO.setAddAllocationPlanDTO(addAllocationPlanDTO);
            // 查询该库区下的该物料的批次信息
            List<InventoryInformation> inventoryInformations = inventoryInformationMapper.selectList(Wrappers.<InventoryInformation>lambdaQuery().eq(InventoryInformation::getMaterialCoding, balanceLibraryDetail.getMaterialCoding())
                    .likeRight(InventoryInformation::getCargoSpaceId, balanceLibraryRecord.getOutWarehouseId()).gt(InventoryInformation::getInventoryCredit, 0));
            if (CollectionUtil.isEmpty(inventoryInformations)) {
                throw new BizException(Result.failure(1001, StrUtil.format("仓库：{},物料：{},库存不足", balanceLibraryRecord.getOutWarehouse(), balanceLibraryDetail.getMaterialCoding())));
            }
            List<AddAllocationPlanDetailDTO> addAllocationPlanDetailDTOList = new ArrayList<>();
            double preCalibrationQuantityTemp = preCalibrationQuantity;
            for (InventoryInformation inventoryInformation : inventoryInformations) {
                AddAllocationPlanDetailDTO addAllocationPlanDetailDTO = new AddAllocationPlanDetailDTO();
                addAllocationPlanDetailDTO.setConsignor(0);
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
            addAllocationPlanAndDetailsDTO.setAddAllocationPlanDetailDTOList(addAllocationPlanDetailDTOList);
            Result r = allocationPlanService.add(addAllocationPlanAndDetailsDTO);
            if (!r.isOk()) {
                throw new BizException("平衡利库调拨计划创建失败：" + r.getMessage());
            }
            planNo = ((AllocationPlan) r.getData()).getAllocationNumber();
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
            boolean flag = saveOrUpdate(balanceLibraryRecord);
            if (!flag) {
                throw new BizException("平衡利库操作记录保存失败，请稍后重试");
            }
            AllocationPlan allocationPlan = (AllocationPlan) r.getData();
            AllocationPlan temp = new AllocationPlan();
            temp.setId(allocationPlan.getId());
            temp.setBalanceLibraryRecordId(balanceLibraryRecord.getId());
            allocationPlanService.updateById(temp);
        }
        return Result.success();
    }
}
