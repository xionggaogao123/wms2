package com.huanhong.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.dto.request.UpdateTemporaryEnterWarehouseRequest;
import com.huanhong.wms.dto.response.TemporaryEnterWarehouseResponse;
import com.huanhong.wms.entity.TemporaryEnterWarehouse;
import com.huanhong.wms.entity.TemporaryEnterWarehouseDetails;
import com.huanhong.wms.mapper.TemporaryEnterWarehouseDetailsMapper;
import com.huanhong.wms.mapper.TemporaryEnterWarehouseMapper;
import com.huanhong.wms.service.TemporaryEnterWarehouseV1Service;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

import static org.apache.commons.math3.distribution.fitting.MultivariateNormalMixtureExpectationMaximization.estimate;

/**
 * @Author wang
 * @date 2022/5/25 13:50
 */
@Service
public class TemporaryEnterWarehouseV1ServiceImpl implements TemporaryEnterWarehouseV1Service {

    @Resource
    private TemporaryEnterWarehouseMapper temporaryEnterWarehouseMapper;

    @Resource
    private TemporaryEnterWarehouseDetailsMapper temporaryEnterWarehouseDetailsMapper;

    @Override
    public Result findById(Long id) {
        QueryWrapper<TemporaryEnterWarehouse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        //主表数据
        TemporaryEnterWarehouse temporaryEnterWarehouse = temporaryEnterWarehouseMapper.selectOne(queryWrapper);
        //子表数据
        QueryWrapper<TemporaryEnterWarehouseDetails> detailsQueryWrapper = new QueryWrapper<>();
        detailsQueryWrapper.eq("enter_number", temporaryEnterWarehouse.getEnterNumber());
        List<TemporaryEnterWarehouseDetails> temporaryEnterWarehouseDetails = temporaryEnterWarehouseDetailsMapper.selectList(detailsQueryWrapper);
        //封装返回
        TemporaryEnterWarehouseResponse temporaryEnterWarehouseResponse = new TemporaryEnterWarehouseResponse();
        temporaryEnterWarehouseResponse.setTemporaryEnterWarehouse(temporaryEnterWarehouse);
        temporaryEnterWarehouseResponse.setTemporaryEnterWarehouseDetails(temporaryEnterWarehouseDetails);
        return Result.success(temporaryEnterWarehouseResponse);
    }

    @Override
    public Result updateTemporaryEnterWarehouse(UpdateTemporaryEnterWarehouseRequest updateTemporaryEnterWarehouseRequest) {

        TemporaryEnterWarehouse temporaryEnterWarehouse = updateTemporaryEnterWarehouseRequest.getTemporaryEnterWarehouse();
        //判断是否存在该条数据
        TemporaryEnterWarehouse estimate = temporaryEnterWarehouseMapper.selectById(temporaryEnterWarehouse.getId());
        if(estimate == null){
            return Result.failure("临时入库修改失败，该数据不存在");
        }
        //根据id更新临时入库主表
        temporaryEnterWarehouseMapper.updateById(updateTemporaryEnterWarehouseRequest.getTemporaryEnterWarehouse());
        //根据id更新临时入住子表
        List<TemporaryEnterWarehouseDetails> temporaryEnterWarehouseDetails = updateTemporaryEnterWarehouseRequest.getTemporaryEnterWarehouseDetails();
        temporaryEnterWarehouseDetails.forEach(details -> {
            temporaryEnterWarehouseDetailsMapper.updateById(details);
        });
        return Result.success("更新成功");
    }

    @Override
    public Result deleteById(Long id) {
        TemporaryEnterWarehouse temporaryEnterWarehouse = temporaryEnterWarehouseMapper.selectById(id);
        if (temporaryEnterWarehouse == null) {
            return Result.failure("临时入库主表数据不存在");
        }
        //获取关联的子表数据
        String enterNumber = temporaryEnterWarehouse.getEnterNumber();
        //查询子表数据
        List<TemporaryEnterWarehouseDetails> details = selectListTemporaryEnterWarehouseDetails(enterNumber);
        if (!CollectionUtils.isEmpty(details)) {
            details.forEach(warehouseDetails -> {
                temporaryEnterWarehouseDetailsMapper.deleteById(warehouseDetails.getId());
            });
        } else {
            return Result.failure("临时入库子表数据不存在");
        }
        temporaryEnterWarehouseMapper.deleteById(id);
        return Result.success("删除临时入库成功");
    }

    /**
     * 查询临时入库子表数据
     *
     * @param enterNumber 跟主表关联字段
     * @return 子表数据
     */
    private List<TemporaryEnterWarehouseDetails> selectListTemporaryEnterWarehouseDetails(String enterNumber) {
        QueryWrapper<TemporaryEnterWarehouseDetails> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enter_number", enterNumber);
        return temporaryEnterWarehouseDetailsMapper.selectList(queryWrapper);
    }
}
