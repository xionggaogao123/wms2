package com.huanhong.wms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.ProcessAssignment;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.dto.UpPaStatus;
import com.huanhong.wms.entity.param.ApproveParam;
import com.huanhong.wms.entity.param.ProcessAssignmentParam;

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

    Result<Page<ProcessAssignment>> selectPage(Page<ProcessAssignment> page, ProcessAssignmentParam param);

    Result<Integer> updateProcessAssignmentStatusByParam(UpPaStatus up);

    Result<Integer> approveTaskByParam(ApproveParam param);
}
