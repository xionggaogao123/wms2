package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;
import com.huanhong.wms.dto.request.RecordRequest;
import com.huanhong.wms.entity.Record;

import java.util.List;

/**
 * @Author wang
 * @date 2022/5/30 20:21
 */
public interface RecordService {

    /**
     * 添加
     * @param record 出入库数据
     * @return 返回值
     */
    Result addRecord(Record record);


    /**
     * 查询出入库记录
     * @param recordRequest 参数
     * @return 返回值
     */
    List<Record> getRecords(RecordRequest recordRequest);

}
