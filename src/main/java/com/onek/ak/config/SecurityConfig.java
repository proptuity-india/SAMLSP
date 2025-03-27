package com.onek.ak.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/dashboard.html", "/css/**", "/js/**", "/images/**").permitAll() // ✅ Allow static files
                        .requestMatchers("/saml/login", "/saml/acs", "api/user/profile").permitAll() // ✅ Allow SAML endpoints
                        .anyRequest().authenticated() // Require authentication for everything else
                )
                .csrf(AbstractHttpConfigurer::disable) // ✅ Disable CSRF (needed for SAML POST)
                .formLogin(AbstractHttpConfigurer::disable) // Disable default login
                .httpBasic(AbstractHttpConfigurer::disable); // Disable HTTP Basic Auth

        return http.build();
    }
}


