package com.huanhong.wms.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huanhong.common.units.user.CurrentUserUtil;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.BalanceLibrary;
import com.huanhong.wms.entity.BalanceLibraryDetail;
import com.huanhong.wms.entity.BalanceLibraryRecord;
import com.huanhong.wms.entity.ProcurementPlan;
import com.huanhong.wms.entity.dto.AddProcurementPlanAndDetailsDTO;
import com.huanhong.wms.entity.dto.AddProcurementPlanDTO;
import com.huanhong.wms.entity.dto.AddProcurementPlanDetailsDTO;
import com.huanhong.wms.mapper.BalanceLibraryDetailMapper;
import com.huanhong.wms.mapper.BalanceLibraryMapper;
import com.huanhong.wms.mapper.BalanceLibraryRecordMapper;
import com.huanhong.wms.service.IBalanceLibraryDetailService;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.service.IProcurementPlanService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 平衡利库明细 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-24
 */
@Service
public class BalanceLibraryDetailServiceImpl extends SuperServiceImpl<BalanceLibraryDetailMapper, BalanceLibraryDetail> implements IBalanceLibraryDetailService {

    @Resource
    private BalanceLibraryRecordMapper balanceLibraryRecordMapper;
    @Resource
    private BalanceLibraryMapper balanceLibraryMapper;
    @Resource
    private IProcurementPlanService procurementPlanService;
    @Override
    public Result createProcurementPlan(BalanceLibraryDetail detail) {
        Integer balanceLibraryDetailId = detail.getId();
        if (null == balanceLibraryDetailId) {
            return Result.failure(400,"id不可为空");
        }
        BalanceLibraryDetail balanceLibraryDetail = this.baseMapper.selectById(balanceLibraryDetailId);
        if (null == detail) {
            return Result.failure(400, "平衡利库明细不存在或已被删除");
        }
        BalanceLibrary balanceLibrary = balanceLibraryMapper.selectOne(Wrappers.<BalanceLibrary>lambdaQuery().eq(BalanceLibrary::getBalanceLibraryNo, detail.getBalanceLibraryNo()).last("limit 1"));
        if (null == balanceLibrary) {
            return Result.failure(400, "平衡利库不存在或已被删除");
        }
        // 查询该明细下是否有待审批的调拨，如果有的话不可以再次调拨，需审批通过才可以
        int count = this.balanceLibraryRecordMapper.selectCount(Wrappers.lambdaQuery(new BalanceLibraryRecord())
                .ne(BalanceLibraryRecord::getCalibrationStatus, 3).or()
                .ne(BalanceLibraryRecord::getCalibrationStatus2, 3).or()
                .ne(BalanceLibraryRecord::getCalibrationStatus3, 3));
        if (count > 0) {
            return Result.failure("存在不是审批生效的调拨计划，请等待审批通过后再操作");
        }
        List<BalanceLibraryRecord> libraryRecords = this.balanceLibraryRecordMapper.selectList(Wrappers.lambdaQuery(new BalanceLibraryRecord())
                .eq(BalanceLibraryRecord::getBalanceLibraryDetailId, balanceLibraryDetailId));
        double sumCalibrationQuantity = libraryRecords.stream().map(r -> NumberUtil.add(r.getCalibrationQuantity(), r.getCalibrationQuantity2(), r.getCalibrationQuantity3()))
                .reduce(new BigDecimal("0"), NumberUtil::add).doubleValue();
        //待采购数量
        double purchasedQuantity = NumberUtil.sub(balanceLibraryDetail.getApprovedQuantity().doubleValue(),sumCalibrationQuantity);
        // 创建采购计划
        AddProcurementPlanAndDetailsDTO addProcurementPlanAndDetailsDTO = new AddProcurementPlanAndDetailsDTO();
        AddProcurementPlanDTO addProcurementPlanDTO = new AddProcurementPlanDTO();
        addProcurementPlanDTO.setPlanClassification(balanceLibrary.getPlanClassification());
        LoginUser loginUser = CurrentUserUtil.getCurrentUser();
        addProcurementPlanDTO.setPlanner(loginUser.getLoginName());
        addProcurementPlanDTO.setWarehouseId(balanceLibraryDetail.getWarehouseId());
        addProcurementPlanDTO.setMaterialUse(balanceLibrary.getMaterialUse());
        addProcurementPlanDTO.setStatus(1);
        addProcurementPlanDTO.setDemandDepartment(balanceLibrary.getDemandDepartment());
        addProcurementPlanDTO.setPlanningDepartment(balanceLibrary.getPlanningDepartment());
        addProcurementPlanDTO.setOriginalDocumentNumber(balanceLibrary.getBalanceLibraryNo());
        addProcurementPlanDTO.setBalanceLibraryDetailId(balanceLibraryDetailId);
        addProcurementPlanDTO.setBalanceLibraryNo(balanceLibrary.getBalanceLibraryNo());
        addProcurementPlanAndDetailsDTO.setAddProcurementPlanDTO(addProcurementPlanDTO);
        List<AddProcurementPlanDetailsDTO> addProcurementPlanDetailsDTOList = new ArrayList<>();
        AddProcurementPlanDetailsDTO addProcurementPlanDetailsDTO = new AddProcurementPlanDetailsDTO();
        addProcurementPlanDetailsDTO.setMaterialName(balanceLibraryDetail.getMaterialName());
        addProcurementPlanDetailsDTO.setMaterialId(balanceLibraryDetail.getMaterialId());
        addProcurementPlanDetailsDTO.setMaterialCoding(balanceLibraryDetail.getMaterialCoding());
        addProcurementPlanDetailsDTO.setWarehouseId(balanceLibraryDetail.getWarehouseId());
        addProcurementPlanDetailsDTO.setRequestArrivalTime(balanceLibraryDetail.getRequestArrivalTime());
//        addProcurementPlanDetailsDTO.setApprovedQuantity();
        addProcurementPlanDetailsDTO.setInventory(balanceLibraryDetail.getInventory());
        addProcurementPlanDetailsDTO.setEstimatedAmount(balanceLibraryDetail.getEstimatedAmount());
        addProcurementPlanDetailsDTO.setPlannedPurchaseQuantity(purchasedQuantity);
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
        String planNo = ((ProcurementPlan) r.getData()).getPlanNumber();
        BalanceLibraryDetail temp = new BalanceLibraryDetail();
        temp.setProcurementNo(planNo);
        temp.setPurchasedQuantity(purchasedQuantity);
        temp.setId(detail.getId());
        this.baseMapper.updateById(temp);
        return null;
    }
}
