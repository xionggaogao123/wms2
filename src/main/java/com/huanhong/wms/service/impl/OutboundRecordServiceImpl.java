package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.SuperServiceImpl;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.OutboundRecord;
import com.huanhong.wms.entity.dto.AddOutboundRecordDTO;
import com.huanhong.wms.entity.dto.UpdateOutboundRecordDTO;
import com.huanhong.wms.entity.vo.OutboundRecordVO;
import com.huanhong.wms.mapper.OutboundRecordMapper;
import com.huanhong.wms.service.IOutboundRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <p>
 * 出库记录 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-07
 */
@Service
public class OutboundRecordServiceImpl extends SuperServiceImpl<OutboundRecordMapper, OutboundRecord> implements IOutboundRecordService {


    @Resource
    private OutboundRecordMapper outboundRecordMapper;


    /**
     * 分页查询
     * @param outboundRecordPage
     * @param outboundRecordVO
     * @return
     */
    @Override
    public Page<OutboundRecord> pageFuzzyQuery(Page<OutboundRecord> outboundRecordPage, OutboundRecordVO outboundRecordVO) {

        //新建QueryWrapper对象
        QueryWrapper<OutboundRecord> query = new QueryWrapper<>();
        //根据id排序
        query.orderByDesc("id");

        //判断此时的条件对象Vo是否等于空，若等于空，
        //直接进行selectPage查询
        if (ObjectUtil.isEmpty(outboundRecordVO)) {
            return outboundRecordMapper.selectPage(outboundRecordPage, query);
        }

        //若Vo对象不为空，分别获取其中的字段，
        //并对其进行判断是否为空，这一步类似动态SQL的拼装
        query.like(StringUtils.isNotBlank(outboundRecordVO.getDocumentNumber()), "document_number", outboundRecordVO.getDocumentNumber());

        query.like(StringUtils.isNotBlank(outboundRecordVO.getWarehouseId()), "warehouse_id", outboundRecordVO.getWarehouseId());

        query.like(StringUtils.isNotBlank(outboundRecordVO.getMaterialCoding()),"material_coding",outboundRecordVO.getMaterialCoding());

        query.like(StringUtils.isNotBlank(outboundRecordVO.getDetails()),"details",outboundRecordVO.getDetails());

        query.like(ObjectUtil.isNotNull(outboundRecordVO.getStatus()),"status",outboundRecordVO.getStatus());

        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        /**
         * 创建时间时间区间
         */
        if (ObjectUtil.isNotEmpty(outboundRecordVO.getCreateDateStart())&&ObjectUtil.isNotEmpty(outboundRecordVO.getCreateDateEnd())){
            String enterDateStart = dtf1.format(outboundRecordVO.getCreateDateStart());
            String enterDateEnd = dtf1.format(outboundRecordVO.getCreateDateEnd());
            /**
             * 创建时间时间区间查询
             */
            query.apply("UNIX_TIMESTAMP(create_time) >= UNIX_TIMESTAMP('" + enterDateStart + "')")
                    .apply("UNIX_TIMESTAMP(create_time) <= UNIX_TIMESTAMP('" + enterDateEnd + "')");

        }
        return outboundRecordMapper.selectPage(outboundRecordPage,query);
    }

    @Override
    public Result addOutboundRecord(AddOutboundRecordDTO addOutboundRecordDTO) {
        OutboundRecord outboundRecord = new OutboundRecord();
        BeanUtil.copyProperties(addOutboundRecordDTO,outboundRecord);
        int add = outboundRecordMapper.insert(outboundRecord);
        return add>0 ? Result.success() : Result.failure("新增失败");
    }

    @Override
    public Result addOutboundRecordList(List<AddOutboundRecordDTO> addOutboundRecordDTOList) {
        int count = 0;
        for (AddOutboundRecordDTO addOutboundRecordDTO : addOutboundRecordDTOList
             ) {
            OutboundRecord outboundRecord = new OutboundRecord();
            BeanUtil.copyProperties(addOutboundRecordDTO,outboundRecord);
            int add =  outboundRecordMapper.insert(outboundRecord);
            if (add>1){
                count++;
            }
        }
        return count==addOutboundRecordDTOList.size() ? Result.success("新增记录成功") : Result.failure("新增记录失败");
    }

    @Override
    public Result updateOutboundRecord(UpdateOutboundRecordDTO updateOutboundRecordDTO) {
        OutboundRecord outboundRecord = new OutboundRecord();
        BeanUtil.copyProperties(updateOutboundRecordDTO,outboundRecord);
        int update = outboundRecordMapper.updateById(outboundRecord);
        return update>0 ? Result.success("更新成功！") : Result.failure("更新失败！");
    }

    @Override
    public OutboundRecord getOutboundRecordById(Integer id) {
        return outboundRecordMapper.selectById(id);
    }

    @Override
    public List<OutboundRecord> getOutboundRecordListByDocNumAndWarehouseId(String docNum, String warehouseId) {
        QueryWrapper queryWrapper =  new QueryWrapper();
        queryWrapper.eq("document_number",docNum);
        queryWrapper.eq("warehouse_id",warehouseId);
        return outboundRecordMapper.selectList(queryWrapper);
    }

    @Override
    public OutboundRecord getOutboundRecordListByDocNumAndWarehouseIdAndMaterialCoding(String docNum, String warehouseId, String materialCoding) {
        QueryWrapper queryWrapper =  new QueryWrapper();
        queryWrapper.eq("document_number",docNum);
        queryWrapper.eq("material_coding",materialCoding);
        queryWrapper.eq("warehouse_id",warehouseId);
        return outboundRecordMapper.selectOne(queryWrapper);
    }
}
