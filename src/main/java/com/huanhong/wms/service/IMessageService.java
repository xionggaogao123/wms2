package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Message;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.dto.AddMessageDTO;

/**
 * <p>
 * 消息表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-23
 */
public interface IMessageService extends SuperService<Message> {

    Result<Integer> addMessage(AddMessageDTO dto);
}
