package com.jobee.systemconfiguration;

import com.jobee.systemconfiguration.infrastructure.AppInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;


@SpringBootApplication
public class SystemConfigurationApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SystemConfigurationApplication.class, args);

        AppInitializer appInitializer = context.getBean(AppInitializer.class);
        appInitializer.initialize();
    }
}
