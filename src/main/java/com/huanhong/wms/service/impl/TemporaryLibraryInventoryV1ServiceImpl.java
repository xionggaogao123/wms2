package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.common.exception.BizException;
import com.huanhong.common.units.JsonUtil;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.dto.request.TemporaryLibraryInventoryRequest;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.mapper.*;
import com.huanhong.wms.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author wang
 * @date 2022/5/25 9:44
 */
@Slf4j
@Service
public class TemporaryLibraryInventoryV1ServiceImpl implements TemporaryLibraryInventoryV1Service {

    @Resource
    private IRequiremetsPlanningDetailsService requiremetsPlanningDetailsService;

    @Resource
    private ITemporaryLibraryInventoryService temporaryLibraryInventoryService;

    @Resource
    private TemporaryLibraryInventoryMapper temporaryLibraryInventoryMapper;

    @Resource
    private TemporaryLibraryInventoryDetailsMapper temporaryLibraryInventoryDetailsMapper;

    @Resource
    private WarehouseManagerMapper warehouseManager;

    @Resource
    private TemporaryEnterWarehouseMapper temporaryEnterWarehouseMapper;

    @Resource
    private ITemporaryLibraryInventoryDetailsService temporaryLibraryInventoryDetailsService;

    @Resource
    private TemporaryEnterWarehouseDetailsMapper temporaryEnterWarehouseDetailsMapper;

    @Resource
    private IRequirementsPlanningService requirementsPlanningService;

    @Resource
    private RequiremetsPlanningDetailsMapper requiremetsPlanningDetailsMapper;

    @Resource
    private TemporaryRecordMapper temporaryRecordMapper;

    @Resource
    private TemporaryRecordDetailsMapper temporaryRecordDetailsMapper;

    /**
     * 添加临时清点单子表数据和主表数据 添加临时入库数据
     *
     * @param addTemporaryLibraryInventoryAndDetailsDTO 清点单子表数据和主表数据
     * @return 返回值
     */
    @Override
    public Result addTemporaryMainAndSublistAndWarehouse(AddTemporaryLibraryInventoryAndDetailsDTO addTemporaryLibraryInventoryAndDetailsDTO) {
        //申请需求管理
        AddRequirementsPlanningDTO addRequirementsPlanningDTO = new AddRequirementsPlanningDTO();
        BeanUtil.copyProperties(addTemporaryLibraryInventoryAndDetailsDTO.getAddTemporaryLibraryInventoryDTO(),addRequirementsPlanningDTO);
        addRequirementsPlanningDTO.setPlanStatus(1);
        addRequirementsPlanningDTO.setPlanClassification(3);
        Result result = requirementsPlanningService.addRequirementsPlanning(addRequirementsPlanningDTO);
        RequirementsPlanning data = (RequirementsPlanning) result.getData();
        log.info("添加需求管理主表数据为:{}",JsonUtil.obj2String(data));
        AddTemporaryLibraryInventoryDTO addTemporaryLibraryInventoryDTO = addTemporaryLibraryInventoryAndDetailsDTO.getAddTemporaryLibraryInventoryDTO();
        addTemporaryLibraryInventoryDTO.setPlanNumber(data.getPlanNumber());
        addTemporaryLibraryInventoryDTO.setComplete(1);
        //申请需求子表数据
        List<AddTemporaryLibraryInventoryDetailsDTO> temporaryLibraryInventoryDetailsDTOList = addTemporaryLibraryInventoryAndDetailsDTO.getAddTemporaryLibraryInventoryDetailsDTOList();
        List<RequiremetsPlanningDetails> requiremetsPlanningDetails = BeanUtil.copyToList(temporaryLibraryInventoryDetailsDTOList, RequiremetsPlanningDetails.class);
        requiremetsPlanningDetails.forEach(details2->{
            details2.setPlanNumber(data.getPlanNumber());
            details2.setRequiredQuantity(1.0);
            details2.setPlannedPurchaseQuantity(1.0);
            details2.setApprovedQuantity(1.0);
            details2.setArrivalTime(LocalDateTime.now());
            details2.setUsePurpose(data.getMaterialUse());
            details2.setUsePlace("");
            requiremetsPlanningDetailsMapper.insert(details2);
        });

        //新增临时清点主表
        Result resultInventory = temporaryLibraryInventoryService.addTemporaryLibraryInventory(addTemporaryLibraryInventoryDTO);
        if (!resultInventory.isOk()) {
            return Result.failure("新增点验单失败！");
        }
        //获取新增临时清点主表数据
        TemporaryLibraryInventory temporaryLibraryInventory = (TemporaryLibraryInventory) resultInventory.getData();
        log.info("新增临时清点主表数据为:{}", JsonUtil.obj2String(temporaryLibraryInventory));
        //查询仓库对应的管理员信息
        //List<WarehouseManager> warehouseManagers = selectWarehouseManager(temporaryLibraryInventory.getWarehouseId());
        //log.info("获取管理对应的库管数据为:{}", JsonUtil.obj2String(warehouseManagers));
        //添加临时清点入库主表信息
        //TemporaryEnterWarehouse temporaryEnterWarehouse = addTemporaryEnterWarehouse(temporaryLibraryInventory, warehouseManagers);
        //添加临时入库子表数据
        //addTemporaryEnterWarehouseDetail(addTemporaryLibraryInventoryAndDetailsDTO.getAddTemporaryLibraryInventoryDetailsDTOList(), temporaryEnterWarehouse);
        //添加临时清点子表信息
        List<AddTemporaryLibraryInventoryDetailsDTO> addTemporaryLibraryInventoryDetailsDTOList = addTemporaryLibraryInventoryAndDetailsDTO.getAddTemporaryLibraryInventoryDetailsDTOList();
        addTemporaryLibraryInventoryDetailsDTOList.forEach(details -> {
            details.setDocumentNumber(temporaryLibraryInventory.getDocumentNumber());
        });
        //新增出入库流水表
        //addTemporaryRecord(data.getPlanNumber(),temporaryEnterWarehouse,addTemporaryLibraryInventoryDetailsDTOList,warehouseManagers);
        return temporaryLibraryInventoryDetailsService.addInventoryDocumentDetailsList(addTemporaryLibraryInventoryDetailsDTOList);
    }

