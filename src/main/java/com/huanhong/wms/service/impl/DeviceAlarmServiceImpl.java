package com.huanhong.wms.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Device;
import com.huanhong.wms.entity.DeviceAlarm;
import com.huanhong.wms.entity.SublibraryManagement;
import com.huanhong.wms.entity.WarehouseManagement;
import com.huanhong.wms.entity.dto.hik.CommunityEventAlarm;
import com.huanhong.wms.entity.dto.hik.HikMqMessage;
import com.huanhong.wms.mapper.DeviceAlarmMapper;
import com.huanhong.wms.mapper.DeviceMapper;
import com.huanhong.wms.mapper.SublibraryManagementMapper;
import com.huanhong.wms.mapper.WarehouseManagementMapper;
import com.huanhong.wms.service.IDeviceAlarmService;
import com.huanhong.wms.SuperServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 设备报警表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-04-01
 */
@Slf4j
@Service
public class DeviceAlarmServiceImpl extends SuperServiceImpl<DeviceAlarmMapper, DeviceAlarm> implements IDeviceAlarmService {

    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private SublibraryManagementMapper sublibraryManagementMapper;
    @Autowired
    private WarehouseManagementMapper warehouseManagementMapper;

    @Override
    public Result add(List<HikMqMessage> list) {
        log.info("海康云眸消息：{}", list);
        List<DeviceAlarm> deviceAlarms = new ArrayList<>();
        for (HikMqMessage data : list) {
            String msgId = data.getMsgId();
            int count = count(Wrappers.<DeviceAlarm>lambdaQuery().eq(DeviceAlarm::getMessageId, msgId));
            if (count > 0) {
                // 已经消费过了
                log.warn("已经消费过了：{}", data);
                continue;
            }
            String msgType = data.getMsgType();
            DeviceAlarm deviceAlarm = new DeviceAlarm();
            deviceAlarm.setMessageId(msgId);
            deviceAlarm.setMsgType(msgType);
            deviceAlarm.setContent(JSON.toJSONString(data.getContent()));
            switch (msgType) {
                case "community_event_alarm":
                    // 报警事件订阅消息
                    CommunityEventAlarm communityEventAlarm = (CommunityEventAlarm) data.getContent();
                    String deviceId = communityEventAlarm.getDeviceId();
                    Device device = deviceMapper.selectOne(Wrappers.lambdaQuery(new Device()).eq(Device::getOtherId, deviceId).last("limit 1"));
                    if (null == device) {
                        // 设备不存在
                        log.warn("设备不存在：{}", data);
                        continue;
                    }
                    deviceAlarm.setDeviceNo(device.getDeviceNo());
                    deviceAlarm.setDeviceName(device.getDeviceName());
                    deviceAlarm.setSublibraryId(device.getSublibraryId());
                    deviceAlarm.setWarehouseId(device.getWarehouseId());
                    deviceAlarm.setEventDescription(communityEventAlarm.getEventDescription());
                    String name = "";
                    // 根据设备库 id 查询库名称
                    if (StrUtil.isNotBlank(device.getSublibraryId())) {
                        SublibraryManagement sublibraryManagement = sublibraryManagementMapper.selectOne(Wrappers.<SublibraryManagement>lambdaQuery().eq(SublibraryManagement::getSublibraryId, device.getSublibraryId()).last("limit 1"));
                        if (null != sublibraryManagement) {
                            name = sublibraryManagement.getSublibraryName();
                        }
                    } else if (StrUtil.isNotBlank(device.getWarehouseId())) {
                        WarehouseManagement warehouseManagement = warehouseManagementMapper.selectOne(Wrappers.<WarehouseManagement>lambdaQuery().eq(WarehouseManagement::getWarehouseId, device.getWarehouseId()).last("limit 1"));
                        if (null != warehouseManagement) {
                            name = warehouseManagement.getWarehouseName();
                        }
                    }
                    deviceAlarm.setWarehouseName(name);
                    break;
                default:
                    log.warn("该消息类型系统暂未处理：{}", data);
                    if (true) {
                        continue;
                    }
                    break;
            }
            deviceAlarms.add(deviceAlarm);

        }
        if(deviceAlarms.size() > 0){
            boolean f = saveBatch(deviceAlarms);
            if (!f) {
                log.warn("数据插入失败");
                return Result.failure("数据插入失败");
            }
        }
        return Result.success();
    }
}
