package com.walmart.aex.sp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

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
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("com.walmart.aex.sp"))
				.paths(PathSelectors.any()).build();
	}
}
