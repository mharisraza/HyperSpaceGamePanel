package com.hyperspacegamepanel;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = { "com.hyperspacegamepanel.*" })
@EnableScheduling
public class HyperSpaceGamePanelApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(HyperSpaceGamePanelApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// default settings here.
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

}