    /**
     * 入库
     * @param planNumber 需求编号
     * @param temporaryEnterWarehouse 入库主表数据
     * @param temporaryLibraryInventoryDetails 入库子表数据
     * @param warehouseManagers 仓库管理数据
     */
    private void addTemporaryRecord(String planNumber, TemporaryEnterWarehouse temporaryEnterWarehouse, List<TemporaryLibraryInventoryDetails> temporaryLibraryInventoryDetails,List<WarehouseManager> warehouseManagers) {
        TemporaryRecord temporaryRecord = new TemporaryRecord();
        temporaryRecord.setRequirementsPlanningNumber(planNumber);
        temporaryRecord.setNumber(temporaryEnterWarehouse.getEnterNumber());
        temporaryRecord.setBatch("1");
        ArrayList<String> arrayList = new ArrayList<>();
        warehouseManagers.forEach(message -> {
            arrayList.add(message.getLoginName());
        });
        temporaryRecord.setWarehouseManager(String.valueOf(arrayList));
        temporaryRecord.setRecordType("1");
        temporaryRecord.setEnterTime(LocalDateTime.now());
        temporaryRecord.setCreateTime(LocalDateTime.now());
        //添加主表数据
        temporaryRecordMapper.insert(temporaryRecord);
        Integer relevanceId = temporaryRecord.getId();
        //添加子表数据
        temporaryLibraryInventoryDetails.forEach(details->{
            TemporaryRecordDetails temporaryRecordDetails = new TemporaryRecordDetails();
            temporaryRecordDetails.setRelevanceId(relevanceId);
            temporaryRecordDetails.setWarehouseId(details.getWarehouseId());
            temporaryRecordDetails.setMaterialCoding(details.getMaterialCoding());
            temporaryRecordDetails.setMaterialName(details.getMaterialName());
            temporaryRecordDetails.setMeasurementUnit("件");
            temporaryRecordDetails.setEnterQuantity(details.getArrivalQuantity());
            temporaryRecordDetails.setWarehouseManager(String.valueOf(arrayList));
            temporaryRecordDetails.setCreateTime(LocalDateTime.now());
            log.info("添加出入库流水子表数据为:{}",JsonUtil.obj2String(temporaryRecordDetails));
            temporaryRecordDetailsMapper.insert(temporaryRecordDetails);
        });
    }

