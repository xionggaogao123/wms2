package com.huanhong.wms.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huanhong.common.units.user.CurrentUserUtil;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.mapper.BalanceLibraryDetailMapper;
import com.huanhong.wms.mapper.BalanceLibraryMapper;
import com.huanhong.wms.mapper.BalanceLibraryRecordMapper;
import com.huanhong.wms.service.IAllocationPlanService;
import com.huanhong.wms.service.IBalanceLibraryRecordService;
import com.huanhong.wms.service.IProcurementPlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result add(BalanceLibraryRecord balanceLibraryRecord) {
        if (null == balanceLibraryRecord.getBalanceLibraryDetailId()) {
            return Result.failure(400, "平衡利库明细有误");
        }
        BalanceLibraryDetail balanceLibraryDetail = balanceLibraryDetailMapper.selectById(balanceLibraryRecord.getBalanceLibraryDetailId());
        if (null == balanceLibraryDetail) {
            return Result.failure(400, "平衡利库明细不存在或已被删除");
        }
        BalanceLibrary balanceLibrary = balanceLibraryMapper.selectOne(Wrappers.<BalanceLibrary>lambdaQuery().eq(BalanceLibrary::getBalanceLibraryNo, balanceLibraryDetail.getBalanceLibraryNo()).last("limit 1"));
        if (null == balanceLibrary) {
            return Result.failure(400, "平衡利库不存在或已被删除");
        }
        LoginUser loginUser = CurrentUserUtil.getCurrentUser();
        String planNo = null;
        if (balanceLibraryRecord.getBalanceType() == 1) {
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
            addAllocationPlanAndDetailsDTO.setAddAllocationPlanDTO(addAllocationPlanDTO);

            List<AddAllocationPlanDetailDTO> addAllocationPlanDetailDTOList = new ArrayList<>();
            AddAllocationPlanDetailDTO addAllocationPlanDetailDTO = new AddAllocationPlanDetailDTO();
            addAllocationPlanDetailDTO.setConsignor(0);
            addAllocationPlanDetailDTO.setMaterialCoding(balanceLibraryDetail.getMaterialCoding());
            addAllocationPlanDetailDTO.setCalibrationQuantity(balanceLibraryRecord.getCalibrationQuantity());
            addAllocationPlanDetailDTO.setRequestQuantity(balanceLibraryRecord.getCalibrationQuantity());
            addAllocationPlanDetailDTOList.add(addAllocationPlanDetailDTO);
            addAllocationPlanAndDetailsDTO.setAddAllocationPlanDetailDTOList(addAllocationPlanDetailDTOList);
            Result r = allocationPlanService.add(addAllocationPlanAndDetailsDTO);
            if (!r.isOk()) {
                return r;
            }
            planNo = ((AllocationPlan) r.getData()).getAllocationNumber();
        } else {
            // 创建采购计划
            AddProcurementPlanAndDetailsDTO addProcurementPlanAndDetailsDTO = new AddProcurementPlanAndDetailsDTO();
            AddProcurementPlanDTO addProcurementPlanDTO = new AddProcurementPlanDTO();
            addProcurementPlanDTO.setPlanClassification(balanceLibrary.getPlanClassification());
            addProcurementPlanDTO.setPlanner(loginUser.getLoginName());
            addProcurementPlanDTO.setWarehouseId(balanceLibraryDetail.getWarehouseId());
            addProcurementPlanDTO.setMaterialUse(balanceLibrary.getMaterialUse());
            addProcurementPlanDTO.setStatus(1);
            addProcurementPlanDTO.setDemandDepartment(balanceLibrary.getDemandDepartment());
            addProcurementPlanDTO.setPlanningDepartment(balanceLibrary.getPlanningDepartment());
            addProcurementPlanDTO.setOriginalDocumentNumber(balanceLibrary.getBalanceLibraryNo());

            addProcurementPlanAndDetailsDTO.setAddProcurementPlanDTO(addProcurementPlanDTO);
            List<AddProcurementPlanDetailsDTO> addProcurementPlanDetailsDTOList = new ArrayList<>();
            AddProcurementPlanDetailsDTO addProcurementPlanDetailsDTO = new AddProcurementPlanDetailsDTO();
            addProcurementPlanDetailsDTO.setMaterialName(balanceLibraryDetail.getMaterialName());
            addProcurementPlanDetailsDTO.setMaterialId(balanceLibraryDetail.getMaterialId());
            addProcurementPlanDetailsDTO.setMaterialCoding(balanceLibraryDetail.getMaterialCoding());
            addProcurementPlanDetailsDTO.setWarehouseId(balanceLibraryDetail.getWarehouseId());
            addProcurementPlanDetailsDTO.setRequestArrivalTime(balanceLibraryDetail.getRequestArrivalTime());
            addProcurementPlanDetailsDTO.setApprovedQuantity(balanceLibraryRecord.getCalibrationQuantity());
            addProcurementPlanDetailsDTO.setInventory(balanceLibraryDetail.getInventory());
            addProcurementPlanDetailsDTO.setEstimatedAmount(balanceLibraryDetail.getEstimatedAmount());
            addProcurementPlanDetailsDTO.setPlannedPurchaseQuantity(balanceLibraryRecord.getCalibrationQuantity());
            addProcurementPlanDetailsDTO.setEstimatedUnitPrice(balanceLibraryDetail.getEstimatedUnitPrice());
            addProcurementPlanDetailsDTO.setSafetyStock(balanceLibraryDetail.getSafetyStock());
            addProcurementPlanDetailsDTO.setUsePlace(balanceLibraryDetail.getUsePlace());
            addProcurementPlanDetailsDTO.setUsePurpose(balanceLibraryDetail.getUsePurpose());
            addProcurementPlanDetailsDTOList.add(addProcurementPlanDetailsDTO);
            addProcurementPlanAndDetailsDTO.setAddProcurementPlanDetailsDTOList(addProcurementPlanDetailsDTOList);
            Result r = procurementPlanService.add(addProcurementPlanAndDetailsDTO);
            if (!r.isOk()) {
                return r;
            }
            planNo = ((ProcurementPlan) r.getData()).getPlanNumber();

        }
        balanceLibraryRecord.setPlanNo(planNo);
        int flag = this.baseMapper.insert(balanceLibraryRecord);
        if (flag < 1) {
            throw new RuntimeException("平衡利库操作记录保存失败，请稍后重试");
        }
        return Result.success();
    }
}
