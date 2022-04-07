package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.OutboundRecord;
import com.huanhong.wms.entity.PlanUseOut;
import com.huanhong.wms.entity.WarehousingRecord;
import com.huanhong.wms.entity.dto.AddWarehousingRecordDTO;
import com.huanhong.wms.entity.dto.UpdateWarehousingRecordDTO;
import com.huanhong.wms.entity.vo.WarehousingRecordVO;
import com.huanhong.wms.mapper.WarehousingRecordMapper;
import com.huanhong.wms.service.IWarehousingRecordService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-04-07
 */
@Service
public class WarehousingRecordServiceImpl extends SuperServiceImpl<WarehousingRecordMapper, WarehousingRecord> implements IWarehousingRecordService {


    @Resource
    private WarehousingRecordMapper warehousingRecordMapper;

    @Override
    public Page<WarehousingRecord> pageFuzzyQuery(Page<WarehousingRecord> warehousingRecordPage, WarehousingRecordVO warehousingRecordVO) {

        //新建QueryWrapper对象
        QueryWrapper<WarehousingRecord> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");
        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(warehousingRecordVO)) {
            return warehousingRecordMapper.selectPage(warehousingRecordPage, query);
        }

        query.like(StringUtils.isNotBlank(warehousingRecordVO.getDocumentNumber()),"document_number",warehousingRecordVO.getDocumentNumber());

        query.like(ObjectUtil.isNotNull(warehousingRecordVO.getOutType()),"out_type",warehousingRecordVO.getOutType());

        query.like(StringUtils.isNotBlank(warehousingRecordVO.getWarehouseId()), "warehouse_id",warehousingRecordVO.getWarehouseId());

        query.like(StringUtils.isNotBlank(warehousingRecordVO.getCargoSpaceId()), "cargo_space_id",warehousingRecordVO.getCargoSpaceId());

        query.like(StringUtils.isNotBlank(warehousingRecordVO.getMaterialCoding()), "material_coding",warehousingRecordVO.getMaterialCoding());

        query.like(StringUtils.isNotBlank(warehousingRecordVO.getBatch()), "batch",warehousingRecordVO.getBatch());

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        /**
         * 创建时间时间区间
         */
        if (ObjectUtil.isNotEmpty(warehousingRecordVO.getCreateDateStart())&&ObjectUtil.isNotEmpty(warehousingRecordVO.getCreateDateEnd())){
            String enterDateStart = dtf1.format(warehousingRecordVO.getCreateDateStart());
            String enterDateEnd = dtf1.format(warehousingRecordVO.getCreateDateEnd());
            /**
             * 创建时间时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + enterDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + enterDateEnd + "')");

        }
        return warehousingRecordMapper.selectPage(warehousingRecordPage,query);
    }

    @Override
    public Result addWarehousingRecord(AddWarehousingRecordDTO addWarehousingRecordDTO) {
        WarehousingRecord warehousingRecord = new WarehousingRecord();
        BeanUtil.copyProperties(addWarehousingRecordDTO,warehousingRecord);
        int add = warehousingRecordMapper.insert(warehousingRecord);
        return add>0 ? Result.success() : Result.failure("新增失败");
    }

    @Override
    public Result updateWarehousingRecord(UpdateWarehousingRecordDTO updateWarehousingRecordDTO) {
        WarehousingRecord warehousingRecord = new WarehousingRecord();
        BeanUtil.copyProperties(updateWarehousingRecordDTO,warehousingRecord);
        int update = warehousingRecordMapper.updateById(warehousingRecord);
        return update>0 ? Result.success("更新成功！") : Result.failure("更新失败！");
    }

    @Override
    public WarehousingRecord getWarehousingRecordById(Integer id) {
        return warehousingRecordMapper.selectById(id);
    }

    @Override
    public List<WarehousingRecord> getWarehousingRecordByDocNumAndWarhouseId(String docNumber, String warehouseId) {
        QueryWrapper queryWrapper =  new QueryWrapper();
        queryWrapper.eq("document_number",docNumber);
        queryWrapper.eq("warehouse_id",warehouseId);
        return warehousingRecordMapper.selectList(queryWrapper);
    }
}
