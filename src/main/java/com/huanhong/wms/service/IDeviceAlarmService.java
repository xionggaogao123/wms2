package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.DeviceAlarm;
import com.huanhong.wms.SuperService;
import com.huanhong.wms.entity.dto.hik.HikMqMessage;

import java.util.List;

/**
 * <p>
 * 设备报警表 服务类
 * </p>
 *
 * @author liudeyi
 * @since 2022-04-01
 */
public interface IDeviceAlarmService extends SuperService<DeviceAlarm> {

    Result add(List<HikMqMessage> data);
}