    /**
     * 更新临时清点数据主表和子表数据 修改临时入库数据
     *
     * @param update
     * @return
     */
    @Override
    public Result updateTemporaryMainAndSublistAndWarehouse(UpdateTemporaryLibraryInventoryAndDetailsDTO update) {
        TemporaryLibraryInventory temporaryLibraryInventory = update.getTemporaryLibraryInventory();
        TemporaryLibraryInventory temporaryLibraryInventory1 = temporaryLibraryInventoryMapper.selectById(temporaryLibraryInventory.getId());
        if (temporaryLibraryInventory1 == null) {
            throw new BizException("临时清单数据不存在");
        }
        temporaryLibraryInventoryMapper.updateById(temporaryLibraryInventory);
        List<TemporaryLibraryInventoryDetails> temporaryLibraryInventoryDetails = update.getTemporaryLibraryInventoryDetails();
        temporaryLibraryInventoryDetails.forEach(details -> {
            temporaryLibraryInventoryDetailsMapper.updateById(details);
        });
        //查询仓库对应的管理员信息
        List<WarehouseManager> warehouseManagers = selectWarehouseManager(temporaryLibraryInventory.getWarehouseId());
        log.info("获取管理对应的库管数据为:{}", JsonUtil.obj2String(warehouseManagers));
        //添加临时清点入库主表信息
        TemporaryEnterWarehouse temporaryEnterWarehouse = addTemporaryEnterWarehouse(temporaryLibraryInventory, warehouseManagers);
        //添加临时入库子表数据
        List<TemporaryEnterWarehouseDetails> temporaryEnterWarehouseDetails = addSublist(update.getTemporaryLibraryInventoryDetails(),temporaryEnterWarehouse);
        //添加入库记录表主表
        addTemporaryRecord(temporaryLibraryInventory.getPlanNumber(),temporaryEnterWarehouse,temporaryLibraryInventoryDetails,warehouseManagers);
        //        //修改临时入库表
//        String enterNumber = updateTemporaryEnterWarehouse(temporaryLibraryInventory);
//        List<TemporaryLibraryInventoryDetails> temporaryLibraryInventoryDetails = update.getTemporaryLibraryInventoryDetails();
//        //修改临时入库子表数据
//        updateTemporaryEnterWarehouseDetails(temporaryLibraryInventoryDetails, enterNumber);
//        //修改临时清单子表数据
//        temporaryLibraryInventoryDetails.forEach(details -> {
//            temporaryLibraryInventoryDetailsMapper.updateById(details);
//        });
        return Result.success("修改成功");
    }

    private List<TemporaryEnterWarehouseDetails> addSublist(List<TemporaryLibraryInventoryDetails> temporaryLibraryInventoryDetails, TemporaryEnterWarehouse temporaryEnterWarehouse) {
        List<TemporaryEnterWarehouseDetails> temporaryEnterWarehouseDetails = BeanUtil.copyToList(temporaryLibraryInventoryDetails, TemporaryEnterWarehouseDetails.class);
        temporaryEnterWarehouseDetails.forEach(details -> {
            details.setEnterNumber(temporaryEnterWarehouse.getEnterNumber());
            temporaryEnterWarehouseDetailsMapper.insert(details);
        });
        return temporaryEnterWarehouseDetails;
    }


    @Override
    public Result selectById(Long id) {
        //查询主表数据
        TemporaryLibraryInventory temporaryLibraryInventory = temporaryLibraryInventoryMapper.selectById(id);
        if(temporaryLibraryInventory == null){
            throw new BizException("清点单不存在");
        }
        QueryWrapper<TemporaryLibraryInventoryDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number",temporaryLibraryInventory.getDocumentNumber());
        List<TemporaryLibraryInventoryDetails> temporaryLibraryInventoryDetails = temporaryLibraryInventoryDetailsMapper.selectList(queryWrapper);
        //封装数据
        TemporaryLibraryInventoryRequest temporaryLibraryInventoryRequest = new TemporaryLibraryInventoryRequest();
        temporaryLibraryInventoryRequest.setTemporaryLibraryInventory(temporaryLibraryInventory);
        temporaryLibraryInventoryRequest.setTemporaryLibraryInventoryDetails(temporaryLibraryInventoryDetails);
        return Result.success(temporaryLibraryInventoryRequest);
    }

