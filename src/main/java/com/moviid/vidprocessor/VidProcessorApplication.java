package com.moviid.vidprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
@EnableAsync
@SpringBootApplication
public class VidProcessorApplication {
	public static void main(String[] args) {
		SpringApplication.run(VidProcessorApplication.class, args);
	}

}