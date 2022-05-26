package com.huanhong.wms.dto.response;

import com.huanhong.wms.entity.TemporaryRecord;
import com.huanhong.wms.entity.TemporaryRecordDetails;
import lombok.Data;

import java.util.List;

/**
 * @Author wang
 * @date 2022/5/27 0:10
 */
@Data
public class TemporaryRecordResponse {

    TemporaryRecord temporaryRecord;

    List<TemporaryRecordDetails> temporaryRecordDetails;
}
