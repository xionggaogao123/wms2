package com.huanhong.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.dto.request.RecordRequest;
import com.huanhong.wms.entity.Record;
import com.huanhong.wms.mapper.RecordMapper;
import com.huanhong.wms.service.RecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author wang
 * @date 2022/5/30 20:22
 */
@Service
public class RecordServiceImpl implements RecordService {

    @Resource
    private RecordMapper recordMapper;

    @Override
    public Result addRecord(Record record) {
        int insert = recordMapper.insert(record);
        if (insert > 0) {
            return Result.success("添加成功");
        }
        return Result.failure("添加失败");
    }

    @Override
    public List<Record> getRecords(RecordRequest recordRequest) {
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("material_coding",recordRequest.getMaterialCoding());
        queryWrapper.eq("material_name",recordRequest.getMaterialName());
        queryWrapper.eq("batch",recordRequest.getBatch());
        return recordMapper.selectList(queryWrapper);
    }
}
