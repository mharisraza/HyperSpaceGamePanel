package com.hyperspacegamepanel;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;



@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = {"com.hyperspacegamepanel.*"})
public class HyperSpaceGamePanelApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(HyperSpaceGamePanelApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	 @Bean
     public CacheManager cacheManager() {
         return new ConcurrentMapCacheManager("machineInfoCache");
     }

}
