package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.StrUtils;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.LoginUser;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.*;
import com.huanhong.wms.entity.dto.*;
import com.huanhong.wms.entity.vo.TemporaryOutWarehouseVO;
import com.huanhong.wms.mapper.TemporaryOutWarehouseMapper;
import com.huanhong.wms.service.IMaterialService;
import com.huanhong.wms.service.ITemporaryLibraryService;
import com.huanhong.wms.service.ITemporaryOutWarehouseService;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.service.ITemporaryRecordService;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-05-05
 */
@Service
public class TemporaryOutWarehouseServiceImpl extends SuperServiceImpl<TemporaryOutWarehouseMapper, TemporaryOutWarehouse> implements ITemporaryOutWarehouseService {


    @Resource
    private TemporaryOutWarehouseMapper temporaryOutWarehouseMapper;

    @Resource
    private ITemporaryLibraryService temporaryLibraryService;

    @Resource
    private ITemporaryRecordService temporaryRecordService;

    @Resource
    private IMaterialService materialService;

    @Override
    public Page<TemporaryOutWarehouse> pageFuzzyQuery(Page<TemporaryOutWarehouse> temporaryOutWarehousePage, TemporaryOutWarehouseVO temporaryOutWarehouseVO) {

        //新建QueryWrapper对象
        QueryWrapper<TemporaryOutWarehouse> query = new QueryWrapper<>();

        //根据id排序
        query.orderByDesc("id");

        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(temporaryOutWarehouseVO)) {
            return temporaryOutWarehouseMapper.selectPage(temporaryOutWarehousePage, query);
        }

        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
//        query.like(StringUtils.isNotBlank(temporaryOutWarehouseVO.getOutNumber()), "document_number", temporaryOutWarehouseVO.getOutNumber());
//
//        query.like(ObjectUtil.isNotNull(temporaryOutWarehouseVO.getStatus()), "status", temporaryOutWarehouseVO.getStatus());
//
//
//        query.like(StringUtils.isNotBlank(temporaryOutWarehouseVO.getBatch()), "batch", temporaryOutWarehouseVO.getBatch());
//
//        query.like(StringUtils.isNotBlank(temporaryOutWarehouseVO.getRequisitioningUnit()), "requisitioning_unit", temporaryOutWarehouseVO.getRequisitioningUnit());
//
//        query.eq(StringUtils.isNotBlank(temporaryOutWarehouseVO.getRecipient()), "recipient", temporaryOutWarehouseVO.getRecipient());
//
//        query.eq(StringUtils.isNotBlank(temporaryOutWarehouseVO.getWarehouseId()),"warehouse_id",temporaryOutWarehouseVO.getWarehouseId());
//
//
//        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//        /**
//         * 申请时间区间
//         */
//        if (ObjectUtil.isNotEmpty(temporaryOutWarehouseVO.getCreateTimeStart()) && ObjectUtil.isNotEmpty(temporaryOutWarehouseVO.getCreateTimeEnd())) {
//            String createDateStart = dtf1.format(temporaryOutWarehouseVO.getCreateTimeStart());
//            String createDateEnd = dtf1.format(temporaryOutWarehouseVO.getCreateTimeEnd());
//            /**
//             * 申请时间区间查询
//             */
//            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
//                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");
//
//        }

