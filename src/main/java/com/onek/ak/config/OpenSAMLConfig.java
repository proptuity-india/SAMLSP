package com.onek.ak.config;


import org.opensaml.core.config.InitializationService;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenSAMLConfig {

	static {
        try {
            InitializationService.initialize();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing OpenSAML", e);
        }
    }
}
