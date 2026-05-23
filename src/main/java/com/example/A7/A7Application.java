package com.example.A7;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
		org.springframework.cloud.function.context.config.ContextFunctionCatalogAutoConfiguration.class
})
@MapperScan("com.example.A7.Mapper")
public class A7Application {

	public static void main(String[] args) {
		SpringApplication.run(A7Application.class, args);
	}

}