        return temporaryOutWarehouseMapper.selectPage(temporaryOutWarehousePage, query);
    }


    @Override
    public Result addTemporaryOutWarehouse(AddTemporaryOutWarehouseDTO addTemporaryOutWarehouseDTO) {
        try {
            /**
             * 生成领料出库单据编码（LKCK+年月日八位数字+四位流水号）
             * 1.根据addDTO中的库房和当前日期查询目前最大的流水编号
             * 2.截取最大单据编号的后四位流水号，将流水号+1得到新的单据编号
             * 3.根据编码方案中的规则自动生成编码，新增成功后返回新增单据的详细信息
             */
            QueryWrapper<TemporaryOutWarehouse> queryTemporaryOutWarehouse = new QueryWrapper<>();
            /**
             * 当前仓库
             */
            queryTemporaryOutWarehouse .eq("warehouse_id", addTemporaryOutWarehouseDTO.getWarehouseId());
            /**
             * 当前日期
             */
            String today = StrUtils.HandleData(DateUtil.today());
            queryTemporaryOutWarehouse .likeRight("document_number", "LKCK" + today);
            /**
             * likeRigh: LKCK+XXXXXXXX(当前年月日)
             */
            TemporaryOutWarehouse maxTemporaryOutWarehouse = temporaryOutWarehouseMapper.selectOne(queryTemporaryOutWarehouse.orderByDesc("id").last("limit 1"));

            //目前最大的单据编码
            String maxDocNum = null;
            if (ObjectUtil.isNotEmpty(maxTemporaryOutWarehouse)) {
//                maxDocNum = maxTemporaryOutWarehouse.getDocumentNumber();
            }

            String orderNo = null;

            //单据编码前缀-LKCK+年月日
            String code_pfix = "LKCK" + today;
//            if (maxDocNum != null && maxTemporaryOutWarehouse.getDocumentNumber().contains(code_pfix)) {
//                String code_end = maxTemporaryOutWarehouse.getDocumentNumber().substring(12, 16);
//                int endNum = Integer.parseInt(code_end);
//                int tmpNum = 10000 + endNum + 1;
//                orderNo = code_pfix + StrUtils.subStr("" + tmpNum, 1);
//            } else {
//                orderNo = code_pfix + "0001";
//            }

            /**
             * 新增单据
             */
            TemporaryOutWarehouse temporaryOutWarehouse = new TemporaryOutWarehouse();
            BeanUtil.copyProperties(addTemporaryOutWarehouseDTO, temporaryOutWarehouse);
//            temporaryOutWarehouse.setDocumentNumber(orderNo);
            int i = temporaryOutWarehouseMapper.insert(temporaryOutWarehouse);
            if (i > 0) {
                return Result.success(getTemporaryOutWarehouseByDocNumAndWarhouseId(orderNo, addTemporaryOutWarehouseDTO.getWarehouseId()), "新增成功");
            } else {
                return Result.failure(ErrorCode.SYSTEM_ERROR, "新增失败！");
            }
        } catch (Exception e) {
            log.error("新增临库出库单异常", e);
            return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常！");
        }
    }

    @Override
    public Result updateTemporaryOutWarehouse(UpdateTemporaryOutWarehouseDTO updateTemporaryOutWarehouseDTO) {
        TemporaryOutWarehouse temporaryOutWarehouseOld = new TemporaryOutWarehouse();
        BeanUtil.copyProperties(updateTemporaryOutWarehouseDTO,temporaryOutWarehouseOld);
        int update = temporaryOutWarehouseMapper.updateById(temporaryOutWarehouseOld);
        return update>0 ? Result.success():Result.failure("更新失败！");
    }

    @Override
    public TemporaryOutWarehouse getTemporaryOutWarehouseById(Integer id) {
        return temporaryOutWarehouseMapper.selectById(id);
    }

    @Override
    public TemporaryOutWarehouse getTemporaryOutWarehouseByDocNumAndWarhouseId(String docNumber, String warhouseId) {
        QueryWrapper<TemporaryOutWarehouse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number", docNumber);
        queryWrapper.eq("warehouse_id", warhouseId);
        return temporaryOutWarehouseMapper.selectOne(queryWrapper);
    }

    @Override
    public TemporaryOutWarehouse getTemporaryOutWarehouseByProcessInstanceId(String processInstanceId) {
        QueryWrapper<TemporaryOutWarehouse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("process_instance_id", processInstanceId);
        return temporaryOutWarehouseMapper.selectOne(queryWrapper);
    }

    @Override
    public Result addTemporaryRecordUpdateInventory(TemporaryOutWarehouse temporaryOutWarehouse,LoginUser loginUser) {

//        List<AddTemporaryRecordDTO> addTemporaryRecordDTOList = new ArrayList<>();
//
//                    BigDecimal nowNum = null;
//
//                    nowNum = BigDecimal.valueOf(temporaryLibraryService.getNumByMaterialCodingAndWarehouseId(temporaryOutWarehouse.getMaterialCoding(), temporaryOutWarehouse.getWarehouseId()));
//
////                    BigDecimal planNum = BigDecimal.valueOf(temporaryOutWarehouse.getRequisitionQuantity());
//
////                    int event = nowNum.compareTo(planNum);
////                    /**
////                     * event = -1 : planNuM > nowNum
////                     * event =  0 : planNuM = nowNum
////                     * event =  1 : planNuM < nowNum
////                     */
////                    if (event >= 0) {
////                        BigDecimal tempNum = planNum;
//                        List<TemporaryLibrary> temporaryLibrarieList;
//                        temporaryLibrarieList = temporaryLibraryService.getTemporaryLibraryListByMaterialCodingAndWarehouseId(temporaryOutWarehouse.getMaterialCoding(),temporaryOutWarehouse.getWarehouseId());
//
//                        for (TemporaryLibrary temporaryLibrary : temporaryLibrarieList) {
//                            Material material = materialService.getMeterialByMeterialCode(temporaryLibrary.getMaterialCoding());
//                            if (ObjectUtil.isNull(material)) {
//                                return Result.failure("物料不存在！");
//                            }
//                            //留存出库记录
//                            AddTemporaryRecordDTO addTemporaryRecordDTO = new AddTemporaryRecordDTO();
//                            /**
//                             * 1.将一条库存的数据（编码、批次、货位）中的库存数量放入出库记录的出库数量中：库存数量更新为零，出库数量新增一条数据
//                             * 2.每搬空一条库存数据，tempNum减去对应的数量
//                             * 3.tempNum不为零之前（满足计划领用数量之前）一直循环
//                             * 4.在编辑库存数据之前，判断目前的tempNum是否已经小于此条库存数据的库存数。若大于清空此条库存并循环下一条数据，若小于则更新对应数量
//                             */
//                            if (tempNum.compareTo(BigDecimal.valueOf(0)) > 0) {
//                                //tempNum大于等于此条数据的库存数量
//                                if (tempNum.compareTo(BigDecimal.valueOf(temporaryLibrary.getInventoryCredit())) >= 0) {
//                                    //更新原库存为零
//                                    UpdateTemporaryLibraryDTO updateTemporaryLibraryDTO = new UpdateTemporaryLibraryDTO();
//                                    BeanUtil.copyProperties(temporaryLibrary, updateTemporaryLibraryDTO);
//                                    updateTemporaryLibraryDTO.setInventoryCredit((double) 0);
//                                    Result update = temporaryLibraryService.updateTemporaryLibrary(updateTemporaryLibraryDTO);
//                                    if (update.isOk()) {
//                                        //新增一条出库记录
//                                        addTemporaryRecordDTO.setDocumentNumber(temporaryLibrary.getDocumentNumber());
//                                        //记录类型：1-临时库入库 2-临时库出库
//                                        addTemporaryRecordDTO.setRecordType(2);
//                                        addTemporaryRecordDTO.setCargoSpaceId(temporaryLibrary.getCargoSpaceId());
//                                        addTemporaryRecordDTO.setBatch(temporaryLibrary.getBatch());
//                                        addTemporaryRecordDTO.setOutQuantity(temporaryLibrary.getInventoryCredit());
//                                        addTemporaryRecordDTO.setWarehouseId(temporaryLibrary.getWarehouseId());
//                                        addTemporaryRecordDTO.setMaterialCoding(temporaryLibrary.getMaterialCoding());
//                                        addTemporaryRecordDTO.setMaterialName(temporaryLibrary.getMaterialName());
//                                        addTemporaryRecordDTO.setMeasurementUnit(temporaryLibrary.getMeasurementUnit());
//                                        addTemporaryRecordDTO.setWarehouseManager(loginUser.getLoginName());
//                                        addTemporaryRecordDTO.setRemark("系统生成！");
//                                        addTemporaryRecordDTOList.add(addTemporaryRecordDTO);
//                                        tempNum = tempNum.subtract(BigDecimal.valueOf(temporaryLibrary.getInventoryCredit()));
//                                    } else {
//                                        log.error("更新库存失败");
//                                        return Result.failure("更新库存失败");
//                                    }
//                                } else {
//                                    //更新库存为原库存-tempNum
//                                    UpdateTemporaryLibraryDTO updateTemporaryLibraryDTO = new UpdateTemporaryLibraryDTO();
//                                    BeanUtil.copyProperties(temporaryLibrary, updateTemporaryLibraryDTO);
//                                    BigDecimal newInventoryNum = BigDecimal.valueOf(temporaryLibrary.getInventoryCredit()).subtract(tempNum);
//                                    updateTemporaryLibraryDTO.setInventoryCredit(newInventoryNum.doubleValue());
//                                    Result update = temporaryLibraryService.updateTemporaryLibrary(updateTemporaryLibraryDTO);
//
//                                    if (update.isOk()) {
//                                        //新增一条出库记录
//                                        addTemporaryRecordDTO.setDocumentNumber(temporaryLibrary.getDocumentNumber());
//                                        //记录类型：1-临时库入库 2-临时库出库
//                                        addTemporaryRecordDTO.setRecordType(2);
//                                        addTemporaryRecordDTO.setCargoSpaceId(temporaryLibrary.getCargoSpaceId());
//                                        addTemporaryRecordDTO.setBatch(temporaryLibrary.getBatch());
//                                        addTemporaryRecordDTO.setOutQuantity(temporaryLibrary.getInventoryCredit());
//                                        addTemporaryRecordDTO.setWarehouseId(temporaryLibrary.getWarehouseId());
//                                        addTemporaryRecordDTO.setMaterialCoding(temporaryLibrary.getMaterialCoding());
//                                        addTemporaryRecordDTO.setMaterialName(temporaryLibrary.getMaterialName());
//                                        addTemporaryRecordDTO.setMeasurementUnit(temporaryLibrary.getMeasurementUnit());
//                                        addTemporaryRecordDTO.setWarehouseManager(loginUser.getLoginName());
//                                        addTemporaryRecordDTO.setRemark("系统自动生成！");
//                                        addTemporaryRecordDTOList.add(addTemporaryRecordDTO);
//                                    } else {
//                                        log.error("更新库存失败");
//                                        return Result.failure("更新库存失败");
//                                    }
//                                }
//                            } else {
//                                break;
//                            }
//                        }
//                        } else {
//                        return Result.failure("物料：" + temporaryOutWarehouse.getMaterialCoding() + " 库存不足，请重拟领用单！");
//                    }
//                //放入新增出库记录List
//                Result result = temporaryRecordService.addTemporaryRecordList(addTemporaryRecordDTOList);
//                if (!result.isOk()) {
//                    return Result.failure("新增库存记录失败");
//                } else {
//                    return Result.success("新增库存记录成功");
//                }
        return null;
        }



