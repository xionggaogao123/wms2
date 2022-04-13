package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.PlanUseOut;
import com.huanhong.wms.entity.WarehouseManagement;
import com.huanhong.wms.entity.WarehouseManager;
import com.huanhong.wms.entity.dto.AddWarehouseManagerDTO;
import com.huanhong.wms.entity.dto.UpdateWarehouseManagerDTO;
import com.huanhong.wms.entity.vo.WarehouseManagerVO;
import com.huanhong.wms.mapper.WarehouseManagerMapper;
import com.huanhong.wms.service.IWarehouseManagerService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-04-13
 */
@Service
public class WarehouseManagerServiceImpl extends SuperServiceImpl<WarehouseManagerMapper, WarehouseManager> implements IWarehouseManagerService {

    @Resource
    private WarehouseManagerMapper warehouseManagerMapper;


    @Override
    public Page<WarehouseManager> pageFuzzyQuery(Page<WarehouseManager> warehouseManagertPage, WarehouseManagerVO warehouseManagerVO) {

        //新建QueryWrapper对象
        QueryWrapper<WarehouseManager> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(warehouseManagerVO)) {
            return baseMapper.selectPage(warehouseManagertPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(ObjectUtil.isNotEmpty(warehouseManagerVO.getUserId()), "user_id", warehouseManagerVO.getUserId());

        query.like(StringUtils.isNotBlank(warehouseManagerVO.getWarehouseId()), "warehouse_id", warehouseManagerVO.getWarehouseId());

        return baseMapper.selectPage(warehouseManagertPage, query);

    }

    @Override
    public Result addWarehouseManager(AddWarehouseManagerDTO addWarehouseManagerDTO) {
        WarehouseManager warehouseManager = new WarehouseManager();
        BeanUtil.copyProperties(addWarehouseManagerDTO,warehouseManager);
        int add = warehouseManagerMapper.insert(warehouseManager);
        return add>0 ? Result.success() : Result.failure("新增失败");
    }

    @Override
    public Result updateWarehouseManager(UpdateWarehouseManagerDTO updateWarehouseManagerDTO) {
        WarehouseManager warehouseManagerOld = warehouseManagerMapper.selectById(updateWarehouseManagerDTO.getId());
        BeanUtil.copyProperties(updateWarehouseManagerDTO,warehouseManagerOld);
        int update = warehouseManagerMapper.updateById(warehouseManagerOld);
        return update>0 ? Result.success() : Result.failure("更新失败");
    }

    @Override
    public WarehouseManager getWarehouseManagerById(Integer id) {
        return warehouseManagerMapper.selectById(id);
    }

    @Override
    public List<WarehouseManager> getWarehouseManagerListByUserId(Integer userId) {
        QueryWrapper<WarehouseManager> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        return warehouseManagerMapper.selectList(queryWrapper);
    }
}
