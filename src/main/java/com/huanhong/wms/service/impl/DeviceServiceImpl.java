package com.huanhong.wms.service.impl;

import com.huanhong.wms.entity.Device;
import com.huanhong.wms.mapper.DeviceMapper;
import com.huanhong.wms.service.IDeviceService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 设备表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-03-29
 */
@Service
public class DeviceServiceImpl extends SuperServiceImpl<DeviceMapper, Device> implements IDeviceService {

}