//    @Override
//    public Result updateTemporaryRecordAndInventory(TemporaryOutWarehouse temporaryOutWarehouse) {
//        //获取此单据下的明细单，校验批准数量是否等于应出数量，若不同回滚库存并更新详细信息
//        String docNum = temporaryOutWarehouse.getDocumentNumber();
//        String warehousId = temporaryOutWarehouse.getWarehouseId();
//            //如果批准数量不为空并不为零
//            if (ObjectUtil.isNotNull(temporaryOutWarehouse.getRequisitionQuantity()) && BigDecimal.valueOf(temporaryOutWarehouse.getRequisitionQuantity()).compareTo(BigDecimal.valueOf(0)) > 0) {
//                //领用数量
//                BigDecimal requisitionQuantity = BigDecimal.valueOf(temporaryOutWarehouse.getRequisitionQuantity());
//                //批准数量
//                BigDecimal approvalsQuantity = BigDecimal.valueOf(planUseOutDetails.getApprovalsQuantity());
//                if (requisitionQuantity.compareTo(approvalsQuantity) != 0) {
//                    List<OutboundRecord> outboundRecordList = outboundRecordService.getOutboundRecordListByDocNumAndWarehouseId(docNum, warehousId);
//                    Result result = handleOutboundRecordAndInventory(outboundRecordList, planUseOutDetails.getApprovalsQuantity());
//                    if(!result.isOk()){
//                        return result;
//                    }
//                }
//            }
//
//
//        return Result.success();
//    }

    @Override
    public Result checkStock(PlanUseOut planUseOut) {
        return null;
    }
}
