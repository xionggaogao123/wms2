package com.huanhong.wms.config;

import com.huanhong.common.aop.OperateLogAop;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AopConfig {

    /**
     * 日志切面
     */
    @Bean
    public OperateLogAop operateLogAop(){
        return new OperateLogAop();
    }
}
