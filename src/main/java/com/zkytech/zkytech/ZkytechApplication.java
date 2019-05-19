package com.zkytech.zkytech;

import com.zkytech.zkytech.listener.MySessionListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.util.unit.DataSize;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
@EnableJpaAuditing
@EnableWebSecurity
@EnableSwagger2
public class ZkytechApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZkytechApplication.class, args);
    }

    /**
     * 文件上传配置
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //单个文件最大
        factory.setMaxFileSize(DataSize.ofMegabytes(10L));
        /// 设置总上传数据总大小
        factory.setMaxRequestSize(DataSize.ofMegabytes(10L));
        return factory.createMultipartConfig();
    }

    @Bean
    public ServletListenerRegistrationBean<MySessionListener> servletListenerRegistrationBean() {
        ServletListenerRegistrationBean<MySessionListener> slrBean = new ServletListenerRegistrationBean<MySessionListener>();
        slrBean.setListener(new MySessionListener());
        return slrBean;
    }

}
