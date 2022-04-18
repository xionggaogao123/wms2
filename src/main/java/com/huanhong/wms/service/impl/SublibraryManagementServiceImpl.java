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
import com.huanhong.wms.entity.SublibraryManagement;
import com.huanhong.wms.entity.WarehouseManagement;
import com.huanhong.wms.entity.dto.AddSubliraryDTO;
import com.huanhong.wms.entity.vo.SublibraryVO;
import com.huanhong.wms.mapper.SublibraryManagementMapper;
import com.huanhong.wms.service.ISublibraryManagementService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 子库管理 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2021-12-08
 */
@Service
public class SublibraryManagementServiceImpl extends SuperServiceImpl<SublibraryManagementMapper, SublibraryManagement> implements ISublibraryManagementService {

    @Resource
    private SublibraryManagementMapper sublibraryManagementMapper;

    @Override
    public List<SublibraryManagement> getSublibraryManagementByWarehouseId(String warehouseId) {
        QueryWrapper<SublibraryManagement> wrapper = new QueryWrapper<>();
        wrapper.eq("warehouse_id", warehouseId);
        List<SublibraryManagement> sublibraryManagementList = sublibraryManagementMapper.selectList(wrapper);
        return sublibraryManagementList;
    }

    @Override
    public SublibraryManagement getSublibraryBySublibraryId(String sublibraryId) {
        QueryWrapper<SublibraryManagement> wrapper = new QueryWrapper<>();
        wrapper.eq("sublibrary_id", sublibraryId);
        SublibraryManagement sublibraryManagement = sublibraryManagementMapper.selectOne(wrapper);
        return sublibraryManagement;
    }

    @Override
    public Page<SublibraryManagement> pageFuzzyQuery(Page<SublibraryManagement> SublibraryManagementPage, SublibraryVO sublibraryVO) {

        //新建QueryWrapper对象
        QueryWrapper<SublibraryManagement> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(sublibraryVO)) {
            return baseMapper.selectPage(SublibraryManagementPage, query);
        }
        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(sublibraryVO.getWarehouseId()), "warehouse_id", sublibraryVO.getWarehouseId());

        query.like(StringUtils.isNotBlank(sublibraryVO.getSublibraryId()), "sublibrary_id", sublibraryVO.getSublibraryId());

        query.like(StringUtils.isNotBlank(sublibraryVO.getSublibraryName()), "sublibrary_name", sublibraryVO.getSublibraryName());

        return baseMapper.selectPage(SublibraryManagementPage, query);
    }

    /**
     * 查询子库是否停用 0-使用中 1-停用
     *
     * @param sublibraryId
     * @return
     */
    @Override
    public int isStopUsing(String sublibraryId) {
        SublibraryManagement sublibraryManagement = getSublibraryBySublibraryId(sublibraryId);
        return sublibraryManagement.getStopUsing();
    }

    @Override
    public Result addSublibraryManagement(AddSubliraryDTO addSubliraryDTO) {
            /**
             * 判断子库编号是否是两位数字
             */
            if (addSubliraryDTO.getSublibraryId().length() != 2 || !StrUtils.isNumeric(addSubliraryDTO.getSublibraryId())) {
                return Result.failure(ErrorCode.DATA_IS_NULL, "子库号为两位数字!");
            }

            /**
             *组合仓库编号和子库编号 为完整子库编号
             */
            addSubliraryDTO.setSublibraryId(addSubliraryDTO.getWarehouseId()+addSubliraryDTO.getSublibraryId());

            SublibraryManagement sublibrary = getSublibraryBySublibraryId(addSubliraryDTO.getSublibraryId());
            if (ObjectUtil.isNotEmpty(sublibrary)) {
                return Result.failure(ErrorCode.DATA_EXISTS_ERROR, "子库编号重复");
            }
            SublibraryManagement sublibraryManagement = new SublibraryManagement();
            BeanUtil.copyProperties(addSubliraryDTO, sublibraryManagement);
            int insert = sublibraryManagementMapper.insert(sublibraryManagement);
            if (insert>0){
                return  Result.success(getSublibraryBySublibraryId(addSubliraryDTO.getSublibraryId()));
            }else {
                return Result.failure("新增子库失败！");
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
        SublibraryManagement sublibraryManagement = new SublibraryManagement();
        if (enable){
            //启用所有子级
            sublibraryManagement.setStopUsing(0);
        }else {
            //停用所有子级
            sublibraryManagement.setStopUsing(1);
        }
        updateWrapper.likeRight("warehouse_id", parentCode);
        return sublibraryManagementMapper.update(sublibraryManagement, updateWrapper);
    }
}