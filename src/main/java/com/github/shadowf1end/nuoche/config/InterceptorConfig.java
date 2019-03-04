package com.github.shadowf1end.nuoche.config;

import com.github.shadowf1end.nuoche.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Exrickx
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final IgnoredUrlsConfig ignoredUrlsConfig;
    private final UserInterceptor userInterceptor;

    @Autowired
    public InterceptorConfig(IgnoredUrlsConfig ignoredUrlsConfig, UserInterceptor userInterceptor) {
        this.ignoredUrlsConfig = ignoredUrlsConfig;
        this.userInterceptor = userInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器
        InterceptorRegistration userIr = registry.addInterceptor(userInterceptor);
        // 配置拦截的路径
        userIr.addPathPatterns("/**");
        // 配置不拦截的路径
        userIr.excludePathPatterns(ignoredUrlsConfig.getIgnoredUrls());
    }
}
