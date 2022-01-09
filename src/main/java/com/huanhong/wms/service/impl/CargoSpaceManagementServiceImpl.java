package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.entity.CargoSpaceManagement;
import com.huanhong.wms.entity.ShelfManagement;
import com.huanhong.wms.entity.dto.AddCargoSpacedDTO;
import com.huanhong.wms.entity.vo.CargoSpaceVO;
import com.huanhong.wms.mapper.CargoSpaceManagementMapper;
import com.huanhong.wms.service.ICargoSpaceManagementService;
import com.huanhong.wms.service.IShelfManagementService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
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

    @Resource
    private IShelfManagementService shelfManagementService;

    @Override
    public int addCargoSpace(AddCargoSpacedDTO addCargoSpacedDTO) {
        ShelfManagement shelfManagement = shelfManagementService.getShelfByShelfId(addCargoSpacedDTO.getShelfId());
        Integer floor = shelfManagement.getShelfLayer();
        Integer cellNums = shelfManagement.getCellNumber();
        int insert = 0;
        //货架高度除以层数 等于每层高度 等于或为高度
        BigDecimal height = NumberUtil.div(shelfManagement.getShelfHeight(),shelfManagement.getShelfLayer(),3);
        addCargoSpacedDTO.setCargoSpaceHeight(height.doubleValue());
        //货位长度等于货架长度除以每层货位数
        BigDecimal length = NumberUtil.div(shelfManagement.getShelfBottomLength(),shelfManagement.getCellNumber(),3);
        addCargoSpacedDTO.setCargoSpaceLength(length.doubleValue());
        //货位宽度等于货架宽度
        addCargoSpacedDTO.setCargoSpaceWidth(shelfManagement.getShelfBottomWidth());
        addCargoSpacedDTO.setCargoSpaceType(shelfManagement.getShelfType());
        addCargoSpacedDTO.setRemark("自动生成货位");
        for (int i=1;i<=floor;i++){
            addCargoSpacedDTO.setCargoSpaceFloor(i);
            for (int j=1;j<=cellNums;j++){
                addCargoSpacedDTO.setCargoSpaceId(addCargoSpacedDTO.getShelfId()+i+j);
                CargoSpaceManagement addCargoSpace = new CargoSpaceManagement();
                BeanUtil.copyProperties(addCargoSpacedDTO, addCargoSpace);
                insert = cargoSpaceManagementMapper.insert(addCargoSpace);
            }
        }
        return insert;
    }

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