    private void updateTemporaryEnterWarehouseDetails(List<TemporaryLibraryInventoryDetails> temporaryLibraryInventoryDetails, String enterNumber) {
        QueryWrapper<TemporaryEnterWarehouseDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enter_number", enterNumber);
        List<TemporaryEnterWarehouseDetails> temporaryEnterWarehouseDetails = temporaryEnterWarehouseDetailsMapper.selectList(queryWrapper);
        temporaryEnterWarehouseDetails.forEach(details1 -> {
            temporaryLibraryInventoryDetails.forEach(details2 -> {
                details1.setMaterialCoding(details2.getMaterialCoding());
                details1.setReceivableQuantity(details2.getReceivableQuantity());
                details1.setBatch(details2.getBatch());
                details1.setWarehouseId(details2.getWarehouseId());
                details1.setComplete(details2.getComplete());
                details1.setRemark(details2.getRemark());
                details1.setCreateTime(details2.getCreateTime());
                details1.setLastUpdate(details2.getLastUpdate());
                temporaryEnterWarehouseDetailsMapper.updateById(details1);
            });
        });
    }

    /**
     * 修改临时入库主表信息
     *
     * @param temporaryLibraryInventory 临时清点数据
     */
    private String updateTemporaryEnterWarehouse(TemporaryLibraryInventory temporaryLibraryInventory) {
        QueryWrapper<TemporaryEnterWarehouse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number", temporaryLibraryInventory.getDocumentNumber());
        TemporaryEnterWarehouse temporaryEnterWarehouse = temporaryEnterWarehouseMapper.selectOne(queryWrapper);
        temporaryEnterWarehouse.setWarehouseId(temporaryLibraryInventory.getWarehouseId());
        temporaryEnterWarehouse.setEnterTime(temporaryLibraryInventory.getCreateTime());
        temporaryEnterWarehouse.setRemark(temporaryLibraryInventory.getRemark());
        temporaryEnterWarehouseMapper.updateById(temporaryEnterWarehouse);
        return temporaryEnterWarehouse.getEnterNumber();
    }

    /**
     * 添加临时入库子表数据
     *
     * @param addTemporaryLibraryInventoryDetailsDTOList 子表数据
     * @param temporaryEnterWarehouse                    主表数据
     */
    private void addTemporaryEnterWarehouseDetail(List<AddTemporaryLibraryInventoryDetailsDTO> addTemporaryLibraryInventoryDetailsDTOList, TemporaryEnterWarehouse temporaryEnterWarehouse) {
        List<TemporaryEnterWarehouseDetails> temporaryEnterWarehouseDetails = BeanUtil.copyToList(addTemporaryLibraryInventoryDetailsDTOList, TemporaryEnterWarehouseDetails.class);
        log.info("添加临时子表数据为:{}", JsonUtil.obj2String(temporaryEnterWarehouseDetails));
        temporaryEnterWarehouseDetails.forEach(details -> {
            details.setEnterNumber(temporaryEnterWarehouse.getEnterNumber());
            temporaryEnterWarehouseDetailsMapper.insert(details);
        });
    }

    /**
     * 添加临时入库主表数据并返回
     *
     * @param temporaryLibraryInventory 临时清点主表数据
     * @param warehouseManagers         仓库管理信息
     * @return 返回数据
     */
    private TemporaryEnterWarehouse addTemporaryEnterWarehouse(TemporaryLibraryInventory temporaryLibraryInventory, List<WarehouseManager> warehouseManagers) {
        TemporaryEnterWarehouse temporaryEnterWarehouse = new TemporaryEnterWarehouse();
        BeanUtil.copyProperties(temporaryLibraryInventory, temporaryEnterWarehouse);
        temporaryEnterWarehouse.setId(null);
        //遍历赋值
        ArrayList<String> arrayList = new ArrayList<>();
        warehouseManagers.forEach(message -> {
            arrayList.add(message.getLoginName());
        });
        temporaryEnterWarehouse.setWarehouse(String.valueOf(arrayList));
        temporaryEnterWarehouse.setEnterNumber("LKRK" + String.valueOf(System.currentTimeMillis()));
        temporaryEnterWarehouse.setEnterTime(temporaryLibraryInventory.getLastUpdate());
        log.info("添加临时入库主表数据为:{}", JsonUtil.obj2String(temporaryEnterWarehouse));
        temporaryEnterWarehouseMapper.insert(temporaryEnterWarehouse);
        QueryWrapper<TemporaryEnterWarehouse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number", temporaryLibraryInventory.getDocumentNumber());
        return temporaryEnterWarehouseMapper.selectOne(queryWrapper);
    }

    /**
     * 查询仓库对应的管理员信息
     *
     * @param warehouseId 仓库id
     * @return 对应管理员数据
     */
    private List<WarehouseManager> selectWarehouseManager(String warehouseId) {
        QueryWrapper<WarehouseManager> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("warehouse_id", warehouseId);
        return warehouseManager.selectList(queryWrapper);
    }
}
