package com.huanhong.wms.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.entity.CargoSpaceManagement;
import com.huanhong.wms.entity.vo.CargoSpaceVO;
import com.huanhong.wms.mapper.CargoSpaceManagementMapper;
import com.huanhong.wms.service.ICargoSpaceManagementService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 货位管理 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-08
 */
@Service
public class CargoSpaceManagementServiceImpl extends SuperServiceImpl<CargoSpaceManagementMapper, CargoSpaceManagement> implements ICargoSpaceManagementService {


    @Resource
    private CargoSpaceManagementMapper cargoSpaceManagementMapper;

    @Override
    public List<CargoSpaceManagement> getCargoSpaceListByShelfId(String shelfId) {
        QueryWrapper<CargoSpaceManagement> wrapper = new QueryWrapper<>();
        wrapper.eq("del", 0);
        wrapper.eq("shelf_id", shelfId);
        List<CargoSpaceManagement> CargoSpaceList = cargoSpaceManagementMapper.selectList(wrapper);
        return CargoSpaceList;
    }

    @Override
    public CargoSpaceManagement getCargoSpaceByCargoSpaceId(String cargoSpaceId) {
        QueryWrapper<CargoSpaceManagement> wrapper = new QueryWrapper<>();
        wrapper.eq("cargo_space_id", cargoSpaceId);
        wrapper.eq("del", 0);
        CargoSpaceManagement cargoSpaceManagement = cargoSpaceManagementMapper.selectOne(wrapper);
        return cargoSpaceManagement;
    }

    @Override
    public Page<CargoSpaceManagement> pageFuzzyQuery(Page<CargoSpaceManagement> cargoSpaceManagementPage, CargoSpaceVO cargoSpaceVO) {
        //新建QueryWrapper对象
        QueryWrapper<CargoSpaceManagement> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(cargoSpaceVO)) {
            return baseMapper.selectPage(cargoSpaceManagementPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(cargoSpaceVO.getShelfId()), "shelf_id", cargoSpaceVO.getShelfId());

        query.like(StringUtils.isNotBlank(cargoSpaceVO.getCargoSpaceId()), "cargo_space_id", cargoSpaceVO.getCargoSpaceId());

        return baseMapper.selectPage(cargoSpaceManagementPage, query);
    }
}

