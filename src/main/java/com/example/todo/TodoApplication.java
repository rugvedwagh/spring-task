package com.example.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
public class TodoApplication {

	public static void main(String[] args) {
		// Force JVM timezone to UTC before ANY Spring or database code runs
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		SpringApplication.run(TodoApplication.class, args);
	}
}