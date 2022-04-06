package com.huanhong.wms.service;

import com.huanhong.wms.bean.Result;
import com.huanhong.wms.entity.Device;
import com.huanhong.wms.entity.dto.hik.DeviceGroup;
import com.huanhong.wms.entity.dto.hik.HikMqMessage;

import java.util.List;

public interface HikCloudService {

    String getToken();

    Result getEzvizToken();

    Result createConsumerGroup();

    Result<List<HikMqMessage>> consumerMessage(String consumerId, Boolean autoCommit);

    Result consumerOffset(String consumerId);

    void consumerMessage();


    Result createDeviceGroup(DeviceGroup deviceGroup);
    Result getDeviceGroup(String groupNo);

    Result createDevice(Device device);
    Result getDevice(String deviceSerial);
}
