package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.exception.BizException;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.dto.request.TemporaryOutWarehouseDetailsRequest;
import com.huanhong.wms.dto.request.TemporaryOutWarehouseV1AddRequest;
import com.huanhong.wms.dto.response.TemporaryOutWarehouseResponse;
import com.huanhong.wms.entity.TemporaryEnterWarehouse;
import com.huanhong.wms.entity.TemporaryEnterWarehouseDetails;
import com.huanhong.wms.entity.TemporaryOutWarehouse;
import com.huanhong.wms.entity.TemporaryOutWarehouseDetails;
import com.huanhong.wms.entity.vo.TemporaryOutWarehouseVO;
import com.huanhong.wms.mapper.TemporaryEnterWarehouseDetailsMapper;
import com.huanhong.wms.mapper.TemporaryEnterWarehouseMapper;
import com.huanhong.wms.mapper.TemporaryOutWarehouseDetailsMapper;
import com.huanhong.wms.mapper.TemporaryOutWarehouseMapper;
import com.huanhong.wms.service.TemporaryOutWarehouseV1Service;
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

    /**
     * 添加临时出库主表和子表数据
     * @param request 主表子表数据
     * @return
     */
    @Override
    public Result addMasterAndSublist(TemporaryOutWarehouseV1AddRequest request) {
        //添加主表数据返回对应的出库编号
        String outNumber = addWarehouse(request);
        //添加子表数据
        addWarehouseDetails(request.getTemporaryOutWarehouseDetailsRequest(), outNumber);
        //扣减对应的库存
        deductingInventory(request.getTemporaryOutWarehouseDetailsRequest());
        return Result.success("添加临时出库信息成功");
    }

    /**
     * 扣减临时入库物资数量
     * @param temporaryOutWarehouseDetailsRequest 出库数据
     */
    private void deductingInventory(List<TemporaryOutWarehouseDetailsRequest> temporaryOutWarehouseDetailsRequest) {
        BigDecimal number3 = new BigDecimal(0);
        //遍历数据扣减库存
        temporaryOutWarehouseDetailsRequest.forEach(details->{
            QueryWrapper<TemporaryEnterWarehouseDetails> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("material_coding",details.getMaterialCoding());
            queryWrapper.eq("material_name",details.getMaterialName());
            //查询入库子表数据
            List<TemporaryEnterWarehouseDetails> enterWarehouseDetails = temporaryEnterWarehouseDetailsMapper.selectList(queryWrapper);
            //遍历 扣减对应的数据
            enterWarehouseDetails.forEach(warehouseDetails -> {
                BigDecimal number1 = new BigDecimal(warehouseDetails.getArrivalQuantity());
                BigDecimal number2 = new BigDecimal(details.getRequisitionQuantity());
                if(number1.compareTo(number2) > 0){
                    throw new BizException("出库数量大于库存数量");
                }
                BigDecimal subtract = number1.subtract(number2);
                //当库存数据==0时删除子表数据 当库存数据<0
                if(subtract.compareTo(number3) == 0){
                    //删除入库子表数据
                    temporaryEnterWarehouseDetailsMapper.deleteById(warehouseDetails.getId());
                    //查询该入库编号下是否存在数据 不存在数据删除入库单
                    QueryWrapper<TemporaryEnterWarehouseDetails> temporaryEnterWarehouseDetailsQueryWrapper = new QueryWrapper<>();
                    temporaryEnterWarehouseDetailsQueryWrapper.eq("enter_number",warehouseDetails.getEnterNumber());
                    List<TemporaryEnterWarehouseDetails> temporaryEnterWarehouseDetails = temporaryEnterWarehouseDetailsMapper.selectList(temporaryEnterWarehouseDetailsQueryWrapper);
                    if(CollectionUtils.isEmpty(temporaryEnterWarehouseDetails)){
                        QueryWrapper<TemporaryEnterWarehouse> temporaryEnterWarehouseQueryWrapper = new QueryWrapper<>();
                        temporaryEnterWarehouseQueryWrapper.eq("enter_number",warehouseDetails.getEnterNumber());
                        temporaryEnterWarehouseMapper.delete(temporaryEnterWarehouseQueryWrapper);
                    }
                }
            });
        });


    }

    /**
     * 根据id查询信息
     * @param id id
     * @return 返回值
     */
    @Override
    public Result selectById(Long id) {
        //查询临时出库主表数据
        TemporaryOutWarehouse temporaryOutWarehouse = warehouseManager.selectById(id);
        if (temporaryOutWarehouse == null) {
            return Result.failure("暂无临时出库数据");
        }
        //查询临时出库子表数据
        QueryWrapper<TemporaryOutWarehouseDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_number", temporaryOutWarehouse.getOutNumber());
        List<TemporaryOutWarehouseDetails> warehouseDetails = warehouseDetailsManager.selectList(queryWrapper);
        //封转数据返回
        TemporaryOutWarehouseResponse response = new TemporaryOutWarehouseResponse();
        response.setWarehouseDetails(warehouseDetails);
        response.setTemporaryOutWarehouse(temporaryOutWarehouse);
        return Result.success(response);
    }

    /**
     * 根据id删除
     * @param id id
     * @return 返回值
     */
    @Override
    public Result deleteById(Long id) {
        //查询临时出库主表是否存在
        TemporaryOutWarehouse temporaryOutWarehouse = warehouseManager.selectById(id);
        if (temporaryOutWarehouse == null) {
            return Result.failure("临时主表已删除");
        }
        String outNumber = temporaryOutWarehouse.getOutNumber();
        //删除临时主表数据
        warehouseManager.deleteById(id);
        //删除子表数据
        QueryWrapper<TemporaryOutWarehouseDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("out_number", outNumber);
        warehouseDetailsManager.delete(queryWrapper);
        return Result.success("删除临时出库成功");
    }

    @Override
    public Page<TemporaryOutWarehouse> pageFuzzyQuery(Page<TemporaryOutWarehouse> objectPage, TemporaryOutWarehouseVO temporaryOutWarehouseVO) {
        QueryWrapper<TemporaryOutWarehouse> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if(temporaryOutWarehouseVO == null){
            return warehouseManager.selectPage(objectPage,queryWrapper);
        }
        //条件查询
        queryWrapper.like(StringUtils.isNotBlank(temporaryOutWarehouseVO.getOutNumber()), "document_number", temporaryOutWarehouseVO.getOutNumber());
        queryWrapper.like(ObjectUtil.isNotNull(temporaryOutWarehouseVO.getStatus()), "status", temporaryOutWarehouseVO.getStatus());
        queryWrapper.like(StringUtils.isNotBlank(temporaryOutWarehouseVO.getBatch()), "batch", temporaryOutWarehouseVO.getBatch());
        queryWrapper.like(StringUtils.isNotBlank(temporaryOutWarehouseVO.getRequisitioningUnit()), "requisitioning_unit", temporaryOutWarehouseVO.getRequisitioningUnit());
        queryWrapper.eq(StringUtils.isNotBlank(temporaryOutWarehouseVO.getRecipient()), "recipient", temporaryOutWarehouseVO.getRecipient());
        queryWrapper.eq(StringUtils.isNotBlank(temporaryOutWarehouseVO.getWarehouseId()),"warehouse_id",temporaryOutWarehouseVO.getWarehouseId());
        //根据时间查询
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (ObjectUtil.isNotEmpty(temporaryOutWarehouseVO.getCreateTimeStart()) && ObjectUtil.isNotEmpty(temporaryOutWarehouseVO.getCreateTimeEnd())) {
            String createDateStart = dtf1.format(temporaryOutWarehouseVO.getCreateTimeStart());
            String createDateEnd = dtf1.format(temporaryOutWarehouseVO.getCreateTimeEnd());
            queryWrapper.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + createDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + createDateEnd + "')");

        }

        return warehouseManager.selectPage(objectPage, queryWrapper);
    }

    /**
     * 添加临时出库子表信息
     * @param detailsRequest 子表数据
     * @param outNumber 主表关联字段
     */
    private void addWarehouseDetails(List<TemporaryOutWarehouseDetailsRequest> detailsRequest, String outNumber) {
        //转换
        List<TemporaryOutWarehouseDetails> temporaryOutWarehouseDetails = BeanUtil.copyToList(detailsRequest, TemporaryOutWarehouseDetails.class);
        temporaryOutWarehouseDetails.forEach(details -> {
            details.setOutNumber(outNumber);
            details.setCreateTime(LocalDateTime.now());
            details.setLastUpdate(LocalDateTime.now());
            warehouseDetailsManager.insert(details);
        });
    }

    /**
     * 添加临时出库主表数据
     * @param request 主表数据
     * @return 返回值
     */
    private String addWarehouse(TemporaryOutWarehouseV1AddRequest request) {
        TemporaryOutWarehouse temporaryOutWarehouse = new TemporaryOutWarehouse();
        BeanUtil.copyProperties(request.getTemporaryOutWarehouseRequest(), temporaryOutWarehouse);
        temporaryOutWarehouse.setOutNumber("LKCK" + String.valueOf(System.currentTimeMillis()));
        temporaryOutWarehouse.setLastUpdate(LocalDateTime.now());
        int insert = warehouseManager.insert(temporaryOutWarehouse);
        if (insert > 0) {
            TemporaryOutWarehouse warehouse = warehouseManager.selectById(temporaryOutWarehouse.getId());
            return warehouse.getOutNumber();
        }
        return null;
    }
}
