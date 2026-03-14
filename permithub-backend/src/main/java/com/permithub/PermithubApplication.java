package com.permithub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PermithubApplication {
    public static void main(String[] args) {
        SpringApplication.run(PermithubApplication.class, args);
    }
}
