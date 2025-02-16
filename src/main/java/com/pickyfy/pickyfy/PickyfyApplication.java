package com.pickyfy.pickyfy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

// TODO: 비동기 처리를 위한 @EnableAsync 활성화 필요
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class PickyfyApplication {

	public static void main(String[] args) {
		SpringApplication.run(PickyfyApplication.class, args);
	}

}
