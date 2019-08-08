package com.blackonwhite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class BlackOnWhiteGameApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlackOnWhiteGameApplication.class, args);
	}

}
