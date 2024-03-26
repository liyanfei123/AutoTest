package com.testframe.autotest.core.config;

import com.testframe.autotest.interceptor.LoginInterceptor;
import com.testframe.autotest.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public LoginInterceptor getLoginInterceptor() {
        return new LoginInterceptor();
    }

    @Autowired
    private UserInterceptor userInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        String[] addPathPatterns = {
//                "/test/*",
                "/autotest/scene/create"
        };
        String[] excludePathPatterns = {};

        registry.addInterceptor(getLoginInterceptor())
                .addPathPatterns(addPathPatterns).excludePathPatterns(excludePathPatterns);

        registry.addInterceptor(userInterceptor)
                .addPathPatterns(addPathPatterns).excludePathPatterns(excludePathPatterns);
    }
}
