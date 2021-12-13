package com.huanhong.wms.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.huanhong.common.interceptor.LogInterceptor;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.MultipartConfigElement;
import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * WEB 初始化相关配置
 *
 * @author 刘德宜 wudihaike@vip.qq.com
 * @date 2018/1/11 14:27
 */
@SpringBootConfiguration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 拦截器
     *
     * @param registry 注册表
     * @author 刘德宜 wudihaike@vip.qq.com
     * @since 2018/4/8 16:14
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 日志拦截器
        registry.addInterceptor(new LogInterceptor());
    }

    /**
     * 自定义参数数据转换器
     *
     * @param converters 转换器
     * @author 刘德宜 wudihaike@vip.qq.com
     * @since 2018/1/11 14:27
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (int i = converters.size() - 1; i >= 0; i--) {
            if (converters.get(i) instanceof MappingJackson2HttpMessageConverter) {
                converters.remove(i);
            }
        }
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

        // 自定义fastjson配置
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(
                SerializerFeature.WriteMapNullValue,                // 是否输出值为null的字段,默认为false,我们将它打开
//                SerializerFeature.WriteNullListAsEmpty,             // 将Collection类型字段的字段空值输出为[]
//              SerializerFeature.WriteNullStringAsEmpty,           // 将字符串类型字段的空值输出为空字符串
//                SerializerFeature.WriteNullNumberAsZero,            // 将数值类型字段的空值输出为0
                SerializerFeature.DisableCircularReferenceDetect    // 禁用循环引用
        );
        config.setDateFormat("yyyy-MM-dd HH:mm:ss");
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        converter.setFastJsonConfig(config);

        converters.add(converter);
    }

    /**
     * 文件上传配置
     *
     * @author 刘德宜 wudihaike@vip.qq.com
     * @since 2018/4/8 16:16
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 临时上传文件路径
        // factory.setLocation("/data/file/tmp");
        // 单个数据大小
        factory.setMaxFileSize(DataSize.ofMegabytes(10));
        /// 总上传数据大小
        factory.setMaxRequestSize(DataSize.ofMegabytes(20));
        return factory.createMultipartConfig();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
