package com.subtracker.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // https://docs.spring.io/spring-security/reference/5.8/migration/servlet/config.html#use-new-requestmatchers
        // URLs should from most restrictive to the least restrictive
        return http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers("/api/**").authenticated()
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/**").permitAll()
                        // All request will be authenticated
                        // .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        // REST API don't need session management
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt())
                .build();
    }

}
