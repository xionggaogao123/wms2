package com.huanhong.wms;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;

/**
 * @author ldy81
 * @date 2019/12/11 18:46
 */
public class SuperServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements SuperService<T> {


    public Result renderNullDataError() {
        return Result.failure(ErrorCode.DATA_IS_NULL, "数据已被删除或不存在");
    }
}