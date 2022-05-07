package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.OutboundRecord;
import com.huanhong.wms.entity.TemporaryRecord;
import com.huanhong.wms.entity.dto.AddOutboundRecordDTO;
import com.huanhong.wms.entity.dto.AddTemporaryRecordDTO;
import com.huanhong.wms.entity.dto.UpdateTemporaryRecordDTO;
import com.huanhong.wms.entity.vo.TemporaryRecordVO;
import com.huanhong.wms.mapper.TemporaryRecordMapper;
import com.huanhong.wms.service.ITemporaryRecordService;
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
 * @since 2022-05-05
 */
@Service
public class TemporaryRecordServiceImpl extends SuperServiceImpl<TemporaryRecordMapper, TemporaryRecord> implements ITemporaryRecordService {

    @Resource
    private TemporaryRecordMapper temporaryRecordMapper;

    @Override
    public Page<TemporaryRecord> pageFuzzyQuery(Page<TemporaryRecord> temporaryRecordPage, TemporaryRecordVO temporaryRecordVO) {

        //新建QueryWrapper对象
        QueryWrapper<TemporaryRecord> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");

        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(temporaryRecordVO)) {
            return temporaryRecordMapper.selectPage(temporaryRecordPage, query);
        }

        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(temporaryRecordVO.getDocumentNumber()), "document_number", temporaryRecordVO.getDocumentNumber());

        query.like(StringUtils.isNotBlank(temporaryRecordVO.getRequirementsPlanningNumber()), "document_number", temporaryRecordVO.getRequirementsPlanningNumber());

        query.like(StringUtils.isNotBlank(temporaryRecordVO.getWarehouseId()), "warehouse_id", temporaryRecordVO.getWarehouseId());

        query.like(ObjectUtil.isNotNull(temporaryRecordVO.getRecordType()),"record_type",temporaryRecordVO.getRecordType());

        query.like(StringUtils.isNotBlank(temporaryRecordVO.getMaterialCoding()), "material_coding", temporaryRecordVO.getMaterialCoding());

        query.like(StringUtils.isNotBlank(temporaryRecordVO.getMaterialName()), "material_name", temporaryRecordVO.getMaterialName());

        query.like(StringUtils.isNotBlank(temporaryRecordVO.getCargoSpaceId()), "cargo_space_id", temporaryRecordVO.getCargoSpaceId());

        query.like(StringUtils.isNotBlank(temporaryRecordVO.getBatch()), "batch", temporaryRecordVO.getBatch());

        query.like(ObjectUtil.isNotNull(temporaryRecordVO.getWarehouseManager()), "warehouse_manager", temporaryRecordVO.getWarehouseManager());

        query.like(ObjectUtil.isNotNull(temporaryRecordVO.getRecipient()),"recipient",temporaryRecordVO.getRecipient());

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        /**
         * 创建时间时间区间
         */
        if (ObjectUtil.isNotEmpty(temporaryRecordVO.getCreateTimeStart()) && ObjectUtil.isNotEmpty(temporaryRecordVO.getCreateTimeEnd())) {
            String enterDateStart = dtf1.format(temporaryRecordVO.getCreateTimeStart());
            String enterDateEnd = dtf1.format(temporaryRecordVO.getCreateTimeEnd());
            /**
             * 创建时间时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + enterDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + enterDateEnd + "')");

        }
        return temporaryRecordMapper.selectPage(temporaryRecordPage, query);
    }

    @Override
    public Result addTemporaryRecordList(List<AddTemporaryRecordDTO> addTemporaryRecordDTOList) {

        int count = 0;

        for (AddTemporaryRecordDTO addTemporaryRecordDTO: addTemporaryRecordDTOList
        ) {
            TemporaryRecord temporaryRecord = new TemporaryRecord();
            BeanUtil.copyProperties(addTemporaryRecordDTO, temporaryRecord);
            int add = temporaryRecordMapper.insert(temporaryRecord);
            if (add > 0) {
                count++;
            }
        }
        return count == addTemporaryRecordDTOList.size() ? Result.success("新增记录成功") : Result.failure("新增记录失败");

    }

    @Override
    public Result addTemporaryRecord(AddTemporaryRecordDTO addTemporaryRecordDTO) {
        TemporaryRecord temporaryRecord = new TemporaryRecord();
        BeanUtil.copyProperties(addTemporaryRecordDTO, temporaryRecord);
        int add = temporaryRecordMapper.insert(temporaryRecord);
        return add>0 ? Result.success() : Result.failure("新增记录失败！");
    }

    @Override
    public Result updateTemporaryRecord(UpdateTemporaryRecordDTO updateTemporaryRecordDTO) {
        TemporaryRecord temporaryRecord = new TemporaryRecord();
        BeanUtil.copyProperties(updateTemporaryRecordDTO, temporaryRecord);
        int update = temporaryRecordMapper.updateById(temporaryRecord);
        return update > 0 ? Result.success("更新成功！") : Result.failure("更新失败！");
    }

    @Override
    public TemporaryRecord getTemporaryRecordById(Integer id) {
        return temporaryRecordMapper.selectById(id);
    }

    @Override
    public List<TemporaryRecord> getTemporaryRecordListByDocNumAndWarehouseId(String docNum, String warehouseId) {
        QueryWrapper<TemporaryRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number", docNum);
        queryWrapper.eq("warehouse_id", warehouseId);
        return temporaryRecordMapper.selectList(queryWrapper);
    }

    @Override
    public List<TemporaryRecord> getTemporaryRecordByDocNumAndWarehouseIdAndMaterialCoding(String docNum, String warehouseId, String materialCoding) {
        QueryWrapper<TemporaryRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number", docNum);
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.eq("warehouse_id", warehouseId);
        return temporaryRecordMapper.selectList(queryWrapper);
    }

    @Override
    public TemporaryRecord getTemporaryRecordByDocNumAndCargoSpaceAndMaterialCodingAndBatch(String docNum, String cargoSpace, String materialCoding, String batch) {
        QueryWrapper<TemporaryRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("document_number", docNum);
        queryWrapper.eq("material_coding", materialCoding);
        queryWrapper.eq("cargo_space_id", cargoSpace);
        queryWrapper.eq("batch", batch);
        queryWrapper.last("limit 1");
        return temporaryRecordMapper.selectOne(queryWrapper);
    }
}
