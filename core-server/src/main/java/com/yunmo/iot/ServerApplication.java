package com.yunmo.iot;

import com.yunmo.core.UserGrpcConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.zalando.problem.spring.web.autoconfigure.security.ProblemSecurityAutoConfiguration;

@SpringBootApplication(exclude = ProblemSecurityAutoConfiguration.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableCaching
@Import(UserGrpcConfig.class)
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ServerApplication.class);
        app.run(args);
    }
}
