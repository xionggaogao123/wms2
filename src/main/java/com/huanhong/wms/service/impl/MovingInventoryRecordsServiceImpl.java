package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.AllocationPlanDetail;
import com.huanhong.wms.entity.MovingInventoryRecords;
import com.huanhong.wms.entity.PlanUseOut;
import com.huanhong.wms.entity.dto.AddAllocationPlanDetailDTO;
import com.huanhong.wms.entity.dto.AddMovingInventoryRecordsDTO;
import com.huanhong.wms.entity.dto.UpdateMovingInventoryRecordsDTO;
import com.huanhong.wms.entity.vo.MovingInventoryRecordsVO;
import com.huanhong.wms.mapper.MovingInventoryRecordsMapper;
import com.huanhong.wms.service.IMovingInventoryRecordsService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 移动库存记录表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-04-11
 */
@Service
public class MovingInventoryRecordsServiceImpl extends SuperServiceImpl<MovingInventoryRecordsMapper, MovingInventoryRecords> implements IMovingInventoryRecordsService {

    @Resource
    private MovingInventoryRecordsMapper movingInventoryRecordsMapper;

    @Override
    public Page<MovingInventoryRecords> pageFuzzyQuery(Page<MovingInventoryRecords> movingInventoryRecordsPage, MovingInventoryRecordsVO movingInventoryRecordsVO) {

        //新建QueryWrapper对象
        QueryWrapper<MovingInventoryRecords> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(movingInventoryRecordsVO)) {
            return movingInventoryRecordsMapper.selectPage(movingInventoryRecordsPage, query);
        }

        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(movingInventoryRecordsVO.getWarehouseId()), "warehouse_id", movingInventoryRecordsVO.getWarehouseId());

        query.like(StringUtils.isNotBlank(movingInventoryRecordsVO.getMaterialCoding()),"material_coding",movingInventoryRecordsVO.getMaterialCoding());

        query.like(StringUtils.isNotBlank(movingInventoryRecordsVO.getBatch()),"batch",movingInventoryRecordsVO.getBatch());

        query.like(StringUtils.isNotBlank(movingInventoryRecordsVO.getPreCargoSpaceId()),"pre_cargo_space_id",movingInventoryRecordsVO.getPreCargoSpaceId());

        query.like(StringUtils.isNotBlank(movingInventoryRecordsVO.getNewCargoSpaceId()),"new_cargo_space_id",movingInventoryRecordsPage);

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        /**
         * 申请时间区间
         */
        if (ObjectUtil.isNotEmpty(movingInventoryRecordsVO.getCreateTimeStart()) && ObjectUtil.isNotEmpty(movingInventoryRecordsVO.getCreateTimeEnd())) {
            String applicationDateStart = dtf1.format(movingInventoryRecordsVO.getCreateTimeStart());
            String applicationDateEnd = dtf1.format(movingInventoryRecordsVO.getCreateTimeEnd());
            /**
             * 申请时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + applicationDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + applicationDateEnd + "')");

        }

        return movingInventoryRecordsMapper.selectPage(movingInventoryRecordsPage, query);

    }

    @Override
    public Result addMovingInventoryRecordsList(List<AddMovingInventoryRecordsDTO> addMovingInventoryRecordsDTOSList) {
        List<AddMovingInventoryRecordsDTO> listSuccess = new ArrayList<>();
        List<AddMovingInventoryRecordsDTO> listFalse = new ArrayList<>();
        MovingInventoryRecords movingInventoryRecords = new MovingInventoryRecords();
        HashMap map = new HashMap();
        for (AddMovingInventoryRecordsDTO addMovingInventoryRecordsDTO:addMovingInventoryRecordsDTOSList
             ) {
            BeanUtil.copyProperties(addMovingInventoryRecordsDTO,movingInventoryRecords);
            int add = movingInventoryRecordsMapper.insert(movingInventoryRecords);
            if (add > 0) {
                listSuccess.add(addMovingInventoryRecordsDTO);
            } else {
                listFalse.add(addMovingInventoryRecordsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public Result addMovingInventoryRecords(AddMovingInventoryRecordsDTO addMovingInventoryRecordsDTO) {
        MovingInventoryRecords movingInventoryRecords = new MovingInventoryRecords();
        BeanUtil.copyProperties(addMovingInventoryRecordsDTO,movingInventoryRecords);
        int add = movingInventoryRecordsMapper.insert(movingInventoryRecords);
        return add>0?Result.success("新增成功"):Result.failure("新增失败");
    }

    @Override
    public Result updateMovingInventoryRecords(List<UpdateMovingInventoryRecordsDTO> updateMovingInventoryRecordsDTOList) {
        List<UpdateMovingInventoryRecordsDTO> listSuccess = new ArrayList<>();
        List<UpdateMovingInventoryRecordsDTO> listFalse = new ArrayList<>();
        MovingInventoryRecords movingInventoryRecords = new MovingInventoryRecords();
        HashMap map = new HashMap();
        for (UpdateMovingInventoryRecordsDTO updateMovingInventoryRecordsDTO:updateMovingInventoryRecordsDTOList
        ) {
            BeanUtil.copyProperties(updateMovingInventoryRecordsDTO,movingInventoryRecords);
            int update = movingInventoryRecordsMapper.updateById(movingInventoryRecords);
            if (update > 0) {
                listSuccess.add(updateMovingInventoryRecordsDTO);
            } else {
                listFalse.add(updateMovingInventoryRecordsDTO);
            }
        }
        map.put("success", listSuccess);
        map.put("false", listFalse);
        return Result.success(map);
    }

    @Override
    public MovingInventoryRecords getMovingInventoryRecordsById(Integer id) {
        return movingInventoryRecordsMapper.selectById(id);
    }
}
