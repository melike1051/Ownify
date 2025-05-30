package com.ownify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login", "/register", "/reset-password", 
                                "/about", "/faqs", "/customer-support", 
                                "/social/**", "/ads/**", "/api/**",
                                "/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin().disable()
            .logout().disable();
        
        return http.build();
    }
}