package com.management.student_center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StudentCenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentCenterApplication.class, args);
	}

}
