package com.Penske.CodeProducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
public class CodeProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeProducerApplication.class, args);
	}

}
