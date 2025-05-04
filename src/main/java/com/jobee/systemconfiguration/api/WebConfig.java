package com.jobee.systemconfiguration.api;

import com.jobee.systemconfiguration.api.handlers.IdentityHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final IdentityHandler identityHandler;

    public WebConfig(IdentityHandler identityHandler) {
        this.identityHandler = identityHandler;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(identityHandler)
                .addPathPatterns("/**")
                .excludePathPatterns("/settings/health");
    }
}
