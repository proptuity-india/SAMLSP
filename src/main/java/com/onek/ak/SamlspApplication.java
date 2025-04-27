package com.onek.ak;

import org.opensaml.core.config.InitializationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static junit.framework.TestCase.assertTrue;

@SpringBootApplication
public class SamlspApplication {

    public static void main(String[] args) {
        try {
            InitializationService.initialize();  // Initialize OpenSAML
        } catch (Exception e) {
            throw new RuntimeException("Error initializing OpenSAML", e);
        }
        SpringApplication.run(SamlspApplication.class, args);
    }
}
