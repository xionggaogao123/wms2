package com.huanhong.wms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanhong.wms.bean.ErrorCode;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Message;
import com.huanhong.wms.entity.ProcessAssignment;
import com.huanhong.wms.entity.User;
import com.huanhong.wms.entity.dto.AddMessageDTO;
import com.huanhong.wms.entity.param.ProcessAssignmentParam;
import com.huanhong.wms.mapper.MessageMapper;
import com.huanhong.wms.mapper.ProcessAssignmentMapper;
import com.huanhong.wms.mapper.UserMapper;
import com.huanhong.wms.service.IMessageService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 消息表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-23
 */
@Service
public class MessageServiceImpl extends SuperServiceImpl<MessageMapper, Message> implements IMessageService {

    @Resource
    private MessageMapper messageMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ProcessAssignmentMapper processAssignmentMapper;

    @Override
    public Result<Integer> addMessage(AddMessageDTO dto) {
        if (dto.getUserId() == null) {
            return Result.failure("请选择消息接收人");
        }
        User user = userMapper.selectById(dto.getUserId());
        if (user == null) {
            return Result.failure("消息接收人不存在");
        }

        Message message = new Message();
        BeanUtil.copyProperties(dto, message);
        message.setStatus(0);
        message.setUserName(user.getUserName());
        message.setUserId(user.getId());

        if (StrUtil.isNotBlank(dto.getTaskId())) {
            ProcessAssignment processAssignment = processAssignmentMapper.selectList(new QueryWrapper<ProcessAssignment>().eq("task_id", dto.getTaskId()).eq("status", 0)).get(0);
            if (processAssignment == null) {
                return Result.failure("该任务不存在");
            }
            message.setDocumentNumber(processAssignment.getDocumentNumber());
            message.setObjectId(processAssignment.getObjectId());
            message.setObjectType(processAssignment.getObjectType());
            message.setPlanClassification(processAssignment.getPlanClassification());
            message.setProcessInstanceId(processAssignment.getProcessInstanceId());
            int add = messageMapper.insert(message);
            if (add > 0) {
                return Result.success();
            }
        } else {
            //TODO 根据不同计划类型及id查询该计划相关信息
            if (dto.getObjectType() == null || dto.getObjectType() < 1 || dto.getObjectType() > 6) {
                return Result.failure(ErrorCode.PARAM_ERROR, "计划类型有误");
            }
            int add = messageMapper.insert(message);
            if (add > 0) {
                return Result.success();
            }
        }
        return Result.failure(ErrorCode.SYSTEM_ERROR, "系统异常，请稍后重试");
    }
}
