package com.huanhong.wms.config;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.MybatisMapWrapperFactory;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ldy81
 * @date 2019/12/11 17:44
 */
@Configuration
@MapperScan("com.huanhong.wms.mapper")
public class MybatisPlusConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInterceptor.setLimit(100);
        return paginationInterceptor;
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return new MybatisPlusCustomizers();
    }

    class MybatisPlusCustomizers implements ConfigurationCustomizer {
        @Override
        public void customize(org.apache.ibatis.session.Configuration configuration) {
            //*注册Map 下划线转驼峰*
            configuration.setObjectWrapperFactory(new MybatisMapWrapperFactory());
        }
    }

    @Bean
    public OptimisticLockerInterceptor optimisticLockerInterceptor(){
        return new OptimisticLockerInterceptor();
    }

}
