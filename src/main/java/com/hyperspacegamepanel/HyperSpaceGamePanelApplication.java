package com.hyperspacegamepanel;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

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
	public SecurityContextLogoutHandler securityContextLogoutHandler() {
		return new SecurityContextLogoutHandler();
	}

}
