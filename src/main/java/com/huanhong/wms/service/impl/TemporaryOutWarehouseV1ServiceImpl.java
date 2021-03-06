package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.exception.BizException;
import com.huanhong.common.units.JsonUtil;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.dto.request.TemporaryOutWarehouseDetailsRequest;
import com.huanhong.wms.dto.request.TemporaryOutWarehouseRequest;
import com.huanhong.wms.dto.request.TemporaryOutWarehouseV1AddRequest;
import com.huanhong.wms.dto.request.UpdateTemporaryOutWarehouseV1AddRequest;
import com.huanhong.wms.dto.response.TemporaryOutWarehouseResponse;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.AddRequirementsPlanningDTO;
import com.huanhong.wms.entity.vo.TemporaryOutWarehouseVO;
import com.huanhong.wms.mapper.*;
import com.huanhong.wms.service.IRequirementsPlanningService;
import com.huanhong.wms.service.TemporaryOutWarehouseV1Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @Author wang
 * @date 2022/5/25 17:01
 */
@Slf4j
@Service
public class TemporaryOutWarehouseV1ServiceImpl implements TemporaryOutWarehouseV1Service {

    @Resource
    private TemporaryOutWarehouseMapper warehouseManager;

    @Resource
    private TemporaryOutWarehouseDetailsMapper warehouseDetailsManager;

    @Resource
    private TemporaryEnterWarehouseDetailsMapper temporaryEnterWarehouseDetailsMapper;

    @Resource
    private TemporaryEnterWarehouseMapper temporaryEnterWarehouseMapper;

    @Resource
    private IRequirementsPlanningService requirementsPlanningService;

    @Resource
    private RequiremetsPlanningDetailsMapper requirementsPlanningDetailsService;

    @Resource
    private TemporaryRecordMapper temporaryRecordMapper;

    @Resource
    private TemporaryRecordDetailsMapper temporaryRecordDetailsMapper;

    /**
     * ???????????????????????????????????????
     *
     * @param request ??????????????????
     * @return
     */
    @Override
    public Result addMasterAndSublist(TemporaryOutWarehouseV1AddRequest request) {
        //????????????
        checkout(request);
        //??????????????????
        String planNumber = addRequirementsPlanning(request);
        //?????????????????????????????????????????????
        String outNumber = addWarehouse(request, planNumber);
        //??????????????????
        addWarehouseDetails(request.getTemporaryOutWarehouseDetailsRequest(), outNumber);
        //?????????????????????
        deductingInventory(request.getTemporaryOutWarehouseDetailsRequest(), outNumber);
        //????????????
        addTemporaryRecord(planNumber, outNumber, request);
        return Result.success("??????????????????????????????");
    }

    private void checkout(TemporaryOutWarehouseV1AddRequest request) {
        List<TemporaryOutWarehouseDetailsRequest> temporaryOutWarehouseDetailsRequest = request.getTemporaryOutWarehouseDetailsRequest();
        temporaryOutWarehouseDetailsRequest.forEach(details -> {
            QueryWrapper<TemporaryEnterWarehouseDetails> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("material_coding", details.getMaterialCoding());
            queryWrapper.eq("batch", details.getBatch());
            queryWrapper.eq("warehouse_id", details.getWarehouseId());
            List<TemporaryEnterWarehouseDetails> temporaryEnterWarehouseDetails = temporaryEnterWarehouseDetailsMapper.selectList(queryWrapper);
            temporaryEnterWarehouseDetails.forEach(details1 -> {
                Double receivableQuantity = details1.getReceivableQuantity();
                if (details.getRequisitionQuantity() > receivableQuantity) {
                    throw new SecurityException("????????????????????????????????????");
                }
            });
        });
    }

