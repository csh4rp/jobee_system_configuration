package com.jobee.systemconfiguration.application;

import com.jobee.systemconfiguration.application.validators.SettingValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApplicationConfiguration implements WebMvcConfigurer {

    @Bean
    public SettingValidator settingUpsertDTOValidator() {
        return new SettingValidator();
    }
}
