package com.hyperspacegamepanel;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@SpringBootApplication
@ComponentScan(basePackages = { "com.hyperspacegamepanel.*" })
@EnableScheduling
@EnableCaching
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

  @Bean
  public CacheManager cacheManager() {
    ConcurrentMapCacheManager mgr = new ConcurrentMapCacheManager();
    mgr.setCacheNames(Arrays.asList("machineInfo"));
    return mgr;
  }

}
