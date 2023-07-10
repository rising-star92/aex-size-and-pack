package com.walmart.aex.sp;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@ComponentScan("com.walmart.platform.txn.springboot.filters")
@ComponentScan("com.walmart.platform.txn.springboot.interceptor")
@SpringBootApplication(scanBasePackages = {
        "com.walmart.aex.sp",
        "io.strati.tunr.utils.client"
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info().title("aex-size-and-pack service")
                .description("This page lists all APIs of Size-and-Pack Service")
                .termsOfService("http://swagger.io/terms/")
                .license(new License().name("Apache 2.0")
                        .url("http://springdoc.org")));
    }
}
