package com.huanhong.wms.service.impl;

import com.huanhong.wms.entity.SysOpLog;
import com.huanhong.wms.mapper.SysOpLogMapper;
import com.huanhong.wms.service.ISysOpLogService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统操作日志表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-04-12
 */
@Service
public class SysOpLogServiceImpl extends SuperServiceImpl<SysOpLogMapper, SysOpLog> implements ISysOpLogService {

}
