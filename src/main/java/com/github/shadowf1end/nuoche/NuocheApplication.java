package com.github.shadowf1end.nuoche;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author su
 */
@SpringBootApplication
@EnableJpaAuditing
public class NuocheApplication {

    public static void main(String[] args) {
        SpringApplication.run(NuocheApplication.class, args);
    }
}