    private void addTemporaryRecord(String planNumber, String outNumber, TemporaryOutWarehouseV1AddRequest request) {
        QueryWrapper<TemporaryOutWarehouse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_number", outNumber);
        TemporaryOutWarehouse temporaryOutWarehouse = warehouseManager.selectOne(queryWrapper);
        TemporaryRecord temporaryRecord = new TemporaryRecord();
        temporaryRecord.setRequirementsPlanningNumber(planNumber);
        temporaryRecord.setNumber(outNumber);
        temporaryRecord.setRecordType("2");
        temporaryRecord.setBatch("1");
        temporaryRecord.setWarehouseManager(temporaryOutWarehouse.getLibrarian());
        temporaryRecord.setEnterTime(LocalDateTime.now());
        temporaryRecord.setCreateTime(LocalDateTime.now());
        //??????????????????
        temporaryRecordMapper.insert(temporaryRecord);

        Integer id = temporaryRecord.getId();

        //??????????????????
        QueryWrapper<TemporaryOutWarehouseDetails> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("out_number", outNumber);
        List<TemporaryOutWarehouseDetails> temporaryOutWarehouseDetails = warehouseDetailsManager.selectList(queryWrapper2);
        temporaryOutWarehouseDetails.forEach(details -> {
            TemporaryRecordDetails temporaryRecordDetails = new TemporaryRecordDetails();
            temporaryRecordDetails.setRelevanceId(id);
            temporaryRecordDetails.setWarehouseId(details.getWarehouseId());
            temporaryRecordDetails.setMaterialCoding(details.getMaterialCoding());
            temporaryRecordDetails.setMaterialName(details.getMaterialName());
            temporaryRecordDetails.setMeasurementUnit("???");
            temporaryRecordDetails.setOutQuantity(details.getRequisitionQuantity());
            temporaryRecordDetails.setWarehouseManager(temporaryOutWarehouse.getLibrarian());
            temporaryRecordDetails.setCreateTime(LocalDateTime.now());
            log.info("????????????????????????????????????:{}", JsonUtil.obj2String(temporaryRecordDetails));
            temporaryRecordDetailsMapper.insert(temporaryRecordDetails);
        });
    }

    private String addRequirementsPlanning(TemporaryOutWarehouseV1AddRequest request) {
        TemporaryOutWarehouseRequest temporaryOutWarehouseRequest = request.getTemporaryOutWarehouseRequest();
        AddRequirementsPlanningDTO addRequirementsPlanningDTO = new AddRequirementsPlanningDTO();
        BeanUtil.copyProperties(temporaryOutWarehouseRequest, addRequirementsPlanningDTO);
        addRequirementsPlanningDTO.setPlanUnit(temporaryOutWarehouseRequest.getPlanUnit());
        addRequirementsPlanningDTO.setPlanStatus(1);
        addRequirementsPlanningDTO.setPlanClassification(3);
        Result result = requirementsPlanningService.addRequirementsPlanning(addRequirementsPlanningDTO);
        RequirementsPlanning data = (RequirementsPlanning) result.getData();
        String planNumber = data.getPlanNumber();
        //????????????????????????
        List<TemporaryOutWarehouseDetailsRequest> temporaryOutWarehouseDetailsRequest = request.getTemporaryOutWarehouseDetailsRequest();
        List<RequiremetsPlanningDetails> requiremetsPlanningDetails = BeanUtil.copyToList(temporaryOutWarehouseDetailsRequest, RequiremetsPlanningDetails.class);
        requiremetsPlanningDetails.forEach(details -> {
            details.setPlanNumber(planNumber);
            details.setRequiredQuantity(1.0);
            details.setPlannedPurchaseQuantity(1.0);
            details.setApprovedQuantity(1.0);
            details.setArrivalTime(LocalDateTime.now());
            details.setUsePurpose(data.getMaterialUse());
            details.setUsePlace("");
            requirementsPlanningDetailsService.insert(details);
        });
        return planNumber;
    }

