package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ProcessAssignment;
import com.huanhong.wms.SuperService;

/**
 * <p>
 * 流程任务表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-17
 */
public interface IProcessAssignmentService extends SuperService<ProcessAssignment> {

    Result<Integer> syncProcessAssignment();
}
