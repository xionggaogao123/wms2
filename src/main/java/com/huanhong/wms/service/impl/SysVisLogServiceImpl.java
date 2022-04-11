package com.huanhong.wms.service.impl;

import com.huanhong.wms.entity.SysVisLog;
import com.huanhong.wms.mapper.SysVisLogMapper;
import com.huanhong.wms.service.ISysVisLogService;
import com.huanhong.wms.SuperServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统访问日志表 服务实现类
 * </p>
 *
 * @author liudeyi
 * @since 2022-04-12
 */
@Service
public class SysVisLogServiceImpl extends SuperServiceImpl<SysVisLogMapper, SysVisLog> implements ISysVisLogService {

}