    /**
     * ??????????????????????????????
     *
     * @param temporaryOutWarehouseDetailsRequest ????????????
     */
    private void deductingInventory(List<TemporaryOutWarehouseDetailsRequest> temporaryOutWarehouseDetailsRequest, String outNumber) {
        log.info("????????????????????????:{}", JsonUtil.obj2String(temporaryOutWarehouseDetailsRequest.toString()));
        BigDecimal number3 = new BigDecimal(0);
        //????????????????????????
        List<TemporaryOutWarehouseDetails> temporaryOutWarehouseDetails = BeanUtil.copyToList(temporaryOutWarehouseDetailsRequest, TemporaryOutWarehouseDetails.class);
        temporaryOutWarehouseDetails.forEach(details -> {
            QueryWrapper<TemporaryEnterWarehouseDetails> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("material_coding", details.getMaterialCoding());
            queryWrapper.eq("material_name", details.getMaterialName());
            //????????????????????????
            List<TemporaryEnterWarehouseDetails> enterWarehouseDetails = temporaryEnterWarehouseDetailsMapper.selectList(queryWrapper);
            //?????? ?????????????????????
            enterWarehouseDetails.forEach(warehouseDetails -> {
                BigDecimal number1 = new BigDecimal(warehouseDetails.getArrivalQuantity());
                BigDecimal number2 = new BigDecimal(details.getRequisitionQuantity());
                if (number1.compareTo(number2) < 0) {
                    throw new BizException("??????????????????????????????");
                }
                BigDecimal subtract = number1.subtract(number2);
                //???????????????==0????????????????????? ???????????????<0
                if (subtract.compareTo(number3) == 0) {
                    //????????????????????????
                    temporaryEnterWarehouseDetailsMapper.deleteById(warehouseDetails.getId());
                    //?????????????????????????????????????????? ??????????????????????????????
                    QueryWrapper<TemporaryEnterWarehouseDetails> temporaryEnterWarehouseDetailsQueryWrapper = new QueryWrapper<>();
                    temporaryEnterWarehouseDetailsQueryWrapper.eq("enter_number", warehouseDetails.getEnterNumber());
                    List<TemporaryEnterWarehouseDetails> temporaryEnterWarehouseDetails = temporaryEnterWarehouseDetailsMapper.selectList(temporaryEnterWarehouseDetailsQueryWrapper);
                    if (CollectionUtils.isEmpty(temporaryEnterWarehouseDetails)) {
                        QueryWrapper<TemporaryEnterWarehouse> temporaryEnterWarehouseQueryWrapper = new QueryWrapper<>();
                        temporaryEnterWarehouseQueryWrapper.eq("enter_number", warehouseDetails.getEnterNumber());
                        temporaryEnterWarehouseMapper.delete(temporaryEnterWarehouseQueryWrapper);
                    }
                } else if (subtract.compareTo(number3) > 0) {
                    warehouseDetails.setArrivalQuantity(subtract.doubleValue());
                    temporaryEnterWarehouseDetailsMapper.updateById(warehouseDetails);
                }
            });
        });


    }

    /**
     * ??????id????????????
     *
     * @param id id
     * @return ?????????
     */
    @Override
    public Result selectById(Long id) {
        //??????????????????????????????
        TemporaryOutWarehouse temporaryOutWarehouse = warehouseManager.selectById(id);
        if (temporaryOutWarehouse == null) {
            return Result.failure("????????????????????????");
        }
        //??????????????????????????????
        QueryWrapper<TemporaryOutWarehouseDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_number", temporaryOutWarehouse.getOutNumber());
        List<TemporaryOutWarehouseDetails> warehouseDetails = warehouseDetailsManager.selectList(queryWrapper);
        //??????????????????
        TemporaryOutWarehouseResponse response = new TemporaryOutWarehouseResponse();
        response.setWarehouseDetails(warehouseDetails);
        response.setTemporaryOutWarehouse(temporaryOutWarehouse);
        return Result.success(response);
    }

    /**
     * ??????id??????
     *
     * @param id id
     * @return ?????????
     */
    @Override
    public Result deleteById(Long id) {
        //????????????????????????????????????
        TemporaryOutWarehouse temporaryOutWarehouse = warehouseManager.selectById(id);
        if (temporaryOutWarehouse == null) {
            return Result.failure("?????????????????????");
        }
        String outNumber = temporaryOutWarehouse.getOutNumber();
        //????????????????????????
        warehouseManager.deleteById(id);
        //??????????????????
        QueryWrapper<TemporaryOutWarehouseDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_number", outNumber);
        warehouseDetailsManager.delete(queryWrapper);
        return Result.success("????????????????????????");
    }

