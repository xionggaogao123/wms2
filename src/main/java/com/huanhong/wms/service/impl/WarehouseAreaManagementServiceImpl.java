package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.common.units.StrUtils;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.WarehouseAreaManagement;
import com.huanhong.wms.entity.dto.AddWarehouseAreaDTO;
import com.huanhong.wms.entity.vo.WarehouseAreaVO;
import com.huanhong.wms.mapper.WarehouseAreaManagementMapper;
import com.huanhong.wms.service.IWarehouseAreaManagementService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 库房区域管理 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-08
 */
@Service
public class WarehouseAreaManagementServiceImpl extends SuperServiceImpl<WarehouseAreaManagementMapper, WarehouseAreaManagement> implements IWarehouseAreaManagementService {

    @Resource
    private WarehouseAreaManagementMapper warehouseAreaManagementMapper;

    //根据子库ID获取对应库区
    @Override
    public List<WarehouseAreaManagement> getWarehouseAreaListBySublibraryId(String sublibraryId) {
        QueryWrapper<WarehouseAreaManagement> wrapper = new QueryWrapper<>();
        wrapper.eq("sublibrary_id", sublibraryId);
        List<WarehouseAreaManagement> warehouseAreaList = warehouseAreaManagementMapper.selectList(wrapper);
        return warehouseAreaList;
    }
    //根据库区ID获取对应库区信息
    @Override
    public WarehouseAreaManagement getWarehouseAreaByWarehouseAreaId(String WarehouseAreaId) {
        QueryWrapper<WarehouseAreaManagement> wrapper = new QueryWrapper<>();
        wrapper.eq("warehouse_area_id", WarehouseAreaId);
        WarehouseAreaManagement warehouseAreaManagement = warehouseAreaManagementMapper.selectOne(wrapper);
        return warehouseAreaManagement;
    }

    //分页组合模糊查询
    @Override
    public Page<WarehouseAreaManagement> pageFuzzyQuery(Page<WarehouseAreaManagement> WarehouseAreaManagementPage, WarehouseAreaVO warehouseAreaVO) {
        //新建QueryWrapper对象
        QueryWrapper<WarehouseAreaManagement> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(warehouseAreaVO)) {
            return baseMapper.selectPage(WarehouseAreaManagementPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(warehouseAreaVO.getSubLibraryId()), "sublibrary_id", warehouseAreaVO.getSubLibraryId());

        query.like(StringUtils.isNotBlank(warehouseAreaVO.getWarehouseAreaId()), "warehouse_area_id", warehouseAreaVO.getWarehouseAreaId());

        query.like(StringUtils.isNotBlank(warehouseAreaVO.getWarehouseAreaName()), "warehouse_area_name", warehouseAreaVO.getWarehouseAreaName());

        return baseMapper.selectPage(WarehouseAreaManagementPage, query);
    }

    /**
     * 查询某库区是否停用 0- 使用中  1- 停用
     * @param warehouseAreaId
     * @return
     */
    @Override
    public int isStopUsing(String warehouseAreaId) {
        WarehouseAreaManagement warehouseAreaManagement = getWarehouseAreaByWarehouseAreaId(warehouseAreaId);
        return warehouseAreaManagement.getStopUsing();
    }

    @Override
    public Result addWarehouseArea(AddWarehouseAreaDTO addWarehouseAreaDTO) {
        /**
         *验证库区编码
         */

        if(addWarehouseAreaDTO.getWarehouseAreaId().length()!=1||!StrUtils.isEnglish(addWarehouseAreaDTO.getWarehouseAreaId())){
            return Result.failure(ErrorCode.DATA_IS_NULL, "库区编号为除I和O以外的一位大写字母");
        }

        /**
         * 组合子库编号和库区编号为完整库区编号
         */
        addWarehouseAreaDTO.setWarehouseAreaId(addWarehouseAreaDTO.getSublibraryId()+addWarehouseAreaDTO.getWarehouseAreaId());

        WarehouseAreaManagement warehouseAreaIsExist = getWarehouseAreaByWarehouseAreaId(addWarehouseAreaDTO.getWarehouseAreaId());
        if (ObjectUtil.isNotEmpty(warehouseAreaIsExist)) {
            return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "库区编号重复,库区已存在");
        }
        WarehouseAreaManagement warehouseAreaManagement = new WarehouseAreaManagement();
        BeanUtil.copyProperties(addWarehouseAreaDTO, warehouseAreaManagement);
        int insert = warehouseAreaManagementMapper.insert(warehouseAreaManagement);
        if (insert > 0) {
            return Result.success(getWarehouseAreaByWarehouseAreaId(warehouseAreaManagement.getWarehouseAreaId()));
        } else {
            return Result.failure("添加库区失败！");
        }
    }

    /**
     *
     * @param parentCode
     * @param enable enable true = 随父级启用  false = 随父级停用
     * @return
     */
    @Override
    public int stopUsingByParentCode(String parentCode,boolean enable) {
        UpdateWrapper updateWrapper= new UpdateWrapper();
        WarehouseAreaManagement warehouseAreaManagement = new WarehouseAreaManagement();
        if (enable){
            //启用所有子级
            warehouseAreaManagement.setStopUsing(0);
        }else {
            //停用所有子级
            warehouseAreaManagement.setStopUsing(1);
        }
        //更新所有子库编号模糊匹配的库区
        //从库区开始 有2级及以上父级所以需要likeRight 自己的父编码
        updateWrapper.likeRight("sublibrary_id", parentCode);
        return warehouseAreaManagementMapper.update(warehouseAreaManagement, updateWrapper);
    }
}
