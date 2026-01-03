package com.pet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@PropertySource(value = {"jdbc.properties"},ignoreResourceNotFound = true)
@EnableJpaAuditing
public class PetApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetApplication.class, args);
	}

}