    @Override
    public Page<TemporaryOutWarehouse> pageFuzzyQuery(Page<TemporaryOutWarehouse> objectPage, TemporaryOutWarehouseVO temporaryOutWarehouseVO) {
        QueryWrapper<TemporaryOutWarehouse> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (temporaryOutWarehouseVO == null) {
            return warehouseManager.selectPage(objectPage, queryWrapper);
        }
        //????????????
        queryWrapper.like(StringUtils.isNotBlank(temporaryOutWarehouseVO.getOutNumber()), "document_number", temporaryOutWarehouseVO.getOutNumber());
        queryWrapper.like(ObjectUtil.isNotNull(temporaryOutWarehouseVO.getStatus()), "status", temporaryOutWarehouseVO.getStatus());
        queryWrapper.like(StringUtils.isNotBlank(temporaryOutWarehouseVO.getBatch()), "batch", temporaryOutWarehouseVO.getBatch());
        queryWrapper.like(StringUtils.isNotBlank(temporaryOutWarehouseVO.getRequisitioningUnit()), "requisitioning_unit", temporaryOutWarehouseVO.getRequisitioningUnit());
        queryWrapper.eq(StringUtils.isNotBlank(temporaryOutWarehouseVO.getRecipient()), "recipient", temporaryOutWarehouseVO.getRecipient());
        queryWrapper.eq(StringUtils.isNotBlank(temporaryOutWarehouseVO.getWarehouseId()), "warehouse_id", temporaryOutWarehouseVO.getWarehouseId());
        //??????????????????
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (ObjectUtil.isNotEmpty(temporaryOutWarehouseVO.getCreateTimeStart()) && ObjectUtil.isNotEmpty(temporaryOutWarehouseVO.getCreateTimeEnd())) {
            String createDateStart = dtf1.format(temporaryOutWarehouseVO.getCreateTimeStart());
            String createDateEnd = dtf1.format(temporaryOutWarehouseVO.getCreateTimeEnd());
            queryWrapper.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");

        }

        return warehouseManager.selectPage(objectPage, queryWrapper);
    }

    @Override
    public Result selectAll() {
        List<TemporaryEnterWarehouseDetails> temporaryOutWarehouseDetails = temporaryEnterWarehouseDetailsMapper.selectList(null);
        log.info("??????????????????:{}", JsonUtil.obj2String(temporaryOutWarehouseDetails));
        return Result.success(temporaryOutWarehouseDetails);
    }

    /**
     * ??????????????????
     *
     * @param request
     * @return
     */
    @Override
    public Result updateTemporaryOutWarehouse(UpdateTemporaryOutWarehouseV1AddRequest request) {
        //?????????????????????
        TemporaryOutWarehouse temporaryOutWarehouse = request.getTemporaryOutWarehouse();
        warehouseManager.updateById(temporaryOutWarehouse);
        //??????????????????
        List<TemporaryOutWarehouseDetails> temporaryOutWarehouseDetails = request.getTemporaryOutWarehouseDetails();
        temporaryOutWarehouseDetails.forEach(details -> {
            warehouseDetailsManager.updateById(details);
        });
        //????????????????????????
        updateTemporaryEnterWarehouseDetails(temporaryOutWarehouseDetails);
        return null;
    }

    private void updateTemporaryEnterWarehouseDetails(List<TemporaryOutWarehouseDetails> temporaryOutWarehouseDetails) {
        temporaryOutWarehouseDetails.forEach(details -> {
            //???????????????????????????
            QueryWrapper<TemporaryEnterWarehouseDetails> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("out_number", details.getOutNumber());
            List<TemporaryEnterWarehouseDetails> temporaryEnterWarehouseDetails = temporaryEnterWarehouseDetailsMapper.selectList(queryWrapper);

        });
    }

    /**
     * ??????????????????????????????
     *
     * @param detailsRequest ????????????
     * @param outNumber      ??????????????????
     */
    private void addWarehouseDetails(List<TemporaryOutWarehouseDetailsRequest> detailsRequest, String outNumber) {
        //??????
        List<TemporaryOutWarehouseDetails> temporaryOutWarehouseDetails = BeanUtil.copyToList(detailsRequest, TemporaryOutWarehouseDetails.class);
        temporaryOutWarehouseDetails.forEach(details -> {
            details.setOutNumber(outNumber);
            details.setCreateTime(LocalDateTime.now());
            details.setLastUpdate(LocalDateTime.now());
            warehouseDetailsManager.insert(details);
        });
    }

    /**
     * ??????????????????????????????
     *
     * @param request ????????????
     * @return ?????????
     */
    private String addWarehouse(TemporaryOutWarehouseV1AddRequest request, String str) {
        TemporaryOutWarehouse temporaryOutWarehouse = new TemporaryOutWarehouse();
        BeanUtil.copyProperties(request.getTemporaryOutWarehouseRequest(), temporaryOutWarehouse);
        temporaryOutWarehouse.setOutNumber("LKCK" + String.valueOf(System.currentTimeMillis()));
        temporaryOutWarehouse.setLastUpdate(LocalDateTime.now());
        temporaryOutWarehouse.setPlanNumber(str);
        int insert = warehouseManager.insert(temporaryOutWarehouse);
        if (insert > 0) {
            TemporaryOutWarehouse warehouse = warehouseManager.selectById(temporaryOutWarehouse.getId());
            return warehouse.getOutNumber();
        }
        return null;
    }
}
